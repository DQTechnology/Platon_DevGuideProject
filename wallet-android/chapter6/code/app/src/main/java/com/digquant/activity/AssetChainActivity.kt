package com.digquant.activity

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.digquant.adapter.TransactionListAdapter
import com.digquant.api.AtonApi
import com.digquant.api.PlatonApi
import com.digquant.databinding.ActivityAssetsChainBinding
import com.digquant.entity.TransactionListTO
import com.digquant.service.WalletManager
import com.digquant.util.DXRouter
import com.digquant.util.ToastUtil
import com.digquant.util.ViewUtil
import kotlinx.coroutines.launch

class AssetChainActivity : BaseActivity() {

    lateinit var binding: ActivityAssetsChainBinding

    lateinit var adapter: TransactionListAdapter
    override fun inflateUI(inflater: LayoutInflater): View {
        ViewUtil.SetStatusBarColorToLight(window)
        binding = ActivityAssetsChainBinding.inflate(inflater, null, false);
        return binding.root
    }

    override fun initUI() {
        //
        ViewUtil.SetStatusBarMargin(binding.nodeBar)

        binding.walletName.text = WalletManager.GetCurrentWalletName()

        adapter = TransactionListAdapter()

        binding.txRecordList.layoutManager = LinearLayoutManager(this)

        binding.txRecordList.adapter = adapter

        binding.txRecordList.setEmptyView(binding.emptyTip.root)



    }

    private suspend fun getBalance() {
        val walletAddress = WalletManager.GetCurWalletAddress()
        val amount = PlatonApi.GetBalance(walletAddress)
        binding.walletAmount.text = amount.toString()
    }

    private suspend fun loadTransactionList() {
        val transactionListTO = TransactionListTO()
        val walletAddres = ArrayList<String>()
        walletAddres.add(WalletManager.GetCurWalletAddress())
        transactionListTO.walletAddrs = walletAddres
        try {
            val rsp = AtonApi.GetTransactionList(transactionListTO)
            if (rsp.code != 0) {
                ToastUtil.showLongToast(this@AssetChainActivity, rsp.errMsg)
                return
            }
            var dataList = rsp.data
            if (dataList == null) {
                dataList = ArrayList()
            }
            adapter.setTransactionList(dataList)

        } catch (e: Exception) {


        }
    }


    override fun initEvent() {

        binding.sendBtn.setOnClickListener {
            DXRouter.JumpTo(this, SendTransactionActivity::class.java)
        }

        binding.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                getBalance()
                loadTransactionList()
                binding.refreshLayout.finishRefresh(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshLayout.autoRefresh()
    }
}