package com.digquant.activity

import android.os.Bundle

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.digquant.databinding.ActivityOperateMenuBinding
import com.digquant.util.DXRouter
import com.digquant.util.ViewUtil


class OperateMenuActivity : AppCompatActivity() {

    private lateinit var bindding: ActivityOperateMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewUtil.SetWindow(window)
        ViewUtil.SetStatusBarColorToBlack(window)
        val inflater = LayoutInflater.from(this)
        bindding = ActivityOperateMenuBinding.inflate(inflater, null, false)
        setContentView(bindding.root)
        initEvent()
    }

    private fun initEvent() {
        /***
         * 创建钱包
         */
        bindding.scCreateWallet.setOnClickListener {
            DXRouter.JumpTo(this, CreateWalletActivity::class.java)
        }
        /**
         * 导入钱包
         */
        bindding.scImportWallet.setOnClickListener {

        }
    }
}