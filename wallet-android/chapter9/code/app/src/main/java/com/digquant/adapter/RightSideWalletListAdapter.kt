package com.digquant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.R
import com.digquant.api.PlatonApi
import com.digquant.databinding.ItemSidebarWalletListBinding
import com.digquant.event.ChangeWalletEvent
import com.digquant.service.WalletManager
import com.digquant.util.AddressFormatUtil
import com.digquant.util.ResourceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


class WalletItem(itemView: View, val adapter: RightSideWalletListAdapter) :
    BaseViewHolder(itemView) {

    private val binding = ItemSidebarWalletListBinding.bind(itemView)

    override fun OnRender(position: Int) {

        val walletAddress = adapter.GetAddress(position)

        binding.walletAddress.text = AddressFormatUtil.formatAddress(walletAddress)

        val walletName = WalletManager.GetWalletNameByAddress(walletAddress)

        binding.walletName.text = walletName

        if (walletName == WalletManager.GetCurrentWalletName()) {

            binding.linearViewItem.background = ResourceUtil.GetDrawable(R.drawable.bg_item_sidebar_wallet)
        } else {
            binding.linearViewItem.background = ResourceUtil.GetDrawable(R.drawable.bg_item_sidebar_wallet_default)
        }

        GlobalScope.launch(Dispatchers.Main) {
            val balance = PlatonApi.GetBalance(walletAddress)
            binding.walletBalance.text = balance.toString()
        }

        binding.root.setOnClickListener {

            WalletManager.SwitchWallet(walletName)

            EventBus.getDefault().post(ChangeWalletEvent())
        }

    }

}


class RightSideWalletListAdapter : RecyclerView.Adapter<BaseViewHolder>() {


    private var walletAddressList: List<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return WalletItem(ItemSidebarWalletListBinding.inflate(inflater, parent, false).root, this)

    }


    fun UpdateData() {

        walletAddressList = WalletManager.GetAllWalletAddress()

        notifyDataSetChanged()

    }

    fun GetAddress(position: Int): String {
        return walletAddressList[position]
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }

    override fun getItemCount(): Int {

        return walletAddressList.size
    }
}