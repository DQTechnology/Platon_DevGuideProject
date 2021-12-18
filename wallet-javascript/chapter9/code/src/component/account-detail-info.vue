<template>
    <div class="account-detail-info">
        <div v-show="!showExport">
            <div class="horzontal-layout flex-center" style="margin-bottom:16px;">
                <span style="font-size:18px;color: black;"> {{ accountName }}</span>
            </div>
            <div class="horzontal-layout flex-center" style="margin-bottom:16px;">
                <div class="qrCode" ref="qrCodeDiv"></div>
            </div>

            <div class="horzontal-layout flex-center" style="margin-bottom:16px;">
                <el-input v-model="address" style=" width: 286px;" />
            </div>

            <div class="horzontal-layout flex-center" style="margin-bottom:6px;">
                <el-button class="export-btn" type="primary" @click="showExport = true"
                    >导出私钥</el-button
                >
            </div>
        </div>
        <div v-show="showExport">
            <div class="horzontal-layout" style="margin-bottom:16px;">
                <span
                    style="color: black;"
                    @click="showExport = false"
                    class="pointer el-icon-arrow-left"
                    >返回</span
                >
            </div>

            <div class="horzontal-layout flex-center" style="margin-bottom:16px;">
                <span style="font-size:18px;color: black;"> {{ accountName }}</span>
            </div>
            <div class="horzontal-layout flex-center" style="margin-bottom:16px;">
                <el-input v-model="address" style=" width: 286px;" />
            </div>
            <div class="horzontal-layout flex-center" style="margin-bottom:16px;">
                <span style="font-size:18px;color: black;"> 显示私钥</span>
            </div>

            <template v-if="!privateKey">
                <div class="horzontal-layout " style="margin-bottom:6px;">
                    <span style="color: black;margin-left: 22px;"> 输入您的密码</span>
                </div>
                <div class="horzontal-layout flex-center" style="margin-bottom:16px;">
                    <el-input type="password" v-model="password" style=" width: 286px;" />
                </div>
            </template>
            <template v-else>
                <div class="horzontal-layout " style="margin-bottom:6px;">
                    <span style="color: black;margin-left: 22px;"> 这是您的私钥</span>
                </div>
                <div class="horzontal-layout flex-center">
                    <el-input
                        class="private-key-password-display-textarea"
                        type="textarea"
                        v-model="privateKey"
                        rows="2"
                        resize="none"
                    />
                </div>
            </template>

            <div class="horzontal-layout flex-center">
                <div class="private-key-password-warning">
                    注意：永远不要公开这个私钥。任何拥有你的私钥的人都可以窃取你帐户中的任何资产。
                </div>
            </div>

            <div class="horzontal-layout flex-center" style="margin-bottom:6px;">
                <el-button class="bar-btn" @click="showExport = false"> 取消</el-button>

                <el-button class="bar-btn" type="primary" @click="onExportPrivateKey"
                    >确认</el-button
                >
            </div>
        </div>
    </div>
</template>

<script>
import QRCode from "qrcodejs2";
export default {
    props: {
        accountName: {
            type: String,
            required: true
        },
        address: {
            type: String,
            required: true
        }
    },
    data() {
        return {
            showExport: false,
            password: "",
            privateKey: ""
        };
    },
    watch: {
        address() {
            this.showQRCode();
        }
    },
    mounted() {
        this.showQRCode();
    },
    methods: {
        /**
         * 导出秘钥
         */
        async onExportPrivateKey() {
            let res = await this.digging.PrivateKeyManager.ExportPrivateKey(
                this.accountName,
                this.password
            );
            if (res.errCode !== 0) {
                this.$message.error(res.errMsg);
                return;
            }
            console.log(res.data);
            this.privateKey = res.data.privateKey.substring(2);
        },
        showQRCode() {
            new QRCode(this.$refs.qrCodeDiv, {
                text: this.address, // 需要转换为二维码的内容
                width: 164,
                height: 164,
                colorDark: "#000000",
                colorLight: "#ffffff",
                correctLevel: QRCode.CorrectLevel.H
            });
        }
    }
};
</script>

<style lang="less" scoped>
.account-detail-info {
    //     width: 360px;
    .export-btn {
        width: 286px;
    }
    .bar-btn {
        width: 140px;
    }
    .private-key-password-warning {
        border-radius: 8px;
        background-color: #fff6f6;
        font-size: 12px;
        font-weight: 500;
        line-height: 15px;
        color: #e91550;
        width: 256px;
        margin-bottom: 16px;
        padding: 9px 15px;
        margin-top: 16px;
        font-family: Roboto;
    }
    .private-key-password-display-textarea {
        width: 286px;
        textarea {
            line-height: 21px;
            color: #e91550 !important;
        }
    }
}
</style>
