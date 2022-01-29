package com.digquant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.databinding.PageCommonRecyclerviewBinding
import com.digquant.page.AssetRecyclerPage


class AssetPageAdapter : RecyclerView.Adapter<BaseViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageCommonRecyclerviewBinding.inflate(inflater, parent, false)
        return AssetRecyclerPage(binding.root)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }

    override fun getItemCount(): Int {
        return 1
    }
}