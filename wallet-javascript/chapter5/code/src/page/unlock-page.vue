<template>
    <div class="unlock-page vertical-only-layout">
        <header-bar />
        <div class="main vertical-layout flex-center">
            <img src="@/assets/logo.png" style="width: 120px; height: 120px" />
            <div class="header">欢迎回来！</div>
            <div class="description">分散网络待命中</div>

            <el-form
                ref="passwordForm"
                :model="passwordInfo"
                style="margin-top: 10px; width: 300px"
            >
                <el-form-item
                    prop="password"
                    label="密码"
                    :rules="[
                        {
                            required: true,
                            message: '请输入密码',
                            validator: validator.ValidatePassword
                        }
                    ]"
                >
                    <el-input
                        type="password"
                        v-model="passwordInfo.password"
                        placeholder="请输入密码"
                        :minlength="8"
                    />
                </el-form-item>

                <el-form-item class="horzontal-layout vertical-only-layout flex-center">
                    <el-button class="first-time-flow-btn" type="primary" @click="onUnLock"
                        >解锁</el-button
                    >
                </el-form-item>

                <div class="unlock-page__links" @click="onRestoreFromSeedPhrase">
                    <div style="color: #aeaeae;">从助记词还原</div>
                    <div class="unlock-page__link--import">使用帐号种子密语导入</div>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import { mapState } from "vuex";
export default {
    components: {
        headerBar
    },
    computed: {
        ...mapState(["isOpenInTab"])
    },
    data() {
        return {
            passwordInfo: {
                password: ""
            }
        };
    },
    methods: {
        onUnLock() {
            // 判断密码长度是否符合
            this.$refs.passwordForm.validate(vaild => {
                if (!vaild) {
                    return;
                }
                // 去掉密码的两边空格
                let password = this.passwordInfo.password.trim();
                this.doUnLock(password);
            });
        },
        async doUnLock(password) {
            // 解锁钱包
            let bSuccees = await this.digging.PasswordManager.UnLock(password);
            if (bSuccees) {
                this.$message.success("解锁成功!");
                // todo
                // 1, 解锁成功后,需要判断是否已经有钱包,如果还没钱包,则跳转到创建或者导入钱的页面
                // 2, 如果已经有钱包则跳转到主界面
                this.$router.push("/seed-phrase");
            } else {
                this.$message.error("密码错误,解锁失败!");
            }
        },
        onRestoreFromSeedPhrase() {
            if (this.isOpenInTab) {
                this.$router.push("/restore-vault");
            } else {
                chrome.tabs.create({ url: "index.html#/restore-vault" });
            }
        }
    }
};
</script>

<style lang="less" scoped>
.unlock-page {
    margin: 0 auto;
    max-width: 820px;
    .main {
        margin: 0 auto 0;
        width: 317px;
        padding: 30px;
        color: black;
    }
    .header {
        font-size: 28px;
        margin-bottom: 22px;
        margin-top: 30px;
        text-align: center;
    }
    .description {
        text-align: center;
        font-size: 16px;
    }
    .first-time-flow-btn {
        width: 300px;
        font-weight: 500;
    }
    .unlock-page__links {
        margin-top: 25px;
        width: 100%;
        cursor: pointer;
    }
    .unlock-page__link--import {
        color: #f7861c;
    }
}
</style>
