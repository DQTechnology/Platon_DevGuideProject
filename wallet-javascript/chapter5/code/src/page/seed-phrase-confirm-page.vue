<template>
    <div class="seed-phrase-confirm-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>请确认您的私密备份密语</page-title>
        <div class="text-block">请选择每一个片语，以确保片语正确性。</div>

        <div class="grid-layout select-seed-words">
            <el-button class="seed-word" v-for="(item, index) in selectWords" :key="index">
              {{ item.word }}
            </el-button>
        </div>
        <div class="grid-layout">
            <el-button
                class="seed-word"
                :type="getBtnType(item.index)"
                v-for="(item, index) in seedWords"
                :key="index"
                @click="onSelectWord(item)"
            >
                {{ item.word }}
            </el-button>
        </div>
        <el-button type="primary" class="confirm-btn" :disabled="disableBtn" @click="onConfirm"
            >确认</el-button
        >
    </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import pageTitle from "@/component/page-title.vue";
import { mapState } from "vuex";

import MnemonicUtil from "@/util/mnemonicUtil.js";

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
            seedWords: [], // 等待被选择的助记词
            selectWords: [], // 已经被选择的助记词
            orginSeedWords: [], // 未被打算的助记词
            seedWordsMap: {} // 记录助记词选中状态
        };
    },
    mounted() {
        if (!this.mnemonic) {
            let mnemonic = bip39.generateMnemonic();
            this.$store.commit("SetMnemonic", mnemonic);
        }
        // 保存正确顺序的助记词
        let splitSeedWords = this.mnemonic.split(" ");

        this.orginSeedWords = [];
        // 这里给每一个单词排序号,避免有重复单词智能选择一个的问题
        for (let i = 0; i < splitSeedWords.length; ++i) {
            this.orginSeedWords.push({
                index: i,
                word: splitSeedWords[i]
            });
        }

        // 打算助记词的顺序, 这里 [...this.orginSeedWords] 复制数组

        this.seedWords = this.shuffle([...this.orginSeedWords]);
    },

    methods: {
        onSelectWord(item) {
            // 判断单词是否被选中, 如果为选中则恢复为未选中
            if (this.seedWordsMap[item.index]) {
                this.seedWordsMap[item.index] = false;
                let newSelectWords = [];

                this.selectWords.forEach(ele => {
                    if (ele.index === item.index) {
                        return;
                    }
                    newSelectWords.push(ele);
                });
                this.selectWords = newSelectWords;
            } else {
                // 选中单词
                this.seedWordsMap[item.index] = true;
                this.selectWords.push(item);
            }
            // 计算集配的数量
            let matchWordNum = 0;
            for (let i = 0; i < this.selectWords.length; ++i) {
                let orginWord = this.orginSeedWords[i];
                let selectedWord = this.selectWords[i];

                if (orginWord.word === selectedWord.word) {
                    ++matchWordNum;
                } else {
                    break;
                }
            }
            // 如果全部匹配那么设置确认按钮为可用状态
            this.disableBtn = matchWordNum !== this.orginSeedWords.length;

            this.$forceUpdate();
        },
        getBtnType(index) {
            if (this.seedWordsMap[index]) {
                return "primary";
            }
            return "";
        },
        /**
         * 打乱数组
         */
        shuffle(seedWords) {
            let m = seedWords.length;
            let t;
            let i;
            while (m) {
                i = Math.floor(Math.random() * m--);
                t = seedWords[m];
                seedWords[m] = seedWords[i];
                seedWords[i] = t;
            }
            return seedWords;
        },
        async onConfirm() {
            let privateKey = await MnemonicUtil.GeneratePrivateKeyByMnemonic(this.mnemonic);

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
            this.$message.success("创建秘钥成功!");
        },
        onGoBack() {
            this.$router.push("/seed-phrase");
        }
    }
};
</script>

<style lang="less" scoped>
.seed-phrase-confirm-page {
    margin: 2% auto 0 auto;
    width: 742px;
    justify-content: flex-start;
    .go-back {
        cursor: pointer;
        font-weight: bold;
        margin: 0px 0 30px 0;
        font-size: 20px;
    }
    .text-block {
        margin-bottom: 24px;
        color: black;
    }
    .grid-layout {
        display: grid;
        grid-template-columns: repeat(auto-fill, 142px);
        grid-template-rows: repeat(auto-fill, 50px);
        min-height: 150px;
        max-width: 575px;
    }
    .select-seed-words {
        border: 1px solid #cdcdcd;
        border-radius: 6px;
        padding: 12px;
        background-color: #fff;
        margin: 24px 0 20px;
    }

    .seed-word {
        margin: 6px;
        padding: 8px 18px;
        height: 41px;
    }

    .confirm-btn {
        margin-top: 20px;
        width: 170px;
        height: 44px;
    }
}
</style>
