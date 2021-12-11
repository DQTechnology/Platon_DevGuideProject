<template>
    <div class="main-outer-page">
        <div class="main-page vertical-only-layout">
            <div class="horzontal-layout flex-center ">
                <header-bar />
                <span class="flex-1"></span>
                <div class="network-component horzontal-layout flex-center">
                    <span style="background:#e91550" class="circle-dot"></span>
                    <span class="network-name">PlatON测试网</span>
                </div>
            </div>
            <div class="vertical-layout main-container ">
                <div class="horzontal-layout menu-bar ">
                    <div class="flex-1"></div>
                    <div class="vertical-layout flex-1 flex-center ">
                        <span class="account">{{ accountName }}</span>
                        <span class="address">{{ address }}</span>
                    </div>
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
                </div>

                <div class="transaction-record-container" v-if="tabIndex === 0">
                    <transaction-table :address="address" />
                </div>

                <div class="transaction-record-container" v-if="tabIndex === 1">
                    <pending-transaction-table :address="address" />
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
import accountDetailInfo from "@/component/account-detail-info.vue";

import { mapState } from "vuex";
export default {
    components: {
        headerBar,
        transactionTable,
        pendingTransactionTable,
        accountDetailInfo
    },
    computed: {
        ...mapState(["isOpenInTab"])
    },
    data() {
        return {
            bShowAccountDlg: false,
            accountName: "",
            address: "",
            lat: 0,
            tabIndex: 0
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
                    this.$router.push("/select-action-page.vue");
                }, 3000);
                return;
            }

            this.accountName = res.accountName;
            this.address = res.address;
            // 获取钱包的余额
            this.getBalanceOf();
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
    // width: 100%;
    // height: 100%;

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
        border-radius: 82px;
        width: 126px;
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
    .main-container {
        box-shadow: 0 0 7px #00000014;
        background: white;
        .menu-bar {
            padding: 0 8px;
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
            box-shadow: 0 3px 4px rgb(135 134 134 / 16%);
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
