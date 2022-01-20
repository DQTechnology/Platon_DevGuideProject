package com.digquant.service

import android.text.TextUtils
import com.digquant.R


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


}