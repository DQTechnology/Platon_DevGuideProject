package com.digquant.service

import com.digquant.util.JZMnemonicUtil
import com.digquant.util.JZWalletUtil

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
    fun ClearCreateWalletSession() {
        createWalletInfo = null
    }

}