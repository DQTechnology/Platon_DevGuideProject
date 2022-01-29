package com.digquant.page

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.digquant.adapter.BaseViewHolder
import com.digquant.adapter.ValidatorAdapter
import com.digquant.api.AtonApi
import com.digquant.databinding.PageValidatorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ValidatorPage(itemView: View) : BaseViewHolder(itemView) {

    private val binding: PageValidatorBinding = PageValidatorBinding.bind(itemView)

    private val adapter = ValidatorAdapter()

    init {
        initUI()
        initEvent()
    }

    override fun OnRender(position: Int) {
        binding.refresher.autoRefresh()
    }

    private fun initUI() {
        binding.validatorList.layoutManager = LinearLayoutManager(itemView.context)
        binding.validatorList.adapter = adapter
        binding.validatorList.setEmptyView(binding.emptyTip)
    }

    private suspend fun loadData() {

        val verifyNodeListRsp = AtonApi.GetVerifyNodeList()

        adapter.UpdateData(verifyNodeListRsp.data)

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