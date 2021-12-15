<template>
    <div class="delegate-lat-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>委托LAT</page-title>
        <el-form ref="delegateForm" :model="delegateInfo">
            <el-form-item prop="nodeName" label="节点名">
                <el-input disabled class="send-input" v-model="delegateInfo.nodeName" />
            </el-form-item>

            <el-form-item prop="nodeId" label="节点id">
                <el-input disabled class="send-input" v-model="delegateInfo.nodeId" />
            </el-form-item>

            <el-form-item
                prop="lat"
                label="委托数量"
                :rules="[
                    {
                        required: true,
                        message: '请输入要委托的LAT数量',
                        validator: ValidateLAT
                    }
                ]"
            >
                <el-input
                    class="send-input"
                    type="number"
                    v-model="delegateInfo.lat"
                    placeholder="请输入要委托的LAT数量(不少于10LAT)"
                />
            </el-form-item>

            <el-form-item label="所需手续费">
                <el-input disabled class="send-input" v-model="delegateInfo.gasfee" />
            </el-form-item>

            <el-form-item>
                <div class="horzontal-layout">
                    <span class="flex-1"></span>
                    <el-button class="create-btn" @click="onGoBack">取消</el-button>
                    <el-button class="create-btn" type="primary" @click="onDelegate"
                        >委托</el-button
                    >
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
            delegateInfo: {
                nodeName: this.$route.query.nodeName,
                nodeId: this.$route.query.nodeId,
                lat: 0,
                gasfee: 0
            }
        };
    },

    async mounted() {
        this.delegateInfo.gasfee = (await this.digging.TransactionManager.CalcGasUsed()) + " Lat";
    },
    methods: {
        ValidateLAT(rule, value, callback) {
            try {
                let lat = parseInt(this.delegateInfo.lat);
                if (lat <= 0) {
                    callback("lat数量必须大于10");
                    return;
                }
                callback();
            } catch (e) {
                callback(new Error("请输入合法的lat数量"));
            }
        },

        onDelegate() {
            this.$refs.delegateForm.validate(vaild => {
                if (!vaild) {
                    return;
                }
                this.doDelegate();
            });
        },
        /**
         * 执行发送的动作
         */
        async doDelegate() {
            let reply = await this.digging.DelegateManager.Deletgate(
                this.delegateInfo.nodeId,
                this.delegateInfo.lat
            );
            console.log(reply);
            // let res = await this.digging.TransactionManager.SendLATTO(
            //     this.sendInfo.lat,
            //     this.account,
            //     this.sendInfo.toAddress
            // );
            // if (res.errCode !== 0) {
            //     this.$message.error("发送失败!");
            //     return;
            // }
            // this.$message.success("发送成功!");
            // this.onGoBack();
        },
        onGoBack() {
            this.$router.go(-1);
        }
    }
};
</script>

<style lang="less" scoped>
.delegate-lat-page {
    margin: 2% auto 0 auto;
    width: 320px;
    justify-content: flex-start;

    .send-input {
        width: 320px;
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
