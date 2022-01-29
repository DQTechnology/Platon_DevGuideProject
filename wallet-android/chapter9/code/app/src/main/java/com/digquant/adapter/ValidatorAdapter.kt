package com.digquant.adapter

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digquant.R
import com.digquant.activity.DelegateActivity

import com.digquant.databinding.ItemValidatorListBinding
import com.digquant.entity.VerifyNode
import com.digquant.service.NodeManager
import com.digquant.service.NodeStatus
import com.digquant.util.*
import com.platon.aton.widge.ShadowDrawable


class ValidatorItem(itemView: View, val adapter: ValidatorAdapter) :
    BaseViewHolder(itemView) {
    private val binding = ItemValidatorListBinding.bind(itemView)

    override fun OnRender(position: Int) {

        val nodeInfo = adapter.GetNodeData(position)

        ShadowDrawable.setShadowDrawable(
            binding.root,
            ResourceUtil.GetColor(R.color.color_ffffff),
            DensityUtil.DP2PX(itemView.context, 4.0f),
            ResourceUtil.GetColor(R.color.color_cc9ca7c2),
            DensityUtil.DP2PX(itemView.context, 8.0f),
            0,
            DensityUtil.DP2PX(itemView.context, 0.0f)
        )

        Glide.with(itemView.context).load(nodeInfo.url)
            .error(R.mipmap.icon_validators_default)
            .placeholder(R.mipmap.icon_validators_default).into(binding.nodeAvatar)


        binding.nodeName.text = nodeInfo.name
        // 节点委托LAT总数 / 人数
        binding.delegatedAmount.text = "${
            AmountUtil.convertVonToLatWithFractionDigits(
                nodeInfo.delegateSum,
                2
            )
        } ${ResourceUtil.GetString(R.string.amount_with_unit)} / ${
            StringUtil.formatBalance(
                nodeInfo.delegate,
                0,
                0
            )
        }"

        // 节点状态
        binding.nodeState.text = ResourceUtil.GetString(
            NodeManager.GetNodeStatusDescRes(
                nodeInfo.nodeStatus,
                nodeInfo.isConsensus
            )
        )
        // 节点状态颜色
        binding.nodeState.setTextColor(
            ResourceUtil.GetColor(
                getNodeStatusTextAndBorderColor(nodeInfo.nodeStatus, nodeInfo.isConsensus)
            )
        )
        // 节点状态颜色
        binding.nodeState.setRoundedBorderColor(
            ResourceUtil.GetColor(
                getNodeStatusTextAndBorderColor(nodeInfo.nodeStatus, nodeInfo.isConsensus)
            )
        )
        // 节点排名
        binding.nodeRank.text = nodeInfo.ranking.toString()
        // 设置节点排名的背景
        binding.nodeRank.setBackgroundResource(getRankBackground(nodeInfo.ranking))
        //
        binding.nodeRank.textSize = getRankTextSize(nodeInfo.ranking)
        // 显示收益率
        binding.annualYield.text = getShowDelegatedRatePA(nodeInfo)


        binding.root.setOnClickListener {

            val intent = Intent(itemView.context, DelegateActivity::class.java)
            intent.putExtra("nodeId", nodeInfo.nodeId)
            intent.putExtra("nodeName", nodeInfo.name)
            intent.putExtra("url", nodeInfo.url)

            DXRouter.JumpTo(itemView.context, intent)
        }

    }

    private fun getShowDelegatedRatePA(nodeInfo: VerifyNode): String {


        return if (nodeInfo.isInit) {
            "--"
        } else {
            "${
                StringUtil.formatBalance(
                    NumberParserUtils.getPrettyBalance(
                        BigDecimalUtil.div(
                            nodeInfo.delegatedRatePA,
                            "100"
                        )
                    ), 2
                )
            }%"
        }


    }

    private fun getRankTextSize(rank: Int): Float {
        return if (rank >= 1000) 10.0f else 11.0f
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

    private fun getRankBackground(rank: Int): Int {
        return when (rank) {
            1 -> R.mipmap.icon_rank_first
            2 -> R.mipmap.icon_rank_second
            3 -> R.mipmap.icon_rank_third
            else -> R.mipmap.icon_rank_others
        }
    }
}


class ValidatorAdapter : RecyclerView.Adapter<BaseViewHolder>() {


    private val nodeList = ArrayList<VerifyNode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemValidatorListBinding.inflate(inflater, parent, false)

        return ValidatorItem(binding.root, this)
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }


    fun GetNodeData(position: Int): VerifyNode {
        return nodeList[position]
    }


    override fun getItemCount(): Int {
        return nodeList.size
    }

    fun UpdateData(data: List<VerifyNode>?) {

        nodeList.clear()

        if (data != null) {
            nodeList.addAll(data)
        }
        notifyDataSetChanged()
    }
}