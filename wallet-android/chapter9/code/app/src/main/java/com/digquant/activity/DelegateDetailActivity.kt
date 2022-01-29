package com.digquant.activity

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.digquant.adapter.DelegateDetailAdapter
import com.digquant.api.AtonApi
import com.digquant.databinding.ActivityDelegateDetailBinding
import com.digquant.entity.DetailDelegateTO
import com.digquant.util.AddressFormatUtil
import com.digquant.util.AmountUtil
import kotlinx.coroutines.launch

class DelegateDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDelegateDetailBinding

    private val adapter = DelegateDetailAdapter()

    private lateinit var walletAddress: String
    private lateinit var walletName: String

    override fun inflateUI(inflater: LayoutInflater): View {
        binding = ActivityDelegateDetailBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun initUI() {

        walletAddress = intent.getStringExtra("walletAddress")!!

        walletName = intent.getStringExtra("walletName")!!

        binding.recordList.layoutManager = LinearLayoutManager(this)

        binding.recordList.adapter = adapter
        // 显示钱包名
        binding.walletName.text = walletName

        binding.walletAddress.text = AddressFormatUtil.formatAddress(walletAddress)

        binding.recordList.setEmptyView(binding.emptyTip)
    }


    private suspend fun loadData() {

        val param = DetailDelegateTO(walletAddress)
        // 获取委托详情列表
        val delegateDetail = AtonApi.GetDelegateDetailList(param).data


        if (delegateDetail != null) {
            binding.avaliableBalanceAmount.text =
                AmountUtil.formatAmountText(delegateDetail.availableDelegationBalance)
            binding.totalDelegatedAmount.text =
                AmountUtil.formatAmountText(delegateDetail.delegated)

            adapter.UpdateData(walletName, delegateDetail.item)
        }


        binding.refresher.finishRefresh(true)

    }

    override fun initEvent() {

        binding.refresher.setOnRefreshListener {

            lifecycleScope.launch {
                loadData()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.refresher.autoRefresh()
    }


}