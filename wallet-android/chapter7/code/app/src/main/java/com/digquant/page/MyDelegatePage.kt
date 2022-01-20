package com.digquant.page

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.digquant.adapter.BaseViewHolder
import com.digquant.adapter.MyDelegateAdapter
import com.digquant.api.AtonApi
import com.digquant.databinding.PageMyDelegateBinding
import com.digquant.entity.ApiResponse
import com.digquant.entity.DelegateNodeDetail
import com.digquant.entity.DetailDelegateTO
import com.digquant.entity.MyDelegateTO
import com.digquant.service.WalletManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MyDelegatePage(itemView: View) : BaseViewHolder(itemView) {

    private val binding: PageMyDelegateBinding =
        PageMyDelegateBinding.bind(itemView)

    private val adapter = MyDelegateAdapter()

    init {
        initUI()
        initEvent()
    }

    override fun OnRender(position: Int) {
        binding.refresher.autoRefresh()
    }

    private fun initUI() {
        val layoutManager = LinearLayoutManager(itemView.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        binding.delegateList.layoutManager = layoutManager
        binding.delegateList.adapter = adapter
    }


    private suspend fun loadData() {

        val walletAddrs = ArrayList<String>()
        /**
         * 获取所有的钱包地址
         */
        walletAddrs.addAll(WalletManager.GetAllWalletAddress())
        val myDelegateTO = MyDelegateTO(walletAddrs)
        // 获取钱包信息
        val delegateInfoRsp = AtonApi.GetMyDelegateList(myDelegateTO)
        // 获取委托节点详情
        adapter.UpdateData(delegateInfoRsp.data)

        binding.refresher.finishRefresh(true)
    }

    private fun initEvent() {
        binding.refresher.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Main) {
                loadData()

            }
        }
    }
}