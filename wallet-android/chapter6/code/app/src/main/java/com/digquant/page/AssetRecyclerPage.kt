package com.digquant.page

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.digquant.R
import com.digquant.adapter.AssetRecyclerAdAdapter
import com.digquant.adapter.BaseViewHolder
import com.digquant.api.PlatonApi
import com.digquant.databinding.PageCommonRecyclerviewBinding
import com.digquant.entity.AssetItemData
import com.digquant.service.WalletManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AssetRecyclerPage(itemView: View) : BaseViewHolder(itemView) {

    private val binding = PageCommonRecyclerviewBinding.bind(itemView)

    private lateinit var adapter: AssetRecyclerAdAdapter

    init {

        initUI()
        initEvent()
    }

    private fun initUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(itemView.context)

        adapter = AssetRecyclerAdAdapter()

        binding.recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.Main) {
            loadAsset()
        }


    }

    private suspend fun loadAsset() {

        val walletAddress = WalletManager.GetCurWalletAddress()

        val lat = PlatonApi.GetBalance(walletAddress)

        val assetItemData = AssetItemData(R.mipmap.icon_platon_item_default, "LAT", lat)

        val itemList = ArrayList<AssetItemData>()

        itemList.add(assetItemData)

        adapter.UpdateData(itemList)
    }


    private fun initEvent() {
        /**
         * 下拉刷新頁面
         */
        binding.root.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Main) {
                loadAsset()
                binding.root.finishRefresh(true)
            }
        }
    }


    override fun OnRender(position: Int) {


    }
}