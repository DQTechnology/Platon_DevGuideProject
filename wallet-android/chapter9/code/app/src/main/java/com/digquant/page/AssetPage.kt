package com.digquant.page

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.digquant.R
import com.digquant.activity.CreateWalletActivity
import com.digquant.activity.ImportActivity
import com.digquant.activity.NodeSettingsActivity
import com.digquant.adapter.AssetPageAdapter
import com.digquant.adapter.BaseViewHolder
import com.digquant.adapter.ImportPageAdapter
import com.digquant.databinding.DialogAssetsMoreBinding
import com.digquant.databinding.LayoutAppTabItem1Binding
import com.digquant.databinding.LayoutAppTabItem3Binding
import com.digquant.databinding.PageAssetsBinding
import com.digquant.event.ChangeNodeEvent
import com.digquant.event.ChangeWalletEvent
import com.digquant.event.ImportWalletSuccessEvent
import com.digquant.event.OpenWalletListMenuEvent
import com.digquant.service.NodeManager
import com.digquant.service.WalletManager
import com.digquant.util.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetPage(itemView: View) : BaseViewHolder(itemView) {

    private val binding: PageAssetsBinding = PageAssetsBinding.bind(itemView)


    private val assertAdapter = AssetPageAdapter()

    private val assetMoreDialog =
        DialogAssetsMoreBinding.inflate(LayoutInflater.from(itemView.context), null, false)


    private lateinit var popWindow: PopupWindow

    init {
        initUI()
        initEvent()
    }

    private fun initUI() {


        //设置顶部的状态栏高度
        ViewUtil.SetStatusBarMargin(binding.nodeBar)
        //
        // 设置tab选项
        var indicatorThickness = DensityUtil.DP2PX(itemView.context, 2.0f)
        binding.stbBar.setIndicatorThickness(indicatorThickness + 4)
        indicatorThickness += 4
        binding.stbBar.setIndicatorCornerRadius(indicatorThickness / 2.0f)
        val titleList = ArrayList<String>(3)
        // 添加资产选项
        titleList.add(ResourceUtil.GetString(R.string.wallet_tab_assets))

        binding.stbBar.setCustomTabView { container, title -> getTableView(container, title) }

        binding.vpContent.adapter = assertAdapter

        binding.stbBar.setViewPager(binding.vpContent, titleList)

        showNodeInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ChangeWalletEvent) {
        showWalletInfo()
        assertAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ChangeNodeEvent) {
        showNodeInfo()

        assertAdapter.notifyDataSetChanged()
    }


    private fun showNodeInfo() {
        val nodeInfo = NodeManager.GetCurNodeInfo()
        binding.nodeName.text = nodeInfo.nodeName
        binding.bk.setBackgroundColor(Color.parseColor(nodeInfo.themeColor))
    }

    private fun showWalletInfo() {
        // 获取当前钱包名字
        val walletName = WalletManager.GetCurrentWalletName()
        // 获取当前钱包地址
        val walletAddress = WalletManager.GetWalletAddress(walletName)
        // 显示在页面上
        binding.walletName.text = walletName
        binding.walletAddress.text = walletAddress
    }

    override fun OnRender(position: Int) {
        showWalletInfo()
    }

    private fun initEvent() {
        EventBus.getDefault().register(this)

        binding.walletNameBar.setOnClickListener {
            EventBus.getDefault().post(OpenWalletListMenuEvent())
        }

        binding.walletAddress.setOnClickListener {
            //失败

            val cm =
                itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clipData = ClipData.newPlainText("text", binding.walletAddress.text.toString())

            cm.setPrimaryClip(clipData)
            ToastUtil.showLongToast(itemView.context, "复制成功")
        }

        binding.nodeSetting.setOnClickListener {
            DXRouter.JumpTo(itemView.context, NodeSettingsActivity::class.java)
        }

        // 显示弹出框
        binding.assetsAdd.setOnClickListener {
            popWindow = PopupWindowUtil.Show(assetMoreDialog.root, binding.assetsAdd)
        }

        // 跳转到创建钱包页面
        assetMoreDialog.createWallet.setOnClickListener {
            DXRouter.JumpTo(itemView.context, CreateWalletActivity::class.java)

            popWindow.dismiss()
        }

        // 跳转到导入钱包页面
        assetMoreDialog.importWallet.setOnClickListener {
            DXRouter.JumpTo(itemView.context, ImportActivity::class.java)

            popWindow.dismiss()
        }
    }

    private fun getTableView(container: ViewGroup, title: String): View? {
        val inflater = LayoutInflater.from(container.context)
        val tabBinding = LayoutAppTabItem3Binding.inflate(inflater, container, false)
        tabBinding.tvTitle.text = title

        return tabBinding.root
    }
}