
const SUCCESS = 0; // 执行成功
const ERROR = 1; // 执行失败
const WARNNING = 2; // 警告

/**
 * 密码管理类
 */
class PasswordManager {
    // 密码解锁后,把密码保存在这里
    static password = "";

    static IsUnlock() {
        return !!PasswordManager.password;
    }
    /**
     * 判断密码是否存在
     * @returns 
     */
    static async IsPasswordExist() {
        let password = await PasswordManager.getPassword();

        return !!password;

    }
    /**
     * 获取密码
     * @returns password
     */
    static getPassword() {
        return new Promise((resolve) => {
            chrome.storage.local.get(['password'], (res) => {
                // errCode为0是表示
                console.log(res);
                let password = res['password'];
                resolve(password);
            });
        });
    }

    /**
     * 创建密码
     * @param {新密码} newPassword 
     * @returns 
     */
    static CreatePassword(newPassword) {
        return new Promise((resolve) => {
            // 使用 chrome.storage存储数据,把key设置为password
            chrome.storage.local.set({
                "password": newPassword
            }, () => {

                // errCode为0是表示
                resolve({ errCode: SUCCESS, errMsg: "" });

            });
        });
    }
    /**
     * 判断密码是否正确
     * @param {密码} inputPassword 
     */
    static async UnLock(inputPassword) {
        let password = await PasswordManager.getPassword();
        if (inputPassword === password) {
            PasswordManager.password = password;
            return true;
        }
        return false;
    }
    /**
     * 更新密码
     * @param {旧密码} oldPassword 
     * @param {新密码} newPassword 
     */
    static async UpdatePassword(oldPassword, newPassword) {
        let isMatch = await PasswordManager.UnLock(oldPassword);
        if (!isMatch) {
            return {
                errCode: ERROR,
                errMsg: "旧密码不正确"
            }
        }
        // 调用创建密码函数,覆盖旧密码
        return await PasswordManager.CreatePassword(newPassword);
    }
}


// 导出PasswordManager
window.digging = {
    PasswordManager
}