<template>
    <div class="send-lat-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>发送LAT</page-title>
        <el-form ref="sendForm" :model="sendInfo">
            <el-form-item prop="toAddress" label="接收地址">
                <el-input
                    class="send-input"
                    v-model="sendInfo.toAddress"
                    placeholder="请输入接收地址"
                />
            </el-form-item>
            <el-form-item prop="lat" label="LAT数量">
                <el-input
                    class="send-input"
                    type="number"
                    v-model="sendInfo.lat"
                    placeholder="请输入要发送的LAT数量"
                />
            </el-form-item>

            <el-form-item>
                <div class="horzontal-layout">
                    <span class="flex-1"></span>
                    <el-button class="create-btn" @click="onGoBack">取消</el-button>

                    <el-button class="create-btn" type="primary" @click="doSend">发送</el-button>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import pageTitle from "@/component/page-title.vue";

import { mapState } from "vuex";

export default {
    components: {
        headerBar,
        pageTitle
    },
    data() {
        return {
            sendInfo: {
                toAddress: "",
                lat: 0,
                gasfee: 0
            }
        };
    },
    computed: {
        ...mapState(["connector"])
    },
    mounted() {
        // 连接没建立
        if (!this.connector || !this.connector.connected) {
            this.$message.error("未建立连接!");
            this.onGoBack();
            return;
        }
    },
    methods: {
        /**
         * 执行发送的动作
         */
        async doSend() {
           
            let web3Ins = new Web3(new Web3.providers.HttpProvider("http://35.247.155.162:6789"));

            const { accounts } = this.connector;
            // 这里要转成lat账号才可以获取到交易数量
            let latAddress = web3Ins.utils.toBech32Address("lat", accounts[0]);

             // 使用交易数量做nonce
            let nonce = await web3Ins.platon.getTransactionCount(latAddress);
            // 获取当前的手续费用
            let gasPrice = await web3Ins.platon.getGasPrice();
            // wallectconnect只支持16进制的地址,因此需要把lat的地址转换
            let toHexAddress = web3Ins.utils.decodeBech32Address(this.sendInfo.toAddress);

            this.connector
                .sendTransaction({
                    data: "",
                    from: accounts[0],
                    gasLimit: 21000,
                    gasPrice: gasPrice,
                    nonce: nonce,
                    to: toHexAddress,
                    value: web3Ins.utils.toVon(this.sendInfo.lat, "lat")
                })
                .then(txHash => {
                    // 发送交易成功， 钱包会返回交易hash
                    console.log("txHash: ", txHash);
                });

            this.onGoBack();
        },
        onGoBack() {
            this.$router.go(-1);
        }
    }
};
</script>

<style lang="less" scoped>
.send-lat-page {
    margin: 2% auto 0 auto;
    width: 300px;
    justify-content: flex-start;

    .send-input {
        width: 300px;
        display: block;
    }
    .go-back {
        cursor: pointer;
        font-weight: bold;
        margin: 0px 0 30px 0;
        font-size: 20px;
    }
    .create-btn {
        margin-left: 20px;
        // width: 170px;
        // height: 44px;
    }
}
</style>
