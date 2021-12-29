import MnemonicUtil from "@/util/mnemonicUtil.js";

export default class Validator {
    static ValidatePassword(rule, value, callback) {
        if (!value || value.length < 8) {
            callback(new Error(rule.message));
            return;
        }
        callback();
    }

    static ValidateMnemonic(rule, value, callback) {
        if (!value) {
            callback(new Error("助记词为12个单词"));
            return;
        }
       

        if (!MnemonicUtil.ValidateMnemonic(value)) {
            callback(new Error("助记词为12个单词"));
            return;
        }

        callback();
    }
}
