<template>
    <div v-if="isConnect" class="main-outer-page">
        <div class="main-page vertical-only-layout">
            <div class="horzontal-layout flex-center ">
                <header-bar />
                <span class="flex-1"></span>
            </div>
            <div class="vertical-layout main-container ">
                <div class="horzontal-layout menu-bar ">
                    <div class="flex-1"></div>

                    <div class="account-bar vertical-layout flex-1 flex-center pointer">
                        <span class="account">当前账号</span>
                        <span class="address">{{ address }}</span>
                    </div>

                    <span class="flex-1"></span>
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
            </div>
        </div>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
// 引入walletconnect的sdk
import WalletConnect from "@walletconnect/client";
import QRCodeModal from "@walletconnect/qrcode-modal";

import { mapState } from "vuex";
export default {
    components: {
        headerBar
    },
    computed: {
        ...mapState(["connector"])
    },
    data() {
        return {
            isConnect: false, //判断是否和aton连接,如果未连接不显示主页面
            address: "", // 地址
            lat: 0, // 当前余额
            web3Ins: null
        };
    },
    mounted() {
        let isOpenInTab = window.location.pathname !== "/popup.html";

        //先默认在tab打开
        if (!isOpenInTab) {
            chrome.tabs.create({ url: "home.html" });
        } else {
            this.init();
        }
    },
    methods: {
        init() {
            // 创建web的对象,用于获取余额和转换地址
            this.web3Ins = new Web3(new Web3.providers.HttpProvider("http://35.247.155.162:6789"));
            this.walletConnectInit();
        },

        async walletConnectInit() {
            // 构建wallet connector的session
            const bridge = "https://bridge.walletconnect.org";
            let connector = new WalletConnect({ bridge, qrcodeModal: QRCodeModal });
            if (!connector.connected) {
                await connector.createSession();
            }
            // 设置到store,让其他页面也可以使用connector
            this.$store.commit("SetConnector", connector);
            this.subscribeToEvents();
        },
        async subscribeToEvents() {
            // 订阅时间
            if (!this.connector) {
                return;
            }

            // 建立连接
            this.connector.on("connect", (error, payload) => {
                if (error) {
                    throw error;
                }
                this.onConnect();
            });
            // 连接断开
            this.connector.on("disconnect", (error, payload) => {
                console.log(`connector.on("disconnect")`, payload);
                if (error) {
                    throw error;
                }
                this.onDisconnect();
            });

            this.onConnect();
        },

        onConnect() {
            if (!this.connector.connected) {
                return;
            }

            const { chainId, accounts } = this.connector;
            let address = accounts[0];
            this.isConnect = true;
            // wallectconnect获取的地址都是16进制的,这里转成lat开头的地址
            this.address = this.web3Ins.utils.toBech32Address("lat", address);

            this.getBalanceOf();
        },
        async getBalanceOf() {
            // 获取钱包地址,这里需要使用lat开头的地址
            let balance = await this.web3Ins.platon.getBalance(this.address);
            this.lat = this.web3Ins.utils.fromVon(balance, "lat");
        },
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
