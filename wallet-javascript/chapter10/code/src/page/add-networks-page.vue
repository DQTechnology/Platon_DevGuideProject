<template>
    <div class="add-networks-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>添加网络</page-title>
        <el-form ref="networkForm" :model="networkInfo">
            <el-form-item
                prop="name"
                label="网络名"
                :rules="[
                    {
                        required: true,
                        message: '请输入网络名'
                    }
                ]"
            >
                <el-input class="send-input" v-model="networkInfo.name" />
            </el-form-item>

            <el-form-item
                prop="rpcUrl"
                label="新增RPC URL"
                :rules="[
                    {
                        required: true,
                        message: '请输入RPC URL'
                    }
                ]"
            >
                <el-input class="send-input" v-model="networkInfo.rpcUrl" />
            </el-form-item>
            <el-form-item
                prop="chainId"
                label="ChainId"
                :rules="[
                    {
                        required: true,
                        message: '请输入chainId'
                    }
                ]"
            >
                <el-input class="send-input" v-model="networkInfo.chainId" />
            </el-form-item>

            <el-form-item
                prop="browserUrl"
                label="浏览器地址"
                :rules="[
                    {
                        required: true,
                        message: '浏览器地址'
                    }
                ]"
            >
                <el-input class="send-input" v-model="networkInfo.browserUrl" />
            </el-form-item>

            <el-form-item>
                <div class="horzontal-layout">
                    <span class="flex-1"></span>
                    <el-button class="create-btn" @click="onGoBack">取消</el-button>
                    <el-button class="create-btn" type="primary" @click="onAdd">添加</el-button>
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
            networkInfo: {
                name: "",
                rpcUrl: "",
                chainId: "",
                browserUrl: ""
            }
        };
    },

    async mounted() {},
    methods: {
        onAdd() {
            this.$refs.networkForm.validate(vaild => {
                if (!vaild) {
                    return;
                }
                this.doAdd();
            });
        },
        /**
         * 执行发送的动作
         */
        async doAdd() {
            let res = await this.digging.NetworkManager.AddNetwork(this.networkInfo);
            if (res.errCode !== 0) {
                this.$message.error(res.errMsg);
                return;
            }

            this.$message.success("添加成功!");
            this.onGoBack();
        },
        onGoBack() {
            this.$router.go(-1);
        }
    }
};
</script>

<style lang="less" scoped>
.add-networks-page {
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
