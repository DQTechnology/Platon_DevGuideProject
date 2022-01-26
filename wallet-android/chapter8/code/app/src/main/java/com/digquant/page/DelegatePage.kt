package com.digquant.page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digquant.R
import com.digquant.adapter.BaseViewHolder
import com.digquant.adapter.DelegatePageAdapter
import com.digquant.databinding.LayoutAppTabItem3Binding
import com.digquant.databinding.PageDelegateBinding
import com.digquant.util.DensityUtil
import com.digquant.util.ResourceUtil
import com.digquant.util.ViewUtil

class DelegatePage(itemView: View) : BaseViewHolder(itemView) {
    private val binding: PageDelegateBinding = PageDelegateBinding.bind(itemView)
    init {
        initUI()
    }
    private fun initUI() {
        //设置顶部的状态栏高度
        ViewUtil.SetStatusBarMargin(binding.stbBar)

        // 设置tab选项
        var indicatorThickness = DensityUtil.DP2PX(itemView.context, 2.0f)
        binding.stbBar.setIndicatorThickness(indicatorThickness + 4)
        indicatorThickness += 4
        binding.stbBar.setIndicatorCornerRadius(indicatorThickness / 2.0f)
        val titleList = ArrayList<String>(2)
        // 添加资产选项
        titleList.add(ResourceUtil.GetString(R.string.tab_my_delegate))
        // 添加资产选项
        titleList.add(ResourceUtil.GetString(R.string.tab_validators))

        binding.stbBar.setCustomTabView { container, title -> getTableView(container, title) }

        binding.vpContent.adapter = DelegatePageAdapter()

        binding.stbBar.setViewPager(binding.vpContent, titleList)

    }
    private fun getTableView(container: ViewGroup, title: String): View? {
        val inflater = LayoutInflater.from(container.context)
        val tabBinding = LayoutAppTabItem3Binding.inflate(inflater, container, false)
        tabBinding.tvTitle.text = title

        return tabBinding.root
    }
}