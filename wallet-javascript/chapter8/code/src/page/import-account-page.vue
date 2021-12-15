<template>
    <div class="import-account-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>导入账户</page-title>
        <div class="desc-text">
            导入的账户将不会与最初创建的 Digging 账户助记词相关联。
        </div>
        <div class="horzontal-layout " style="margin-bottom:6px;">
            <span style="color: black;">请输入账号名:</span>
        </div>
        <div class="horzontal-layout flex-center" style="margin-bottom: 10px;">
            <el-input class="private-key-password-display-textarea" v-model="accountName" />
        </div>

        <div class="horzontal-layout " style="margin-bottom:6px;">
            <span style="color: black;">请粘贴您的私钥:</span>
        </div>
        <div class="horzontal-layout flex-center">
            <el-input
                class="private-key-password-display-textarea"
                type="textarea"
                v-model="privateKey"
                rows="3"
                resize="none"
            />
        </div>

        <div class="horzontal-layout flex-center" style="margin-top:20px;">
            <el-button round class="import-btn" @click="onGoBack">取消</el-button>
            <span class="flex-1"></span>
            <el-button round class="import-btn " type="primary" @click="onImport">创建</el-button>
        </div>
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
            accountName: "",
            privateKey: ""
        };
    },
    async mounted() {},

    methods: {
        onGoBack() {
            this.$router.go(-1);
        },
        async onImport() {
            if (!this.accountName) {
                this.$message.error("请输入账号名");
                return;
            }

            if (!this.privateKey) {
                this.$message.error("请输入秘钥");
                return;
            }

            let res = await this.digging.PrivateKeyManager.StorePrivateKey(
                this.accountName,
                this.privateKey,
                false
            );

            if (res.errCode !== 0) {
                this.$message.error(res.errMsg);
                return;
            }

            this.$message.success("导入账户成功!");
        }
    }
};
</script>

<style lang="less" scoped>
.import-account-page {
    margin: 2% auto 0 auto;
    width: 375px;
    justify-content: flex-start;
    padding: 0 10px;
    .desc-text {
        color: #808080;
        font-size: 14px;
        line-height: 140%;
        padding-bottom: 10px;
        margin-bottom: 10px;
        border-bottom: 1px solid #ececec;
    }
    .go-back {
        cursor: pointer;
        font-weight: bold;
        margin: 0px 0 30px 0;
        font-size: 20px;
    }
    .import-btn {
        width: 150px;
    }
    .private-key-password-display-textarea {
        textarea {
            line-height: 21px;
        }
    }
}
</style>
