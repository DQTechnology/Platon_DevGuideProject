package com.digquant.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.digquant.databinding.ActivitySplashBinding
import com.digquant.util.DXRouter
import com.digquant.util.ViewUtil


class SplashActivity : AppCompatActivity() {

    private lateinit var bindding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val inflater = LayoutInflater.from(this)
        bindding = ActivitySplashBinding.inflate(inflater, null, false)

        setContentView(bindding.root)

        val handler = Handler(Looper.getMainLooper())
        /**
         * 设置2s后跳转到页面
         */
        handler.postDelayed({

            DXRouter.JumpAndFinish(this, OperateMenuActivity::class.java)

        }, 2000)
    }
}

