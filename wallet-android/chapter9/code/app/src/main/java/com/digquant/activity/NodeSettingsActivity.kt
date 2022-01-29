package com.digquant.activity

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.digquant.adapter.NodeAdapter
import com.digquant.databinding.ActivityNodeSettingsBinding
import com.digquant.event.ChangeNodeEvent
import com.digquant.event.OpenWalletListMenuEvent
import com.digquant.util.ViewUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NodeSettingsActivity : BaseActivity() {


    lateinit var binding: ActivityNodeSettingsBinding


    override fun inflateUI(inflater: LayoutInflater): View {
        ViewUtil.SetStatusBarColorToBlack(window)
        binding = ActivityNodeSettingsBinding.inflate(inflater, null, false);
        return binding.root
    }

    override fun initUI() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.listNodes.layoutManager =  layoutManager

        binding.listNodes.adapter = NodeAdapter()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ChangeNodeEvent) {
        this.finish()
    }



    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this);
    }

    override fun initEvent() {
        EventBus.getDefault().register(this)
    }
}