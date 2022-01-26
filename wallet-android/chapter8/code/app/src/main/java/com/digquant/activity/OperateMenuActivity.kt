package com.digquant.activity

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.digquant.databinding.ActivityBackupMnemonicPhraseBinding
import com.digquant.databinding.ActivityOperateMenuBinding
import com.digquant.util.DXRouter
import com.digquant.util.ViewUtil


class OperateMenuActivity : BaseActivity() {

    private lateinit var binding: ActivityOperateMenuBinding

    override fun inflateUI(inflater: LayoutInflater): View {

        binding = ActivityOperateMenuBinding.inflate(inflater, null, false)

        return binding.root
    }

    override fun initUI() {

    }

    override fun initEvent() {
        /***
         * 创建钱包
         */
        binding.scCreateWallet.setOnClickListener {
            DXRouter.JumpTo(this, CreateWalletActivity::class.java)
        }
        /**
         * 导入钱包
         */
        binding.scImportWallet.setOnClickListener {
            DXRouter.JumpTo(this, ImportActivity::class.java)
        }
    }
}