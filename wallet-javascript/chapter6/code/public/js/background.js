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
        return new Promise(resolve => {
            chrome.storage.local.get(["password"], res => {
                let password = res["password"];
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
        return new Promise(resolve => {
            // 使用 chrome.storage存储数据,把key设置为password
            chrome.storage.local.set(
                {
                    password: newPassword
                },
                () => {
                    // errCode为0是表示
                    resolve({ errCode: SUCCESS, errMsg: "" });
                }
            );
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
     * 获取传进来的密码
     * @returns
     */
    static GetPassPassword() {
        return PasswordManager.password;
    }
}

/**
 * 私钥管理类
 */
class PrivateKeyManager {
    // 存储私钥的键
    static privateKeyName = "privateKeys";
    // 存储当前选中的账号
    static curAccountKey = "currentAccount";
    /**
     *
     * @param {账号名} accountName 为了支持多个私钥,因为每个私钥都有一个名字
     * @param {私钥} privateKey
     * @param {是否强制导入} force 如果是强制导入的话, 会把之前所存储的私钥全部先删除,再执行导入
     */
    static async StorePrivateKey(account, privateKey, force) {
        if (!privateKey.startsWith("0x")) {
            privateKey = "0x" + privateKey;
        }

        // 强制导入的话,会把之前的私钥全部删除
        if (force) {
            chrome.storage.local.remove(PrivateKeyManager.privateKeyName);
        }
        // 不传账号,则设置默认的账号
        if (!account) {
            account = "账号 1";
        } else {
            // 取出账号两边的空格
            account = account.trim();
        }

        // 先获取之前所有的privateKey
        let privateKeys = await PrivateKeyManager.getPrivateKeys();

        // 判断账号是否已经存在, 若已经存在,不给创建
        if (privateKeys && privateKeys[account]) {
            return {
                errCode: ERROR,
                errMsg: "保存私钥失败: 账号已存在!"
            };
        }
        // 创建账号实例
        let ethAccount = new Web3EthAccounts(null, "lat");
        //
        //  通过PasswordManager 获取已经创建的密码
        let password = await PasswordManager.getPassword();
        // 加密的内容
        let keyStore = ethAccount.encrypt(privateKey, password);

        // 判断私有是否已经导入过, 如果已经导入则导入失败

        if (privateKeys) {
            let accounts = Object.keys(privateKeys);

            for (let i = 0; i < accounts.length; ++i) {
                let lastKeyStore = privateKeys[accounts[i]];

                if (keyStore.address === lastKeyStore.address) {
                    return {
                        errCode: ERROR,
                        errMsg: "保存私钥失败: 私钥已经存在!"
                    };
                }
            }
        } else {
            // 不存在, 说明是第一次导入
            privateKeys = {};
        }

        // 存储的privateKeys对象中
        privateKeys[account] = keyStore;
        // 持久化
        return await PrivateKeyManager.storePriateKeys(privateKeys);
    }

    /**
     * 删除账号
     * @param {}} account
     * @returns
     */
    static async DeleteAccount(account) {
        let privateKeys = await PrivateKeyManager.getPrivateKeys();
        // 当前没有私钥
        if (!privateKeys) {
            return {
                errCode: ERROR,
                errMsg: "删除失败: 账号不存在!"
            };
        }
        // 判断账号是否存在
        if (!privateKeys[account]) {
            return {
                errCode: ERROR,
                errMsg: "删除失败: 账号不存在!"
            };
        }
        // 删除秘钥
        delete privateKeys[account];
        // 持久化
        return await PrivateKeyManager.storePriateKeys(privateKeys);
    }

    /**
     * 导出加密后的文件
     * @param {账号名} account
     */
    static async ExportKeyStore(account) {
        let privateKeys = await PrivateKeyManager.getPrivateKeys();
        if (!privateKeys) {
            return {
                errCode: ERROR,
                errMsg: "导出失败: 账号不存在!"
            };
        }
        // 判断账号是否存在
        if (!privateKeys[account]) {
            return {
                errCode: ERROR,
                errMsg: "导出失败: 账号不存在!"
            };
        }

        return {
            errCode: SUCCESS,
            data: privateKeys[account]
        };
    }
    /**
     * 导出私钥
     * @param {} account
     * @param {*} password
     * @returns
     */
    static async ExportPrivateKey(account, password) {
        let privateKeys = await PrivateKeyManager.getPrivateKeys();
        if (!privateKeys) {
            return {
                errCode: ERROR,
                errMsg: "导出失败: 账号不存在!"
            };
        }

        // 判断账号是否存在
        if (!privateKeys[account]) {
            return {
                errCode: ERROR,
                errMsg: "导出失败: 账号不存在!"
            };
        }

        let unLock = await PasswordManager.UnLock(password);
        if (!unLock) {
            return {
                errCode: ERROR,
                errMsg: "导出失败: 密码错误!"
            };
        }

        let keyStore = privateKeys[account];
        // 创建账号实例
        let ethAccount = new Web3EthAccounts(null, "lat");

        try {
            // 解锁账号
            let decryptInfo = ethAccount.decrypt(keyStore, password);

            return {
                errCode: SUCCESS,
                data: decryptInfo
            };
        } catch (e) {
            return {
                errCode: ERROR,
                errMsg: "导出失败: 密码错误!"
            };
        }
    }

    /**
     * 切换账号
     * @param {账号名} account
     */
    static async SwitchAccount(account) {
        // 获取当前所有的私钥信息
        let privateKeys = await PrivateKeyManager.getPrivateKeys();
        // 当前没有私钥
        if (!privateKeys) {
            return {
                errCode: ERROR,
                errMsg: "账号不存在"
            };
        }
        // 对应账号不存在
        let keystore = privateKeys[account];
        if (!keystore) {
            return {
                errCode: ERROR,
                errMsg: "账号不存在"
            };
        }
        // 存储当前选中的账号
        await PrivateKeyManager.storeCurrentAccount(account);

        return {
            errCode: SUCCESS,
            errMsg: "",
            data: keystore.address // 返回钱包地址
        };
    }
    /**
     * 获取所有钱包地址列表
     */
    static async GetAccountList() {
        // 获取所有私钥地想你
        let privateKeys = await PrivateKeyManager.getPrivateKeys();

        let accounts = Object.keys(privateKeys);
        // 获取当前选中的账号
        let curAccount = await PrivateKeyManager.getCurrentAccount();

        let accountList = [];

        accounts.forEach(account => {
            let keyStore = privateKeys[account];
            accountList.push({
                account,
                address: keyStore.address
            });
        });

        return {
            accountList, // 账号列表
            curAccount // 当前账号
        };
    }

    static async GetAccountInfo() {
        // 获取所有私钥地想你
        let privateKeys = await PrivateKeyManager.getPrivateKeys();

        let accounts = Object.keys(privateKeys);
        // 获取当前选中的账号
        let curAccount = await PrivateKeyManager.getCurrentAccount();

        for (let i = 0; i < accounts.length; ++i) {
            let account = accounts[i];

            if (account === curAccount) {
                let keyStore = privateKeys[account];
                return {
                    accountName: account,
                    address: keyStore.address
                };
            }
        }
        // 不存在账号
        if (accounts.length === 0) {
            return null;
        }
        // 如果没有选中的账号, 则使用第一个作为默认账号
        // 这种情况出现概率极低,但是还是需要考虑
        return {
            accountName: accounts[0],
            address: privateKeys[accounts[0]].address
        };
    }

    /**
     * 判断是否有账号
     */
    static async HasAccount() {
        let privateKeys = await PrivateKeyManager.getPrivateKeys();
        if (!privateKeys) {
            return false;
        }
        let accounts = Object.keys(privateKeys);
        return accounts.length !== 0;
    }

    /**
     * 获取当前选中的账号
     * @returns
     */
    static getCurrentAccount() {
        return new Promise(resolve => {
            chrome.storage.local.get([PrivateKeyManager.curAccountKey], res => {
                let account = res[PrivateKeyManager.curAccountKey];
                resolve(account);
            });
        });
    }

    /**
     * 保存当前选中的账号
     * @param {账号名} account
     * @returns
     */
    static storeCurrentAccount(account) {
        return new Promise(resolve => {
            chrome.storage.local.set(
                {
                    currentAccount: account
                },
                () => {
                    resolve({ errCode: SUCCESS });
                }
            );
        });
    }

    /**
     * 保存加密后的私钥列表
     * @param {私钥列表} encryptPrivateKeys
     */
    static storePriateKeys(encryptPrivateKeys) {
        return new Promise(resolve => {
            chrome.storage.local.set(
                {
                    privateKeys: encryptPrivateKeys
                },
                () => {
                    resolve({ errCode: SUCCESS });
                }
            );
        });
    }
    /**
     * 获取所有私钥的对象
     * @returns
     */
    static getPrivateKeys() {
        return new Promise(resolve => {
            chrome.storage.local.get([PrivateKeyManager.privateKeyName], res => {
                let privateKeys = res[PrivateKeyManager.privateKeyName];
                resolve(privateKeys);
            });
        });
    }
}
/**
 * 交易管理类
 */
class TransactionManager {
    static web3; // Web3 实例

    static chainId = "210309"; // 测试网的chainId

    static getWeb3Ins() {
        if (TransactionManager.web3) {
            return TransactionManager.web3;
        }
        TransactionManager.web3 = new Web3(
            new Web3.providers.HttpProvider("http://35.247.155.162:6789")
        );
        return TransactionManager.web3;
    }

    /**
     * 获取余额
     * @param {钱包地址} address
     * @returns
     */
    static async GetBalanceOf(address) {
        // 钱包地址
        let web3 = TransactionManager.getWeb3Ins();

        let balance = await web3.platon.getBalance(address);

        return web3.utils.fromVon(balance, "lat");
    }
    /**
     * 判断是不是有效的钱包地址
     * @param {} address 
     * @returns 
     */
    static IsBech32Address(address) {
        let web3 = TransactionManager.getWeb3Ins();
        return web3.utils.isBech32Address(address);
    }
    /**
     *
     * @returns 获取当前gas的价格
     */
    static async GetGasPrice() {
        let web3 = TransactionManager.getWeb3Ins();

        let gasprice = await web3.platon.getBalance(address);

        return web3.utils.fromVon(gasprice, "lat");
    }
    /**
     * 发送lat
     * @param {lat数量} lat
     * @param {接收地址} toAddress
     */
    static async SendLATTO(lat, account, toAddress) {
        let web3 = TransactionManager.getWeb3Ins();

        //解锁秘钥文件
        let privateketRes = await PrivateKeyManager.ExportPrivateKey(
            account,
            PasswordManager.GetPassPassword()
        );
        if (privateketRes.errCode !== 0) {
            return privateketRes;
        }

        let walletInfo = privateketRes.data;

        let nonce = await web3.platon.getTransactionCount(walletInfo.address);

        let gasPrice = await web3.platon.getGasPrice();

        let txData = {
            from: walletInfo.address, //转账的钱包地址
            to: toAddress, // 接收转账的地址
            value: web3.utils.toVon(lat, "lat"), //value的单位为von 从官网得知 1Lat = 1e18个VON, 这里转10lat
            chainId: TransactionManager.chainId, // 链ID 从官网得知 测试链的id为 210309
            gasPrice: gasPrice, //每一步的费用
            gas: 21000, // 步数 就是gasLimit
            nonce: nonce,
            data: ""
        };

   

        let signTxtData = await web3.platon.accounts.signTransaction(txData, walletInfo.privateKey);

        let txInfo = await web3.platon.sendSignedTransaction(signTxtData.rawTransaction);

        let receipt = await web3.platon.getTransactionReceipt(txInfo.transactionHash);

        return {
            errCode: SUCCESS,
            data: {
                txHash: txInfo.transactionHash,
                receipt: receipt
            }
        };
    }
}

// 导出PasswordManager
window.digging = {
    PasswordManager,
    PrivateKeyManager,
    TransactionManager
};
