package com.digquant.page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.digquant.R
import com.digquant.adapter.AssetPageAdapter
import com.digquant.adapter.BaseViewHolder
import com.digquant.adapter.ImportPageAdapter
import com.digquant.databinding.LayoutAppTabItem1Binding
import com.digquant.databinding.LayoutAppTabItem3Binding
import com.digquant.databinding.PageAssetsBinding
import com.digquant.service.WalletManager
import com.digquant.util.DensityUtil
import com.digquant.util.ResourceUtil

import com.digquant.util.ViewUtil

class AssetPage(itemView: View) : BaseViewHolder(itemView) {
    private val binding: PageAssetsBinding = PageAssetsBinding.bind(itemView)

    init {
        initUI()

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

        binding.vpContent.adapter = AssetPageAdapter()

        binding.stbBar.setViewPager(binding.vpContent, titleList)
        // 获取当前钱包名字
        val walletName = WalletManager.GetCurrentWalletName()
        // 获取当前钱包地址
        val walletAddress = WalletManager.GetWalletAddress(walletName)
        // 显示在页面上
        binding.walletName.text = walletName
        binding.walletAddress.text = walletAddress
    }

    private fun getTableView(container: ViewGroup, title: String): View? {
        val inflater = LayoutInflater.from(container.context)
        val tabBinding = LayoutAppTabItem3Binding.inflate(inflater, container, false)
        tabBinding.tvTitle.text = title

        return tabBinding.root
    }
}