package com.digquant.service

import android.content.Context
import com.DiggingApplication
import com.digquant.R
import com.digquant.entity.NodeInfo
import com.digquant.util.ThreadPoolUtil

object NodeStatus {
    /**
     * 所有类型
     */
    val ALL = "All"

    /**
     * 活跃中
     */
    val ACTIVE = "Active"

    /**
     * 候选中
     */
    val CANDIDATE = "Candidate"

    /**
     * 锁定中
     */
    val LOCKED = "Locked"

    /**
     * / **
     * 退出中
     */
    val EXITING = "Exiting"

    /**
     * 已退出
     */
    val EXITED = "Exited"
}

object NodeManager {




    private val nodeList = ArrayList<NodeInfo>()

    private lateinit var curNodeInfo: NodeInfo

    private var curNodeIndex: Int = 0


    fun GetNodeStatusDescRes( nodeStatus: String?, isConsensus: Boolean?): Int {
        return when (nodeStatus) {
            NodeStatus.ACTIVE -> if (isConsensus != null && isConsensus)  {
                R.string.validators_verifying
            } else {
                R.string.validators_active
            }
            NodeStatus.CANDIDATE -> R.string.validators_candidate
            NodeStatus.LOCKED -> R.string.validators_locked
            NodeStatus.EXITING -> R.string.validators_state_exiting
            NodeStatus.EXITED -> R.string.validators_state_exited
            else -> R.string.unknown
        }
    }

    fun LoadNodeConfig() {
        nodeList.add(
            NodeInfo(
                nodeName = "PLATON 开发网络",
                hrp = "LAT",
                nodeUrl = "http://35.247.155.162:6789",
                chainId = 210309,
                atonUrl = "https://aton-dev.platon.network",
                themeColor = "#04081f",
                isSelected = false
            )
        )

        nodeList.add(
            NodeInfo(
                nodeName = "PLATON 主网络",
                hrp = "LAT",
                nodeUrl = "https://samurai.platon.network",
                chainId = 100,
                atonUrl = "https://aton.platon.network",
                themeColor = "#0912D4",
                isSelected = false
            )
        )
        // 设置

        val context = DiggingApplication.context

        val sp = context.getSharedPreferences("nodeInfo", Context.MODE_PRIVATE)
        curNodeIndex = sp.getInt("nodeIndex", 0)

        curNodeInfo = nodeList[curNodeIndex]
    }


    fun GetNodeInfoByIndex(position: Int): NodeInfo {
        return nodeList[position]
    }

    fun GetNodeNum(): Int {
        return nodeList.size
    }

    fun GetCurNodeIndex(): Int {
        return curNodeIndex
    }

    fun GetCurNodeInfo(): NodeInfo {
        return curNodeInfo
    }

    fun SwitchNode(index: Int) {
        curNodeInfo = nodeList[index]
        curNodeIndex = index
        ThreadPoolUtil.AddTask {
            val context = DiggingApplication.context
            val sp = context.getSharedPreferences("nodeInfo", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putInt("nodeIndex", index)
            editor.commit()
        }

    }

}