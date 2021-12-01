<template>
  <div class="seed-phrase-confirm-page vertical-only-layout">
    <header-bar />
    <div class="go-back" @click="onGoBack">< Back</div>
    <page-title>请确认您的私密备份密语</page-title>
    <div class="text-block">请选择每一个片语，以确保片语正确性。</div>

    <div class="grid-layout select-seed-words">
      <el-button
        class="seed-word"
        v-for="(item, index) in selectWords"
        :key="index"
      >
        {{ item }}
      </el-button>
    </div>
    <div class="grid-layout">
      <el-button
        class="seed-word"
        :type="getBtnType(item)"
        v-for="(item, index) in seedWords"
        :key="index"
        @click="onSelectWord(item)"
      >
        {{ item }}
      </el-button>
    </div>
    <el-button
      type="primary"
      class="confirm-btn"
      :disabled="disableBtn"
      @click="onConfirm"
      >确认</el-button
    >
  </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
import pageTitle from "@/component/page-title.vue";
import { mapState } from "vuex";
const bip39 = require("bip39");
const hdkey = require("hdkey");

export default {
  components: {
    headerBar,
    pageTitle,
  },

  computed: {
    ...mapState(["mnemonic"]),
  },
  data() {
    return {
      disableBtn: true, //
      seedWords: [], // 等待被选择的助记词
      selectWords: [], // 已经被选择的助记词
      orginSeedWords: [], // 未被打算的助记词
      seedWordsMap: {}, // 记录助记词选中状态
    };
  },
  mounted() {
    if (!this.mnemonic) {
      let mnemonic = bip39.generateMnemonic();
      this.$store.commit("SetMnemonic", mnemonic);
    }
    // 保存正确顺序的助记词
    this.orginSeedWords = this.mnemonic.split(" ");
    // 打算助记词的顺序, 这里 [...this.orginSeedWords] 复制数组
    this.seedWords = this.shuffle([...this.orginSeedWords]);
  },
  methods: {


      

    onSelectWord(word) {
      // 判断单词是否被选中, 如果为选中则恢复为未选中
      if (this.seedWordsMap[word]) {
        this.seedWordsMap[word] = false;
        let newSelectWords = [];
        this.selectWords.forEach((ele) => {
          if (ele === word) {
            return;
          }
          newSelectWords.push(ele);
        });
        this.selectWords = newSelectWords;
      } else {
        // 选中单词
        this.seedWordsMap[word] = true;
        this.selectWords.push(word);
      }
      // 计算集配的数量
      let matchWordNum = 0;
      for (let i = 0; i < this.selectWords.length; ++i) {
        let orginWord = this.orginSeedWords[i];
        let selectedWord = this.selectWords[i];

        if (orginWord === selectedWord) {
          ++matchWordNum;
        } else {
          break;
        }
      }
      // 如果全部匹配那么设置确认按钮为可用状态
      this.disableBtn = matchWordNum !== this.orginSeedWords.length;

      this.$forceUpdate();
    },
    getBtnType(word) {
      if (this.seedWordsMap[word]) {
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
    onConfirm() {
      /**
       * 助记词转换为种子
       */
      bip39.mnemonicToSeed(this.mnemonic).then((seed) => {
        const hdSeed = hdkey.fromMasterSeed(seed);

        // 利用hdPath导出子私钥
        const hdPath = "m/44'/60'/0'/0/0"; // platon默认使用路径为0

        const privateKey = hdSeed.derive(hdPath).privateKey.toString("hex");

        console.log(privateKey);
        // 清空store里面的助记词
        this.$store.commit("SetMnemonic", "");
      });
    },
    onGoBack() {
      this.$router.push("/seed-phrase");
    },
  },
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