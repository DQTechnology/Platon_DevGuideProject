package com.digquant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.databinding.PageMyDelegateBinding
import com.digquant.databinding.PageValidatorBinding
import com.digquant.page.MyDelegatePage
import com.digquant.page.ValidatorPage

import java.lang.RuntimeException

class DelegatePageAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            0 -> {
                val binding = PageMyDelegateBinding.inflate(inflater, parent, false)
                MyDelegatePage(binding.root)
            }
            1 -> {
                val binding = PageValidatorBinding.inflate(inflater, parent, false)
                ValidatorPage(binding.root)
            }
            else -> {
                throw RuntimeException("无法识别页面类型")
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return position;
    }

    override fun getItemCount(): Int {
        return 2
    }


}