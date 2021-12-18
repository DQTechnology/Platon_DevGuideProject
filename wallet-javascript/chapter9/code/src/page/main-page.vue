<template>
    <div class="main-outer-page">
        <div class="main-page vertical-only-layout">
            <div class="horzontal-layout flex-center ">
                <header-bar />
                <span class="flex-1"></span>

                <el-dropdown trigger="click">
                    <div class="network-component  horzontal-layout flex-center">
                        <span style="background:#e91550" class="circle-dot"></span>
                        <span class="network-name">{{ curNetworkName }}</span>
                        <i class="el-icon-arrow-down"></i>
                    </div>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                            @click.native="onSwitchNetwork(item)"
                            v-for="(item, index) in networkList"
                            :key="index"
                            >{{ item }}</el-dropdown-item
                        >
                        <div class="line"></div>
                        <el-dropdown-item @click.native="onAddNetwork">添加网络</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>

                <el-popover placement="bottom" width="320" trigger="click">
                    <account-menu ref="accountMenu" @on-switch-account="onSwitchAccount" />
                    <div class="account-select-btn vertical-layout flex-center" slot="reference">
                        <img src="@/assets/little_dog.jpg" />
                    </div>
                </el-popover>
            </div>
            <div class="vertical-layout main-container ">
                <div class="horzontal-layout menu-bar ">
                    <div class="flex-1"></div>

                    <el-tooltip effect="dark" :content="copyText" placement="bottom">
                        <div
                            class="account-bar vertical-layout flex-1 flex-center pointer"
                            @mouseleave="onTipLeave"
                            @click="onCopyAccount"
                        >
                            <span class="account">{{ accountName }}</span>
                            <span class="address">{{ showAddress }}</span>
                        </div>
                    </el-tooltip>

                    <div class="flex-1 horzontal-layout flex-center">
                        <span class="flex-1"></span>

                        <el-dropdown trigger="click">
                            <i title="账户选项" class="el-icon-more more-btn"></i>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item
                                    icon="el-icon-bank-card"
                                    @click.native="bShowAccountDlg = true"
                                    >账户详情</el-dropdown-item
                                >
                            </el-dropdown-menu>
                        </el-dropdown>
                    </div>
                </div>
                <div class="wallet-overview vertical-layout-center">
                    <img src="@/assets/logo.png" class="logo" />

                    <div class="horzontal-layout flex-center currency-container">
                        <span class="currency" style="margin-right:6px;">{{ lat }}</span>
                        <span class="currency">LAT</span>
                    </div>
                    <el-button type="primary" round @click="onJumpToSendLatPage">
                        <i class="el-icon-position"></i>
                        发送</el-button
                    >
                </div>

                <div class="horzontal-layout tab-bar">
                    <div
                        @click="onChangeTab(0)"
                        class="flex-1 horzontal-layout  flex-center tab"
                        :class="`${tabIndex === 0 ? 'active' : ''}`"
                    >
                        交易记录
                    </div>
                    <div
                        @click="onChangeTab(1)"
                        class="flex-1 horzontal-layout flex-center tab"
                        :class="`${tabIndex === 1 ? 'active' : ''}`"
                    >
                        交易列表
                    </div>

                    <div
                        @click="onChangeTab(2)"
                        class="flex-1 horzontal-layout flex-center tab"
                        :class="`${tabIndex === 2 ? 'active' : ''}`"
                    >
                        验证节点
                    </div>
                    <div
                        @click="onChangeTab(3)"
                        class="flex-1 horzontal-layout flex-center tab"
                        :class="`${tabIndex === 3 ? 'active' : ''}`"
                    >
                        我的委托
                    </div>
                </div>

                <div class="transaction-record-container" v-if="tabIndex === 0">
                    <transaction-table :address="address" ref="txTable" />
                </div>

                <div class="transaction-record-container" v-if="tabIndex === 1">
                    <pending-transaction-table :address="address" ref="pendingTxTable" />
                </div>
                <div class="transaction-record-container" v-if="tabIndex === 2">
                    <validator-table ref="validatorTable" />
                </div>
                <div class="transaction-record-container" v-if="tabIndex === 3">
                    <my-delegate-table ref="myDelegateTable" />
                </div>
            </div>
        </div>
        <el-dialog
            v-if="bShowAccountDlg"
            width="360px"
            :visible.sync="bShowAccountDlg"
            append-to-body
        >
            <account-detail-info :accountName="accountName" :address="address" />
        </el-dialog>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import transactionTable from "@/component/transaction-table.vue";
import pendingTransactionTable from "@/component/pending-transaction-table.vue";
import accountMenu from "@/component/account-menu.vue";
import validatorTable from "@/component/validator-table.vue";
import accountDetailInfo from "@/component/account-detail-info.vue";

import myDelegateTable from "@/component/my-delegate-table.vue";

