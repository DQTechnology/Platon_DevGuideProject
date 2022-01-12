package com.digquant.activity

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import com.digquant.databinding.ActivitySplashBinding
import com.digquant.util.DXRouter


class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding


    override fun inflateUI(inflater: LayoutInflater): View {
        binding = ActivitySplashBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun initUI() {
        val handler = Handler(Looper.getMainLooper())
        /**
         * 设置2s后跳转到页面
         */
        handler.postDelayed({

            DXRouter.JumpAndFinish(this, OperateMenuActivity::class.java)

        }, 2000)
    }

    override fun initEvent() {

    }
}

