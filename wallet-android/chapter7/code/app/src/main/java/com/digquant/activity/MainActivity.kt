package com.digquant.activity


import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.digquant.adapter.MainPageAdapter

import com.digquant.databinding.ActivityMainBinding
import com.digquant.util.ViewUtil


class MainActivity : BaseActivity() {
    /**
     * 使用binding
     */
    lateinit var binding: ActivityMainBinding


    private var curSelectPageBtn: LinearLayout? = null

    override fun inflateUI(inflater: LayoutInflater): View {
        ViewUtil.SetStatusBarColorToLight(window)
        binding = ActivityMainBinding.inflate(inflater, null, false);
        return binding.root
    }

    override fun initUI() {

        binding.mainPager.adapter = MainPageAdapter()
        // 禁用左右滑动
        binding.mainPager.isUserInputEnabled = false

        onSelectPage(binding.assetBtn, 0)
    }

    /**
     * 初始化事件
     */
    override fun initEvent() {

        binding.assetBtn.setOnClickListener {
            ViewUtil.SetStatusBarColorToLight(window)
            onSelectPage(binding.assetBtn, 0)

        }
        binding.delegateBtn.setOnClickListener {
            ViewUtil.SetStatusBarColorToBlack(window)
            onSelectPage(binding.delegateBtn, 1)
        }
        binding.meBtn.setOnClickListener {
            onSelectPage(binding.meBtn, 2)
        }
    }


    private fun onSelectPage(selectPageBtn: LinearLayout?, index: Int) {
        if (selectPageBtn == null || selectPageBtn == curSelectPageBtn) {
            return
        }
        selectPageBtn.getChildAt(0).isSelected = true
        selectPageBtn.getChildAt(1).isSelected = true


        val imgView = curSelectPageBtn?.getChildAt(0)
        imgView?.isSelected = false

        val textView = curSelectPageBtn?.getChildAt(1)
        textView?.isSelected = false


        curSelectPageBtn = selectPageBtn

        binding.mainPager.setCurrentItem(index, false)


    }
}