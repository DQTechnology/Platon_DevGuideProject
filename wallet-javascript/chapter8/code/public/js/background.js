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
        let curAccount = await PrivateKeyManager.GetCurrentAccount();

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
        let curAccount = await PrivateKeyManager.GetCurrentAccount();

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
    static GetCurrentAccount() {
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
 * 网络管理
 */
class NetworkManager {
    static webIns = null;
    static curNetwork = "PlatON开发测试网";

    static networkInfos = {
        PlatON开发测试网: {
            rpcUrl: "http://35.247.155.162:6789",
            chainId: "210309",
            browserUrl: "https://devnetscan.platon.network"
        },
        PlatON网络: {
            rpcUrl: "https://samurai.platon.network",
            chainId: "100",
            browserUrl: "https://scan.platon.network"
        }
    };

    static async InitNetwork() {
        await NetworkManager.loadNetworkInfos();
        await NetworkManager.loadCurNetwork();
        // 判断网络是否存在,如果不存在则选择开发测试网
        if (!NetworkManager.networkInfos[NetworkManager.curNetwork]) {
            NetworkManager.curNetwork = "PlatON开发测试网";
        }
    }
    /**
     * 加载网络信息
     * @returns
     */
    static loadNetworkInfos() {
        return new Promise(resolve => {
            chrome.storage.local.get(["networkInfos"], res => {
                let networkInfos = res["networkInfos"];
                // 没有自定义网络,直接返回
                if (!networkInfos) {
                    return;
                }
                NetworkManager.networkInfos = {
                    ...networkInfos,
                    ...NetworkManager.networkInfos
                };
                resolve();
            });
        });
    }

    /**
     * 获取所有网络信息
     * @returns
     */
    static GetNetworkNameList() {
        return Object.keys(NetworkManager.networkInfos);
    }

    static GetCurNetworkName() {
        return NetworkManager.curNetwork;
    }
    /**
     *
     * @returns
     */
    static GetCurNetworkRPCUrl() {
        let networkInfo = NetworkManager.networkInfos[NetworkManager.curNetwork];

        return networkInfo.rpcUrl;
    }
    /**
     * 获取区块链浏览器地址
     * @returns
     */
    static GetBrowserUrl() {
        let networkInfo = NetworkManager.networkInfos[NetworkManager.curNetwork];

        return networkInfo.browserUrl;
    }
    /**
     * 切换网络
     * @param {}} networkName
     * @returns
     */
    static SwitchNetwork(networkName) {
        return new Promise(resolve => {
            let networkInfo = NetworkManager.networkInfos[networkName];

            if (!networkInfo) {
                resolve({
                    errCode: ERROR,
                    errMsg: "网络不存在!"
                });
                return;
            }
            chrome.storage.local.set(
                {
                    curNetwork: networkName
                },
                () => {
                    NetworkManager.curNetwork = networkName;
                    NetworkManager.webIns = null;
                    resolve({ errCode: SUCCESS });
                }
            );
        });
    }
    /**
     * 加载网络信息
     * @returns
     */
    static loadCurNetwork() {
        return new Promise(resolve => {
            chrome.storage.local.get(["curNetwork"], res => {
                let curNetwork = res["curNetwork"];
                if (!curNetwork) {
                    return;
                }
                NetworkManager.curNetwork = curNetwork;
                resolve();
            });
        });
    }
    /**
     * 获取网页实例
     * @returns
     */
    static GetWebIns() {
        if (NetworkManager.webIns) {
            return NetworkManager.webIns;
        }

        let networkInfo = NetworkManager.networkInfos[NetworkManager.curNetwork];

        NetworkManager.webDevelopIns = new Web3(
            new Web3.providers.HttpProvider(networkInfo.rpcUrl)
        );
        return NetworkManager.webDevelopIns;
    }
    /**
     *
     * @returns 获取链id
     */
    static GetChainId() {
        let networkInfo = NetworkManager.networkInfos[NetworkManager.curNetwork];
        return networkInfo.chainId;
    }
}

/**
 * 交易管理类
 */
class TransactionManager {
    // 存储当前选中的账号

    static pendingTxRecords = {
        // 等确认的交易记录
    };

    static isMonitoring = false; // 判断是否正在监控中

    /**
     * 计算手续费
     */
    static async CalcGasUsed() {
        let web3 = NetworkManager.GetWebIns();
        let gasPrice = await web3.platon.getGasPrice();
        //
        // 手续费 = gasPrice * gasLimit  gasLimit设置为= 21000;
        // 这里需要把值换成字符串,
        return web3.utils.fromVon(gasPrice * 21000 + "", "lat");
    }

    /**
     * 获取余额
     * @param {钱包地址} address
     * @returns
     */
    static async GetBalanceOf(address) {
        // 钱包地址
        let web3 = NetworkManager.GetWebIns();
        let balance = await web3.platon.getBalance(address);

        return web3.utils.fromVon(balance, "lat");
    }

    /**
     * 判断是不是有效的钱包地址
     * @param {} address
     * @returns
     */
    static IsBech32Address(address) {
        let web3 = NetworkManager.GetWebIns();
        return web3.utils.isBech32Address(address);
    }
    /**
     *
     * @returns 获取当前gas的价格
     */
    static async GetGasPrice() {
        let web3 = NetworkManager.GetWebIns();

        let gasprice = await web3.platon.getBalance(address);

        return web3.utils.fromVon(gasprice, "lat");
    }
    /**
     * 发送lat
     * @param {lat数量} lat
     * @param {接收地址} toAddress
     */
    static async SendLATTO(lat, account, toAddress) {
        let web3 = NetworkManager.GetWebIns();

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
        // 获取的收据
        let receipt = await web3.platon.getTransactionReceipt(txInfo.transactionHash);

        // pending状态
        if (receipt === null) {
            // 持久化交易记录
            TransactionManager.addTxRecord(txInfo.transactionHash, txData, receipt);
            return {
                errCode: WARNNING,
                errMsg: "交易等待处理中"
            };
        }

        // 如果交易失败,则直接返回
        if (!receipt.status) {
            return {
                errCode: Error,
                errMsg: "发送失败!",
                data: {
                    txHash: txInfo.transactionHash,
                    receipt: receipt
                }
            };
        }
        // 持久化交易记录
        TransactionManager.addTxRecord(txInfo.transactionHash, txData, receipt);
        return {
            errCode: SUCCESS,
            data: {
                txHash: txInfo.transactionHash,
                receipt: receipt
            }
        };
    }

    /**
     * 获取交易列表
     * @param {钱包地址} address
     */
    static GetPendingRecords(address) {
        let txHashList = Object.keys(TransactionManager.pendingTxRecords);
        let txRecords = [];
        for (let i = 0; i < txHashList.length; ++i) {
            let txHash = txHashList[i];
            // 获取交易记录
            let txRcordInfo = TransactionManager.pendingTxRecords[txHash];
            if (txRcordInfo.from === address) {
                txRecords.push(txRcordInfo);
            }
        }
        return txRecords;
    }
    /**
     * 插件启动时,需要从store加载未完成的交易记录
     */
    static LoadPendingTxRecord() {
        chrome.storage.local.get(["pendingTxRecords"], res => {
            let records = res["pendingTxRecords"];
            // 没有交易记录直接返回
            if (!records) {
                return;
            }
            TransactionManager.records = records;

            // 启动监控器

            TransactionManager.startTxMonitor();
        });
    }

    /**
     * 添加交易记录
     * @param {交易哈希} transactionHash
     * @param {交易数据} txData
     * @param {收据} receipt
     */
    static async addTxRecord(transactionHash, txData, receipt) {
        let web3 = NetworkManager.GetWebIns();
        // 以交易哈希作为key
        let txRcordInfo = {
            txHash: transactionHash,
            value: web3.utils.fromVon(txData.value, "lat"), // lat数量
            from: txData.from, // 发送人
            to: txData.to, // 接收人
            timestamp: new Date().getTime() // 发送时间
        };
        // 记录交易记录
        TransactionManager.pendingTxRecords[transactionHash] = txRcordInfo;

        if (receipt) {
            txRcordInfo.status = "确认中";
            txRcordInfo.blockNumber = receipt.blockNumber; // 记录所在块高
        } else {
            txRcordInfo.status = "待处理";
        }
        //  持久化交易记录
        await TransactionManager.persistTXRecord();
        // 启动监控器

        TransactionManager.startTxMonitor();
    }

    /**
     *  持久化交易记录
     */
    static persistTXRecord() {
        return new Promise(resolve => {
            chrome.storage.local.set(
                {
                    pendingTxRecord: TransactionManager.pendingTxRecords
                },
                () => {
                    resolve();
                }
            );
        });
    }
    static startTxMonitor() {
        // 监控器已经在 运行中,不重复运行
        if (TransactionManager.isMonitoring) {
            return;
        }
        //正在监控中
        TransactionManager.isMonitoring = true;

        TransactionManager.checkTXRecordStatus();
    }
    // 检查交易记录状态
    static checkTXRecordStatus() {
        setTimeout(async () => {
            let txHashList = Object.keys(TransactionManager.pendingTxRecords);
            if (txHashList.length === 0) {
                // 当前没有待确认的交易列表
                // 把监控状态设置为false然后直接返回
                TransactionManager.isMonitoring = false;
                return;
            }
            let web3 = NetworkManager.GetWebIns();
            // 获取当前块高
            let blockNumber = await web3.platon.getBlockNumber();
            for (let i = 0; i < txHashList.length; ++i) {
                let txHash = txHashList[i];
                // 获取交易记录
                let txRcordInfo = TransactionManager.pendingTxRecords[txHash];
                if (txRcordInfo.status === "确认中") {
                    // 设置确认块高为6, 则认为交易已经成功!
                    if (blockNumber - txRcordInfo.blockNumber > 6) {
                        // 已经确认的,则从监控列表中删除
                        delete TransactionManager.pendingTxRecords[txHash];
                        continue;
                    }
                } else if (txRcordInfo.status === "待处理") {
                    // 获取的收据
                    let receipt = await web3.platon.getTransactionReceipt(txHash);
                    // 还是没有获取到收据,交易还在待处理中,需要处理
                    if (receipt === null) {
                        continue;
                    }
                    // 判断确认块高是否已经超过6,如果超过6则已经交易成功
                    if (blockNumber - receipt.blockNumber > 6) {
                        delete TransactionManager.pendingTxRecords[txHash];
                        continue;
                    }

                    // 确认块高未超过6, 交易记录状态编程确认中
                    txRcordInfo.blockNumber = receipt.blockNumber; // 记录所在块高
                    txRcordInfo.status = "确认中";
                }
            }

            //  持久化交易记录
            await TransactionManager.persistTXRecord();
            // 继续监听
            TransactionManager.checkTXRecordStatus();
            // 10s检查一次
        }, 1000);
    }
}

/**
 * 委托管理类
 */
class DelegateManager {
    /**
     * 委托LAT
     * @param {}} nodeId 
     * @param {*} lat 
     * @returns 
     */
    static async Deletgate(nodeId, lat) {
        let web3 = NetworkManager.GetWebIns();

        let curAccount = await PrivateKeyManager.GetCurrentAccount();
        let privateketRes = await PrivateKeyManager.ExportPrivateKey(
            curAccount,
            PasswordManager.GetPassPassword()
        );
        if (privateketRes.errCode !== 0) {
            return privateketRes;
        }

        let walletInfo = privateketRes.data;

        let ppos = new web3.PPOS({ provider: NetworkManager.GetCurNetworkRPCUrl() });

        ppos.updateSetting({
            privateKey: walletInfo.privateKey,
            chainId: 100
        });

        let params = {
            funcType: 1004, // 委托LAT的函数
            typ: 0,
            nodeId: ppos.hexStrBuf(nodeId),
            amount: ppos.bigNumBuf(web3.utils.toVon(lat, "lat"))
        };

        let reply = await ppos.send(params);
        return reply;
    }
}

// 导出PasswordManager
window.digging = {
    NetworkManager,
    DelegateManager,
    PasswordManager,
    PrivateKeyManager,
    TransactionManager
};

//  初始化钱包
async function InitWallet() {
    // 加载网络
    await NetworkManager.InitNetwork();
    // 加载交易记录
    TransactionManager.LoadPendingTxRecord();
}
// 调用初始化函数
InitWallet();
