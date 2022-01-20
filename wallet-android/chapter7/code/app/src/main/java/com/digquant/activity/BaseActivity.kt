package com.digquant.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.digquant.util.ViewUtil

abstract class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * 把状态栏颜色设置未黑色
         */
        ViewUtil.SetWindow(window)
        ViewUtil.SetStatusBarColorToBlack(window)
        /**
         * 设置UI对象
         */
        setContentView(inflateUI( LayoutInflater.from(this)))
        initUI()
        initEvent()
    }
    /**
     * 创建UI对象
     */
    protected abstract fun inflateUI(inflater: LayoutInflater): View
    /**
     * 初始化UI
     */
    protected abstract fun initUI()
    /**
     * 初始化时间
     */
    protected abstract fun initEvent()
}