package com.digquant.service


import com.DiggingApplication
import com.digquant.entity.Status
import com.digquant.util.FileUtil
import com.digquant.util.JZMnemonicUtil
import com.digquant.util.JZWalletUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.bitcoinj.crypto.*
import org.web3j.crypto.*
import org.web3j.utils.Numeric

/**
 * 创就按钱包的session
 */
data class CreateWalletSessionInfo(
    var walletName: String,
    var password: String,
    var mnemonicWords: List<String>
)

object WalletManager {


    private var createWalletInfo: CreateWalletSessionInfo? = null

    private const val PATH = "M/44H/486H/0H/0"

    private const val N_STANDARD = 16384

    private const val P_STANDARD = 1

    private const val WalletStorePath = "wallet"


    /**
     * 生成创建钱包的会话
     */
    fun BuildCreateWalletSession(walletName: String, password: String): Boolean {
        /**
         * 生成助记词
         */
        val mnemonic: String = JZWalletUtil.generateMnemonic()
        /**
         * 检测生产的助记词是否有效
         */
        if (!JZWalletUtil.isValidMnemonic(mnemonic)) {
            return false
        }
        val mnemonicWords = mnemonic.split(" ")

        createWalletInfo = CreateWalletSessionInfo(walletName, password, mnemonicWords)

        return true
    }

    /**
     * 获取创建钱包的会话
     */
    fun GetCreateWalletSession(): CreateWalletSessionInfo? {

        return createWalletInfo
    }

    /**
     * 清空创建钱包的会话
     */
    private fun clearCreateWalletSession() {
        createWalletInfo = null
    }

    /**
     * 生成钱包
     */
    fun GenerateWallet(): Boolean {
        //
        val createWalletInfo = GetCreateWalletSession() ?: return false
        return ImportMnemonicWords(
            createWalletInfo.walletName,
            createWalletInfo.password,
            createWalletInfo.mnemonicWords
        )
    }

    /**
     * 导入助记词
     */
    fun ImportMnemonicWords(name: String, password: String, mnemonicWords: List<String>): Boolean {
        // 1,把助记词组合成空格隔开的字符串
        val mnemonic = mnemonicWords.joinToString(" ")
        // 2.生成种子
        val seed = JZMnemonicUtil.generateSeed(mnemonic, null)
        // 3. 生成根Keystore root private key 树顶点的master key ；bip32
        val rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed)
        // 4. 由根Keystore生成 第一个HD 钱包
        val dh = DeterministicHierarchy(rootPrivateKey)
        // 5. 定义父路径 H则是加强
        val parentPath = HDUtils.parsePath(PATH)
        // 6. 由父路径,派生出第一个子Keystore "new ChildNumber(0)" 表示第一个（PATH）
        val child: DeterministicKey = dh.deriveChild(parentPath, true, true, ChildNumber(0))
        val ecKeyPair = ECKeyPair.create(child.privKeyBytes)
        // 7. 删除当前创建钱包的信息
        clearCreateWalletSession()
        // 8. 存储钱包
        return storePrivateKey(name, password, ecKeyPair)
    }

    /**
     * 导入钱包文件
     */
    fun ImportKeyStore(
        name: String,
        password: String,
        keystore: String
    ): Status {

        try {
            val context = DiggingApplication.context

            val objectMapper = ObjectMapper()
            val walletFile: WalletFile = objectMapper.readValue(
                keystore,
                WalletFile::class.java
            )
            // 使用密码解锁钱包文件, 如果密码不正确会抛出CipherException异常
            Credentials.create(Wallet.decrypt(password, walletFile))

            FileUtil.WriteStringToFile(
                context.filesDir,
                "$WalletStorePath/${name}.json",
                keystore
            )
            return Status(0, "")

        } catch (e: CipherException) {
            return Status(1, "密码错误")
        } catch (e: Exception) {
            return Status(1, "导入失败!")
        }

    }

    /**
     * 导入密码
     */
    fun ImportPrivateKey(
        name: String,
        password: String,
        privateKey: String
    ): Boolean {

        val ecKeyPair =
            ECKeyPair.create(Numeric.toBigIntNoPrefix(Numeric.cleanHexPrefix(privateKey)))
        return storePrivateKey(name, password, ecKeyPair)
    }


    /**
     * 存储私钥
     */
    private fun storePrivateKey(
        name: String,
        password: String,
        ecKeyPair: ECKeyPair
    ): Boolean {
        return try {

            val context = DiggingApplication.context
            // 生成钱包文件
            val walletFile =
                Wallet.create(password, ecKeyPair, N_STANDARD, P_STANDARD)
            val objectMapper = ObjectMapper()
            // 将钱包文件序列化为json字符串
            val keyFileContent = objectMapper.writeValueAsString(walletFile)
            // 写入文件
            FileUtil.WriteStringToFile(
                context.filesDir,
                "$WalletStorePath/${name}.json",
                keyFileContent
            )
            true
        } catch (e: Exception) {
            false
        }
    }

}