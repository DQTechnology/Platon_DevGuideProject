package com.digquant.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.digquant.databinding.ActivityBackupMnemonicPhraseBinding
import com.digquant.service.WalletManager

import com.digquant.util.ViewUtil

class BackupMnemonicPhraseActivity : AppCompatActivity() {

    private lateinit var bindding: ActivityBackupMnemonicPhraseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewUtil.SetWindow(window)

        ViewUtil.SetStatusBarColorToBlack(window)
        val inflater = LayoutInflater.from(this)
        bindding = ActivityBackupMnemonicPhraseBinding.inflate(inflater, null, false)

        setContentView(bindding.root)

        initUI()

        initEvent()
    }


    private fun initUI() {

        val createWalletInfo = WalletManager.GetCreateWalletSession()

        val mnemonicWords = createWalletInfo?.mnemonicWords
        if (mnemonicWords != null) {
            bindding.tvMnemonic1.text = mnemonicWords[0]
            bindding.tvMnemonic2.text = mnemonicWords[1]
            bindding.tvMnemonic3.text = mnemonicWords[2]
            bindding.tvMnemonic4.text = mnemonicWords[3]
            bindding.tvMnemonic5.text = mnemonicWords[4]
            bindding.tvMnemonic6.text = mnemonicWords[5]
            bindding.tvMnemonic7.text = mnemonicWords[6]
            bindding.tvMnemonic8.text = mnemonicWords[7]
            bindding.tvMnemonic9.text = mnemonicWords[8]
            bindding.tvMnemonic10.text = mnemonicWords[9]
            bindding.tvMnemonic11.text = mnemonicWords[10]
            bindding.tvMnemonic12.text = mnemonicWords[11]
        }
    }

    private fun initEvent() {
        bindding.backBtn.setOnClickListener {
            this.finish()
        }
    }
}