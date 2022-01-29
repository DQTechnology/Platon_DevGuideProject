package com

import android.app.Application
import android.content.Context
import com.digquant.service.WalletManager
import com.digquant.util.ResourceUtil
import org.web3j.crypto.WalletApplication
import org.web3j.crypto.bech32.AddressBehavior
import org.web3j.crypto.bech32.AddressManager

class DiggingApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    init {
        ResourceUtil.SetContext(this)
        WalletApplication.init(
            WalletApplication.MAINNET,
            AddressManager.ADDRESS_TYPE_BECH32,
            AddressBehavior.CHANNLE_PLATON
        )
        context = this

    }
}