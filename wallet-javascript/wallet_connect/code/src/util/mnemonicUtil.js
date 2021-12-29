const bip39 = require("bip39");
const hdkey = require("hdkey");

export default class MnemonicUtil {
    // 生成秘钥的路径
    static hdPath = "m/44'/60'/0'/0/0";


    /**
     * 通过到ms的时间戳生成一个16进制的字符串
     * @returns 
     */
    static GenHexString() {
        let curTime = new Date().getTime();
        return curTime.toString(16);
    }

    /**
     * 验证助记词是否有效
     * @param {助记词} mnemonic
     * @returns
     */
    static ValidateMnemonic(mnemonic) {
        let seedWords = mnemonic.split(" ");

        seedWords = seedWords.filter(word => {
            word = word.trim();

            return !!word;
        });

        return seedWords.length === 12;
    }
    /**
     *
     * @param {助记词} mnemonic
     * @returns
     */
    static async GeneratePrivateKeyByMnemonic(mnemonic) {
        // 这里先把助记词分割, 防止助记词之间有多个空格
        let seedWords = mnemonic.split(" ");
        //  然后再把分割好的单词 已一个空格合并成字符串
        mnemonic = seedWords.join(" ");

        let seed = await bip39.mnemonicToSeed(mnemonic);

        const hdSeed = hdkey.fromMasterSeed(seed);

        const privateKey = hdSeed.derive(MnemonicUtil.hdPath).privateKey.toString("hex");

        return privateKey;
    }
}
