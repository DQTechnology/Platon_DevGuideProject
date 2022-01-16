package com.digquant.activity

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.digquant.databinding.ActivityBackupMnemonicPhraseBinding
import com.digquant.service.WalletManager
import com.digquant.util.DXRouter
import kotlinx.coroutines.launch

class BackupMnemonicPhraseActivity : BaseActivity() {


    private lateinit var binding: ActivityBackupMnemonicPhraseBinding

    override fun inflateUI(inflater: LayoutInflater): View {

        binding = ActivityBackupMnemonicPhraseBinding.inflate(inflater, null, false)

        return binding.root
    }


    override fun initUI() {


        val createWalletInfo = WalletManager.GetCreateWalletSession()

        val mnemonicWords = createWalletInfo?.mnemonicWords
        if (mnemonicWords != null) {
            binding.tvMnemonic1.text = mnemonicWords[0]
            binding.tvMnemonic2.text = mnemonicWords[1]
            binding.tvMnemonic3.text = mnemonicWords[2]
            binding.tvMnemonic4.text = mnemonicWords[3]
            binding.tvMnemonic5.text = mnemonicWords[4]
            binding.tvMnemonic6.text = mnemonicWords[5]
            binding.tvMnemonic7.text = mnemonicWords[6]
            binding.tvMnemonic8.text = mnemonicWords[7]
            binding.tvMnemonic9.text = mnemonicWords[8]
            binding.tvMnemonic10.text = mnemonicWords[9]
            binding.tvMnemonic11.text = mnemonicWords[10]
            binding.tvMnemonic12.text = mnemonicWords[11]
        }
    }

    override fun initEvent() {
        binding.backBtn.setOnClickListener {
            this.finish()
        }
        binding.scNext.setOnClickListener {

            DXRouter.JumpTo(this, VerifyMnemonicPhraseActivity::class.java)
        }
    }
}