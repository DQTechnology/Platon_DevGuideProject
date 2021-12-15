<template>
    <div class="seed-phrase-page vertical-only-layout">
        <header-bar />
        <div class="horzontal-layout">
            <div class="seed-phrase-main">
                <page-title>私密备份密语</page-title>
                <div class="text-block">
                    您的个人私密备份密语可以帮助您轻松备份和恢复个人账户。
                </div>
                <div class="text-block">
                    警告：切勿向他人透露您的备份密语。任何人一旦持有该密语，即可取走您的币。
                </div>
                <div class="reveal-seed-phrase-secret">
                    <div class="secret-words" ref="secretWords">
                        <!-- 显示助记词 -->
                        {{ mnemonic }}
                    </div>
                    <div class="secret-blocker" v-if="!hideBlock" @click="onHideBlock">
                        <div class="el-icon-lock"></div>
                        <div class="reveal-txt">点击此处显示密语</div>
                    </div>
                </div>

                <div class="horzontal-layout" style="margin-top: 16px">
                    <!-- <el-button class="seed-phrase-btn">稍后提醒</el-button> -->
                    <!-- 让两个按钮靠两边 -->
                    <span class="flex-1"></span>
                    <el-button
                        class="seed-phrase-btn"
                        type="primary"
                        :disabled="disableBtn"
                        @click="jumpToConfirmPage"
                        >下一步</el-button
                    >
                </div>
            </div>
            <div class="seed-phrase-aside">
                <div class="text-block">提示:</div>
                <div class="text-block">通过如 1Password 等密码管家保存该密语。</div>
                <div class="text-block">
                    请将该密语记录在纸上，并保存在安全的地方。如果希望提升信息安全性，请将信息记录在多张纸上，并分别保存在
                    2 - 3 个不同的地方。
                </div>
                <div class="text-block">记住该密语。</div>
                <div class="download-btn" @click="onSaveAsText">
                    下载私密备份密语，并使用外部加密硬盘或存储媒介妥善保管。
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import pageTitle from "@/component/page-title.vue";
const bip39 = require("bip39");

import MnemonicUtil from "@/util/mnemonicUtil.js";

import { mapState } from "vuex";

export default {
    components: {
        headerBar,
        pageTitle
    },
    computed: {
        ...mapState(["mnemonic"])
    },
    data() {
        return {
            disableBtn: true, //
            hideBlock: false
        };
    },
    mounted() {
        this.digging.PrivateKeyManager.HasAccount().then(bHas => {
            if (bHas) {
         
            this.$router.push("/main");
            } else {
                this.generateMnemonic();
            }
        });
    },
    methods: {
        onHideBlock() {
            this.hideBlock = true;
            this.$refs.secretWords.style.filter = "blur(0px)";
            this.disableBtn = false;
        },
        generateMnemonic() {
            if (this.mnemonic) {
                return;
            }
            let mnemonic = bip39.generateMnemonic();

            // 生成助记词
            this.$store.commit("SetMnemonic", mnemonic);
        },
        /**
         * 把助记词保存成txt文本
         */
        onSaveAsText() {
            let blob = new Blob([this.mnemonic]);

            let link = document.createElement("a");

            link.href = window.URL.createObjectURL(blob);
            link.download = MnemonicUtil.GenHexString() + ".txt";
            link.click();
            //释放内存
            window.URL.revokeObjectURL(link.href);
        },

        jumpToConfirmPage() {
            this.$store.commit("SetMnemonic", this.mnemonic);

            this.$router.push("/seed-phrase-confirm");
        }
    }
};
</script>

<style lang="less" scoped>
.seed-phrase-page {
    margin: 2% auto 0 auto;
    width: 742px;
    justify-content: flex-start;

    .seed-phrase-main {
        flex: 3;
    }

    .download-btn {
        cursor: pointer;
        color: #409eff;
        &:hover {
            color: #66b1ff;
        }
    }

    .reveal-seed-phrase-secret {
        position: relative;
        display: flex;
        justify-content: center;
        border: 1px solid #cdcdcd;
        border-radius: 6px;
        background-color: #fff;
        padding: 18px;
        margin-top: 36px;
        .secret-words {
            width: 310px;
            font-size: 1.25rem;
            text-align: center;
            filter: blur(5px);
        }
        .secret-blocker {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
            background-color: rgba(0, 0, 0, 0.6);
            display: flex;
            flex-flow: column nowrap;
            align-items: center;
            justify-content: center;
            padding: 8px 0 18px;
            cursor: pointer;
        }
        .el-icon-lock {
            color: #fff;
            font-size: 30px;
        }
        .reveal-txt {
            color: #fff;
            font-size: 0.75rem;
            font-weight: bold;
            text-transform: uppercase;
            margin-top: 8px;
            text-align: center;
        }
    }
    .seed-phrase-aside {
        flex: 2;
        margin-left: 81px;
        .text-block {
            margin-bottom: 24px;
            color: #5a5a5a;
        }
    }

    .seed-phrase-btn {
        width: 170px;
        height: 44px;
    }
}
</style>
