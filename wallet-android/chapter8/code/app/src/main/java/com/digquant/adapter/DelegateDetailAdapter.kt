package com.digquant.adapter

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.R
import com.digquant.activity.DelegateActivity
import com.digquant.activity.WithdrawDelegateActivity
import com.digquant.databinding.ItemDelegateDetailListBinding
import com.digquant.entity.DelegateItemInfo
import com.digquant.service.NodeManager
import com.digquant.service.NodeStatus
import com.digquant.util.*
import com.platon.aton.widge.ShadowDrawable


class DelegateDetailItem(itemView: View, val adapter: DelegateDetailAdapter) :
    BaseViewHolder(itemView) {

    private val binding = ItemDelegateDetailListBinding.bind(itemView)
    override fun OnRender(position: Int) {
        val delegateItem = adapter.GetDelegateItem(position)



        ShadowDrawable.setShadowDrawable(
            binding.root,
            ResourceUtil.GetColor(R.color.color_ffffff),
            DensityUtil.DP2PX(itemView.context, 4.0f),
            ResourceUtil.GetColor(R.color.color_cc9ca7c2),
            DensityUtil.DP2PX(itemView.context, 10.0f),
            0,
            DensityUtil.DP2PX(itemView.context, 2.0f)
        )

        binding.nodeStatus.text = ResourceUtil.GetString(
            NodeManager.GetNodeStatusDescRes(
                delegateItem.nodeStatus,
                delegateItem.isConsensus
            )
        )

        // 节点状态颜色
        binding.nodeStatus.setTextColor(
            ResourceUtil.GetColor(
                getNodeStatusTextAndBorderColor(delegateItem.nodeStatus, delegateItem.isConsensus)
            )
        )

        binding.nodeName.text = delegateItem.nodeName

        binding.nodeAddress.text = AddressFormatUtil.formatAddress(delegateItem.nodeId)

        val delegateAmount = AmountUtil.formatAmountText(delegateItem.delegated)

        binding.delegatedAmount.text = delegateAmount

        binding.undelegatedAmount.text = AmountUtil.formatAmountText(delegateItem.released)

        binding.unclaimedRewardAmount.text =
            AmountUtil.formatAmountText(delegateItem.withdrawReward)
        //
        binding.layoutUnclaimReward.visibility =
            if (BigDecimalUtil.isBiggerThanZero(delegateItem.withdrawReward)) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.layoutDelegate.isEnabled = true

        /**
         * 跳转到委托页面
         */
        binding.layoutDelegate.setOnClickListener {

            val intent = Intent(itemView.context, DelegateActivity::class.java)
            intent.putExtra("nodeId", delegateItem.nodeId)
            intent.putExtra("nodeName", delegateItem.nodeName)
            intent.putExtra("url", delegateItem.url)

            intent.putExtra("walletName", adapter.GetWalletName())

            DXRouter.JumpTo(itemView.context, intent)
        }


        binding.layoutUndelegate.setOnClickListener {
            val intent = Intent(itemView.context, WithdrawDelegateActivity::class.java)
            intent.putExtra("nodeId", delegateItem.nodeId)
            intent.putExtra("nodeName", delegateItem.nodeName)
            intent.putExtra("url", delegateItem.url)
            intent.putExtra("delegateAmount", delegateAmount)

            intent.putExtra("walletName", adapter.GetWalletName())
            DXRouter.JumpTo(itemView.context, intent)
        }

    }

    private fun getNodeStatusTextAndBorderColor(nodeStatus: String?, isConsensus: Boolean): Int {
        return if (TextUtils.equals(NodeStatus.ACTIVE, nodeStatus)) {
            if (isConsensus) {
                R.color.color_f79d10
            } else {
                R.color.color_4a90e2
            }
        } else if (TextUtils.equals(NodeStatus.LOCKED, nodeStatus)) {
            R.color.color_808080
        } else {
            R.color.color_19a20e
        }
    }
}

/**
 * 钱包委托详情
 */
class DelegateDetailAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val delegateItemList = ArrayList<DelegateItemInfo>()

    private var walletName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {


        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemDelegateDetailListBinding.inflate(inflater, parent, false)

        return DelegateDetailItem(binding.root, this)

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }

    override fun getItemCount(): Int {
        return delegateItemList.size
    }


    fun GetDelegateItem(position: Int): DelegateItemInfo {
        return delegateItemList[position]
    }

    fun GetWalletName(): String {
        return this.walletName
    }

    fun UpdateData(walletName: String, data: List<DelegateItemInfo>?) {
        this.walletName = walletName
        delegateItemList.clear()

        if (data != null) {
            delegateItemList.addAll(data)
        }
        notifyDataSetChanged()
    }


}