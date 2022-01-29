package com.digquant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.databinding.ItemNodeSettingsBinding
import com.digquant.event.ChangeNodeEvent
import com.digquant.event.ChangeWalletEvent
import com.digquant.service.NodeManager
import org.greenrobot.eventbus.EventBus


class NodeItem(itemView: View) : BaseViewHolder(itemView) {


    private val binding = ItemNodeSettingsBinding.bind(itemView)

    override fun OnRender(position: Int) {

        val nodeInfo = NodeManager.GetNodeInfoByIndex(position)


        if (position == NodeManager.GetCurNodeIndex()) {
            binding.selected.visibility = View.VISIBLE
        } else {
            binding.selected.visibility = View.GONE
        }


        binding.nodeName.text = "${nodeInfo.nodeName}(${nodeInfo.hrp})"

        binding.nodeInfo.text = "${nodeInfo.atonUrl}(${nodeInfo.chainId})"

        binding.root.setOnClickListener {
            NodeManager.SwitchNode(position)

            EventBus.getDefault().post(ChangeNodeEvent())
        }

    }
}


class NodeAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return NodeItem(ItemNodeSettingsBinding.inflate(inflater, parent, false).root)

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }

    override fun getItemCount(): Int {
        return NodeManager.GetNodeNum()
    }
}