<template>
    <div class="select-action-page vertical-only-layout">
        <header-bar />
        <div class="select-action-container horzontal-layout">
            <div
                class="
          select-action-btn
          vertical-only-layout
          flex-horzontal-center flex-1
        "
                style="margin-right: 16px"
            >
                <div class="el-icon-download icon"></div>
                <div class="select-action__button-text-big">
                    不，我已经有一个种子密语了。
                </div>
                <div class="select-action__button-text-small">
                    请使用 12 字种子密语导入现有钱包
                </div>
                <el-button class="action-btn" type="primary" @click="onJumpToImportSeedPhrase"
                    >导入钱包</el-button
                >
            </div>
            <div
                class="
          select-action-btn
          vertical-only-layout
          flex-horzontal-center flex-1
        "
            >
                <div class="el-icon-plus icon"></div>
                <div class="select-action__button-text-big">是的。立即开始设置！</div>
                <div class="select-action__button-text-small">
                    该操作将为您创建新钱包和种子密语
                </div>
                <el-button class="action-btn" type="primary" @click="onJumpToCreatePwd"
                    >创建钱包</el-button
                >
            </div>
        </div>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
export default {
    components: {
        headerBar
    },
    mounted() {
        this.digging.PrivateKeyManager.HasAccount().then(bHas => {
            if (bHas) {
                this.$router.push("/main");
            }
        });
    },
    methods: {
        onJumpToImportSeedPhrase() {
            this.$router.push("/import-seed-phrase");
        },
        onJumpToCreatePwd() {
            this.$router.push("/create-password");
        }
    }
};
</script>

<style lang="less" scoped>
.select-action-page {
    margin: 2% auto 0 auto;
    width: 820px;
    justify-content: flex-start;

    .select-action-container {
        margin-top: 100px;

        .select-action-btn {
            height: 278px;
            border-radius: 10px;
            border: 1px solid #d8d8d8;
            .icon {
                font-size: 60px;
                margin-top: 20px;
                margin-bottom: 20px;
            }
            .select-action__button-text-big {
                font-style: normal;
                font-weight: normal;
                line-height: 28px;
                font-size: 20px;
                color: #000;
                margin-top: 12px;
                text-align: center;
            }
            .select-action__button-text-small {
                font-style: normal;
                font-weight: normal;
                line-height: 20px;
                font-size: 14px;
                color: #7a7a7b;
                margin-top: 10px;
                text-align: center;
            }
            .action-btn {
                width: 184px;
                font-weight: 500;
                margin: 35px 0 14px;
            }
        }
    }
}
</style>
