<template>
    <div class="restore-vault-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>使用种子密语恢复个人账户</page-title>
        <div class="text-block">
            输入12位助记词以恢复钱包。
        </div>
        <el-form ref="importForm" :model="restoreInfo">
            <el-form-item
                prop="mnemonic"
                label="钱包助记词"
                :rules="[
                    {
                        required: true,
                        message: '助记词为12个单词',
                        validator: validator.ValidateMnemonic,
                        trigger: 'change'
                    }
                ]"
            >
                <el-input
                    type="textarea"
                    class="pwd-input"
                    rows="3"
                    resize="none"
                    placeholder="用空格分开每个单词"
                    v-model="restoreInfo.mnemonic"
                >
                </el-input>
            </el-form-item>
            <el-form-item
                prop="newPassword"
                label="新密码(至少8个字符)"
                :rules="[
                    {
                        required: true,
                        message: '请输入新密码(至少8个字符)',
                        validator: validator.ValidatePassword
                    }
                ]"
            >
                <el-input
                    class="pwd-input"
                    type="password"
                    v-model="restoreInfo.newPassword"
                    placeholder="请输入新密码(至少8个字符)"
                    :minlength="8"
                />
            </el-form-item>
            <el-form-item
                prop="confirmPassword"
                label="确认密码"
                :rules="[
                    {
                        required: true,
                        message: '请再次输入密码',
                        validator: validator.ValidatePassword
                    }
                ]"
            >
                <el-input
                    class="pwd-input"
                    type="password"
                    v-model="restoreInfo.confirmPassword"
                    placeholder="请再次输入密码"
                    :minlength="8"
                />
            </el-form-item>
            <el-form-item>
                <el-button class="import-btn" type="primary" @click="onImport">恢复</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import pageTitle from "@/component/page-title.vue";

import MnemonicUtil from "@/util/mnemonicUtil.js";
export default {
    components: {
        headerBar,
        pageTitle
    },
    data() {
        return {
            restoreInfo: {
                mnemonic: "",
                newPassword: "",
                confirmPassword: ""
            }
        };
    },
    mounted() {
        this.digging.PrivateKeyManager.HasAccount().then(bHas => {
            if (bHas) {
                this.$router.push("/main");
            }
        });
    },
    methods: {
        /**
         * 点击创建按钮后,指定的函数
         */
        onImport() {
            this.$refs.importForm.validate(vaild => {
                if (!vaild) {
                    return;
                }
                // 去掉密码的两边空格
                let newPassword = this.restoreInfo.newPassword.trim();
                let confirmPassword = this.restoreInfo.confirmPassword.trim();

                if (newPassword !== confirmPassword) {
                    this.$message.error("两次密码不一致");
                    return;
                }
                // 执行创建创建密码的动作
                this.doImport(newPassword);
            });
        },

        async doImport(newPassword) {
            // 先保存好密码
            await this.digging.PasswordManager.CreatePassword(newPassword);
            //
            //  生成私钥
            let privateKey = await MnemonicUtil.GeneratePrivateKeyByMnemonic(
                this.restoreInfo.seedPhrase
            );

            let defaultAccount = "账号 1";
            // 调用PrivateKeyManager的StorePrivateKey接口保存私钥
            // 这里创建第一个账号默认为账号 1
            // 设置未强制导入, 这样可以把之前存在的账号一次性清空
            let res = await this.digging.PrivateKeyManager.StorePrivateKey(
                defaultAccount,
                privateKey,
                true
            );
            if (res.errCode !== 0) {
                this.$message.error(res.errMsg);
                return;
            }
            // 保存成功后, 把账号切换为刚刚导入的账号
            res = await this.digging.PrivateKeyManager.SwitchAccount(defaultAccount);
            if (res.errCode !== 0) {
                this.$message.error(res.errMsg);
                return;
            }
            this.$message.success("恢复成功!");
        },
        onGoBack() {
            this.$router.go(-1);
        }
    }
};
</script>

<style lang="less" scoped>
.restore-vault-page {
    margin: 2% auto 0 auto;
    width: 820px;
    justify-content: flex-start;

    .pwd-input {
        width: 350px;
        display: block;
    }
    .go-back {
        cursor: pointer;
        font-weight: bold;
        margin: 0px 0 30px 0;
        font-size: 20px;
    }
    .import-btn {
        width: 170px;
        height: 44px;
    }
}
</style>