import { mapState } from "vuex";
export default {
    components: {
        headerBar,
        transactionTable,
        pendingTransactionTable,
        accountDetailInfo,
        validatorTable,
        accountMenu,
        myDelegateTable
    },
    computed: {
        ...mapState(["isOpenInTab"])
    },
    data() {
        return {
            bShowAccountDlg: false,
            accountName: "",
            address: "",
            showAddress: "",
            lat: 0,
            tabIndex: 0,
            curNetworkName: "",
            networkList: [],

            copyText: "复制到剪贴板"
        };
    },
    mounted() {
        //先默认在tab打开
        if (!this.isOpenInTab) {
            chrome.tabs.create({ url: "home.html#/main" });
        } else {
            this.init();
        }
    },
    methods: {
        async init() {
            // 判断当前是否有账号
            let bHas = await this.digging.PrivateKeyManager.HasAccount();
            if (!bHas) {
                // 没有的话跳转到选择页面
                this.$router.push("/select-action");
                return;
            }
            // 判断是否已经解锁
            let bHasUnLock = await this.digging.PasswordManager.IsUnlock();
            if (!bHasUnLock) {
                this.$router.push("/unlock");
                return;
            }
            this.getCurAccount();
            //
            this.curNetworkName = this.digging.NetworkManager.GetCurNetworkName();
            this.networkList = this.digging.NetworkManager.GetNetworkNameList();
        },
        onCopyAccount() {
            let oInput = document.createElement("input");

            oInput.value = this.address;
            document.body.appendChild(oInput);
            oInput.select(); // 选择对象;

            document.execCommand("Copy"); // 执行浏览器复制命令

            oInput.remove();
            this.copyText = "已复制";
        },
        onTipLeave() {
            this.copyText = "复制到剪贴板";
        },

        async onSwitchNetwork(item) {
            let res = await this.digging.NetworkManager.SwitchNetwork(item);
            if (res.errCode !== 0) {
                this.$message.error(res.errMsg);
                return;
            }
            this.curNetworkName = item;
            // 切换网络
            this.api.SwitchNetwork();
            // 加载表数据
            this.reloadTableData();
            this.$refs.accountMenu.ReloadData();

            this.getBalanceOf();
        },
        async onSwitchAccount(accountName) {
            let res = await this.digging.PrivateKeyManager.SwitchAccount(accountName);
            if (res.errCode !== 0) {
                this.$message.error(res.errMsg);
                return;
            }
            this.accountName = accountName;
            this.address = res.data;
            this.makeShowAddress();
            this.getBalanceOf();
        },

        reloadTableData() {
            if (this.tabIndex === 0) {
                this.$refs.txTable.ReloadData();
            } else if (this.tabIndex === 1) {
                this.$refs.pendingTxTable.ReloadData();
            } else if (this.tabIndex === 2) {
                this.$refs.validatorTable.ReloadData();
            } else if (this.tabIndex === 3) {
                this.$refs.myDelegateTable.ReloadData();
            }
        },

        makeShowAddress() {
            this.showAddress =
                this.address.substring(0, 6) +
                "..." +
                this.address.substring(this.address.length - 4);
        },
        /**
         * 显示当前钱包信息
         */
        async getCurAccount() {
            // 调用PrivateKeyManager获取当前选中钱包
            let res = await this.digging.PrivateKeyManager.GetAccountInfo();
            if (res === null) {
                this.$message.error("当前无账号,请导入钱包或者创建钱包");
                // 3s后跳转界面
                setTimeout(() => {
                    this.$router.push("/select-action");
                }, 3000);
                return;
            }

            this.accountName = res.accountName;
            this.address = res.address;

            this.makeShowAddress();
            // 获取钱包的余额
            this.getBalanceOf();
        },

        onAddNetwork() {
            this.$router.push("/add-networks");
        },

        /**
         * 获取lat
         */
        async getBalanceOf() {
            this.lat = await this.digging.TransactionManager.GetBalanceOf(this.address);
        },
        onChangeTab(tabIndex) {
            this.tabIndex = tabIndex;
        },
        /**
         *
         */
        onJumpToSendLatPage() {
            this.$router.push({
                path: "/send-lat",
                query: {
                    account: this.accountName
                }
            });
        }
    }
};
</script>

<style lang="less" scoped>
.main-outer-page {
    // padding: 2% 0 0 0;
    background: #f7f7f7;
    min-width: 317px;
    min-height: 500px;

    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
}
.main-page {
    margin: 2% auto 0 auto;
    min-width: 357px;
    max-width: 1200px;
    justify-content: flex-start;
    .network-component {
        cursor: pointer;
        border-radius: 82px;
        // width: 126px;
        padding: 0 10px;
        height: 26px;

        border: 2px solid #bbc0c5;
        font-size: 12px;
        color: #4d4d4d;
        font-weight: 500;
    }
    .circle-dot {
        border-radius: 50%;
        border: none;
        height: 12px;
        width: 12px;
    }
    .network-name {
        margin-left: 6px;
        margin-right: 6px;
    }
    .account-select-btn {
        cursor: pointer;
        margin-left: 10px;
        border-radius: 50%;
        padding: 3px;
        border: 2px solid #037dd6;
        img {
            border-radius: 50%;
            overflow: hidden;
            width: 32px;
            height: 32px;
        }
    }
    .main-container {
        box-shadow: 0 0 7px #00000014;
        background: white;
        .menu-bar {
            padding: 4px 8px;
            height: 56px;
            border-bottom: 1px solid #d6d9dc;
            .more-btn {
                cursor: pointer;
                transform: rotate(90deg);
                &:hover {
                    color: #66b1ff;
                }
            }
        }
        .account-bar {
            &:hover {
                background-color: #f2f3f4;
            }
        }
        .account {
            width: 100%;
            font-size: 1rem;
            font-weight: 500;
            line-height: 19px;
            color: #000;
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
            text-align: center;
            margin-bottom: 4px;
        }
        .address {
            margin-top: 4px;
            font-size: 0.75rem;
            line-height: 0.75rem;
            color: #989a9b;
        }
        .wallet-overview {
            padding-top: 10px;
            height: 209px;
            min-width: 0;
        }
        .currency-container {
            margin: 30px 0 24px 0;
            .currency {
                display: inline-block;
                font-size: 30px;
                color: black;
            }
        }
        .tab-bar {
            height: 52px;
            line-height: 52px;
            border-bottom: 1px solid #ececec;
            .tab {
                font-size: 14px;
                cursor: pointer;
                color: #6a737d;
                border-bottom: 2px solid transparent;
            }
            .active {
                color: #037dd6;
                border-bottom: 2px solid #037dd6;
            }
        }
    }
}
</style>
