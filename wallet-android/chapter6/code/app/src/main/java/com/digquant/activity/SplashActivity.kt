package com.digquant.activity

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import com.digquant.databinding.ActivitySplashBinding
import com.digquant.service.WalletManager
import com.digquant.util.DXRouter


class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding


    override fun inflateUI(inflater: LayoutInflater): View {
        binding = ActivitySplashBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun initUI() {
        // 加载所有的钱包
        WalletManager.LoadAllWallet()
        val handler = Handler(Looper.getMainLooper())
        /**
         * 设置2s后跳转到页面
         */
        handler.postDelayed({

            if (WalletManager.IsExistWallet()) {
                DXRouter.JumpAndFinish(this, MainActivity::class.java)
            } else {
                DXRouter.JumpAndFinish(this, OperateMenuActivity::class.java)
            }
        }, 2000)
    }

    override fun initEvent() {

    }
}

