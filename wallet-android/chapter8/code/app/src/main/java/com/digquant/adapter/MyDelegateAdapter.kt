package com.digquant.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digquant.R
import com.digquant.activity.DelegateDetailActivity
import com.digquant.databinding.ItemEmptyDelegateTipBinding
import com.digquant.databinding.ItemMyDelegateListBinding
import com.digquant.databinding.ItemTotalDelegatedBinding
import com.digquant.dialog.ClaimRewardsDialog

import com.digquant.entity.DelegateInfo
import com.digquant.service.WalletManager
import com.digquant.util.*
import com.platon.aton.widge.ShadowDrawable
import java.lang.RuntimeException
import java.math.BigInteger

class EmptyDelegateTipItem(itemView: View) : BaseViewHolder(itemView) {

    private val binding = ItemEmptyDelegateTipBinding.bind(itemView)

}

class TotalDelegatedItem(itemView: View, val adapter: MyDelegateAdapter) :
    BaseViewHolder(itemView) {

    private val binding = ItemTotalDelegatedBinding.bind(itemView)

    override fun OnRender(position: Int) {


        binding.totalDelegatedAmount.text =
            AmountUtil.formatAmountText(adapter.totalDelegateLAT.toString())

        binding.totalUnclaimedRewardAmount.text =
            AmountUtil.formatAmountText(adapter.canWithdrawReward.toString())

        binding.totalRewardAmount.text =
            AmountUtil.formatAmountText(adapter.cumulativeReward.toString())
    }
}

/**
 * 钱包委托显示
 */
class MyDelegateItem(itemView: View, val adapter: MyDelegateAdapter) :
    BaseViewHolder(itemView) {

    private val binding = ItemMyDelegateListBinding.bind(itemView)

    override fun OnRender(position: Int) {


        ShadowDrawable.setShadowDrawable(
            binding.root,
            ResourceUtil.GetColor(R.color.color_ffffff),
            DensityUtil.DP2PX(itemView.context, 4.0f),
            ResourceUtil.GetColor(R.color.color_cc9ca7c2),
            DensityUtil.DP2PX(itemView.context, 10.0f),
            0,
            DensityUtil.DP2PX(itemView.context, 2.0f)
        )

        val delegateInfo = adapter.GetDelegateData(position)

        /**
         * 显示钱包详情
         */
        val imgResId = RUtils.drawable(delegateInfo.walletIcon);
        if (imgResId != -1) {
            binding.walletAvatar.setImageResource(imgResId)
        } else {
            binding.walletAvatar.setImageResource(R.mipmap.avatar_14)
        }


        val walletName = WalletManager.GetWalletNameByAddress(delegateInfo.walletAddress)

        binding.walletName.text = walletName

        binding.walletAddress.text = AddressFormatUtil.formatAddress(delegateInfo.walletAddress)


        val withdrawReward = AmountUtil.formatAmountText(delegateInfo.withdrawReward, 8)

        binding.unclaimedRewardAmount.text = CommonTextUtils.getPriceTextWithBold(
            withdrawReward,
            ResourceUtil.GetColor(R.color.color_000000),
            ResourceUtil.GetColor(R.color.color_000000),
            DensityUtil.DP2PX(itemView.context, 14.0f).toFloat(),
            DensityUtil.DP2PX(itemView.context, 16.0f).toFloat()
        )

        binding.totalRewardAmount.text =
            AmountUtil.formatAmountText(delegateInfo.cumulativeReward, 8)

        binding.delegatedAmount.text = AmountUtil.formatAmountText(delegateInfo.delegated)




        binding.layoutClaimReward.visibility =
            if (BigDecimalUtil.isBiggerThanZero(delegateInfo.withdrawReward)) View.VISIBLE else View.GONE

        binding.layoutClaimReward.isEnabled = !delegateInfo.isPending

        if (delegateInfo.isPending) {
            binding.claimReward.visibility = View.GONE
            binding.layoutClaimRewardAnimation.visibility = View.VISIBLE

        } else {
            binding.claimReward.visibility = View.VISIBLE
            binding.layoutClaimRewardAnimation.visibility = View.GONE
        }

        binding.root.setOnClickListener {

            val intent = Intent(itemView.context, DelegateDetailActivity::class.java)

            intent.putExtra("walletAddress", delegateInfo.walletAddress)

            intent.putExtra("walletName", walletName)

            DXRouter.JumpTo(itemView.context, intent)
        }

        /**
         * 显示领取奖励的页面
         */
        binding.claimReward.setOnClickListener {

            val dialog = ClaimRewardsDialog(itemView.context, walletName, withdrawReward)


            dialog.show()
        }

    }
}

class MyDelegateAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private var delegateNodeList = ArrayList<DelegateInfo>()

    /**
     * 总委托的LAT数量
     */
    var totalDelegateLAT = BigInteger("0")

    /**
     * 待领取的奖励
     */
    var canWithdrawReward = BigInteger("0")

    /**
     * 累计奖励
     */
    var cumulativeReward = BigInteger("0")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)


        when (viewType) {
            0 -> {
                val binding = ItemTotalDelegatedBinding.inflate(inflater, parent, false)

                return TotalDelegatedItem(binding.root, this)
            }
            1 -> {
                val binding = ItemEmptyDelegateTipBinding.inflate(inflater, parent, false)

                return EmptyDelegateTipItem(binding.root)
            }
            2 -> {
                val binding = ItemMyDelegateListBinding.inflate(inflater, parent, false)

                return MyDelegateItem(binding.root, this)
            }
            else -> {
                throw RuntimeException("")
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || delegateNodeList.size == 0) {
            position
        } else {
            2
        }
    }


    fun GetDelegateData(position: Int): DelegateInfo {
        return delegateNodeList[position]
    }


    fun UpdateData(delegateList: List<DelegateInfo>?) {

        totalDelegateLAT = BigInteger("0")
        canWithdrawReward = BigInteger("0")
        cumulativeReward = BigInteger("0")

        delegateList?.forEach {
            if (it.delegated != null) {
                totalDelegateLAT = totalDelegateLAT.add(it.delegated!!.toBigInteger())
            }
            if (it.withdrawReward != null) {
                canWithdrawReward = canWithdrawReward.add(it.withdrawReward!!.toBigInteger())
            }
            if (it.cumulativeReward != null) {
                cumulativeReward = cumulativeReward.add(it.cumulativeReward!!.toBigInteger())
            }
        }


        this.delegateNodeList.clear()
        if (delegateList != null) {
            this.delegateNodeList.addAll(delegateList)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (delegateNodeList.size == 0) {
            return 2
        }
        return delegateNodeList.size + 1
    }
}