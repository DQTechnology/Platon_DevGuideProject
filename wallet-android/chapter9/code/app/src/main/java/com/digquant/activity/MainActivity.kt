package com.digquant.activity


import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.digquant.adapter.MainPageAdapter
import com.digquant.adapter.RightSideWalletListAdapter

import com.digquant.databinding.ActivityMainBinding
import com.digquant.event.ChangeWalletEvent
import com.digquant.event.ImportWalletSuccessEvent
import com.digquant.event.OpenWalletListMenuEvent
import com.digquant.util.ViewUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {
    /**
     * 使用binding
     */
    lateinit var binding: ActivityMainBinding

    private var curSelectPageBtn: LinearLayout? = null

    private val rightSideWalletAdapter = RightSideWalletListAdapter()


    private val mainPageAdapter = MainPageAdapter()

    override fun inflateUI(inflater: LayoutInflater): View {
        ViewUtil.SetStatusBarColorToLight(window)
        binding = ActivityMainBinding.inflate(inflater, null, false);
        return binding.root
    }

    override fun initUI() {

        binding.mainPager.adapter = mainPageAdapter
        // 禁用左右滑动
        binding.mainPager.isUserInputEnabled = false

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.walletList.layoutManager = layoutManager

        binding.walletList.adapter = rightSideWalletAdapter

        onSelectPage(binding.assetBtn, 0)
    }

    /**
     * 初始化事件
     */
    override fun initEvent() {
        // 注册event
        EventBus.getDefault().register(this)

        binding.assetBtn.setOnClickListener {
            ViewUtil.SetStatusBarColorToLight(window)
            onSelectPage(binding.assetBtn, 0)

        }
        binding.delegateBtn.setOnClickListener {
            ViewUtil.SetStatusBarColorToBlack(window)
            onSelectPage(binding.delegateBtn, 1)
        }
        binding.meBtn.setOnClickListener {


        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenWalletListMenuEvent) {

        rightSideWalletAdapter.UpdateData()

        binding.root.openDrawer(binding.rightMenu)
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ChangeWalletEvent) {

        binding.root.closeDrawer(binding.rightMenu)
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