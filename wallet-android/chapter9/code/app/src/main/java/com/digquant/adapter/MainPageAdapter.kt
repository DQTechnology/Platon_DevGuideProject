package com.digquant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.databinding.PageAssetsBinding
import com.digquant.databinding.PageDelegateBinding
import com.digquant.page.AssetPage
import com.digquant.page.DelegatePage
import java.lang.RuntimeException

class MainPageAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            0 -> {
                // 钱包页面
                AssetPage(PageAssetsBinding.inflate(inflater, parent, false).root)
            }
            1 -> {
                // 委托页面
                DelegatePage(PageDelegateBinding.inflate(inflater, parent, false).root)
            }
            else -> {
                throw RuntimeException("无法识别页面类型")
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return 2
    }

}