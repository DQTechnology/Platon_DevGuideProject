export default class Validator {
    static ValidatePassword(rule, value, callback) {
        if (!value || value.length < 8) {
            callback(new Error(rule.message));
            return;
        }
        callback();
    }
}