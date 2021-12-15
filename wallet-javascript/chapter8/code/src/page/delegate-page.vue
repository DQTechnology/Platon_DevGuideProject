<template>
    <div class="send-lat-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>委托</page-title>
        <el-form ref="sendForm" :model="sendInfo">
            <el-form-item
                prop="toAddress"
                label="接收地址"
                :rules="[
                    {
                        required: true,

                        validator: ValidateAddress
                    }
                ]"
            >
                <el-input
                    class="send-input"
                    v-model="sendInfo.toAddress"
                    placeholder="请输入接收地址"
                />
            </el-form-item>
            <el-form-item
                prop="lat"
                label="LAT数量"
                :rules="[
                    {
                        required: true,
                        message: '请输入要发送的LAT数量',
                        validator: ValidateLAT
                    }
                ]"
            >
                <el-input
                    class="send-input"
                    type="number"
                    v-model="sendInfo.lat"
                    placeholder="请输入要发送的LAT数量"
                />
            </el-form-item>

            <el-form-item label="所需手续费">
                <el-input disabled class="send-input" v-model="sendInfo.gasfee" />
            </el-form-item>

            <el-form-item>
                <div class="horzontal-layout">
                    <span class="flex-1"></span>
                    <el-button class="create-btn" @click="onGoBack">取消</el-button>

                    <el-button class="create-btn" type="primary" @click="onSend">发送</el-button>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import pageTitle from "@/component/page-title.vue";
export default {
    components: {
        headerBar,
        pageTitle
    },
    data() {
        return {
            account: this.$route.query.account,
            sendInfo: {
                toAddress: "",
                lat: 0,
                gasfee: 0
            }
        };
    },

    async mounted() {
        this.sendInfo.gasfee = (await this.digging.TransactionManager.CalcGasUsed()) + " Lat";
    },
    methods: {
        ValidateAddress(rule, value, callback) {
            if (!this.digging.TransactionManager.IsBech32Address(this.sendInfo.toAddress)) {
                callback(new Error("请输入合法的地址"));
                return;
            }
            callback();
        },
        ValidateLAT(rule, value, callback) {
            try {
                let lat = parseInt(this.sendInfo.lat);
                if (lat <= 0) {
                    callback("lat数量必须大于0");
                    return;
                }
                callback();
            } catch (e) {
                callback(new Error("请输入合法的lat数量"));
            }
        },

        onSend() {
            this.$refs.sendForm.validate(vaild => {
                if (!vaild) {
                    return;
                }
                this.doSend();
            });
        },
        /**
         * 执行发送的动作
         */
        async doSend() {
            let res = await this.digging.TransactionManager.SendLATTO(
                this.sendInfo.lat,
                this.account,
                this.sendInfo.toAddress
            );
            if (res.errCode !== 0) {
                this.$message.error("发送失败!");
                return;
            }

            this.$message.success("发送成功!");
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
