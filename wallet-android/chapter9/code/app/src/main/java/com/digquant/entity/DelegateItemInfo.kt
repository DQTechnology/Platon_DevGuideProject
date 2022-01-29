package com.digquant.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DelegateItemInfo {
    /**
     * 投票节点Id（节点地址 ）
     */
     var nodeId: String? = null

    /**
     * 节点名称
     */
    var nodeName: String? = null

    /**
     * 机构官网（链接）
     */
    var website: String? = null

    /**
     * 节点头像
     */
    var url: String? = null

    /**
     * 竞选状态:
     * Active —— 活跃中
     * Candidate —— 候选中
     * Exiting —— 退出中
     * Exited —— 已退出
     * Locked —— 锁定中
     */
    var nodeStatus: String? = null

    /**
     * 已解除委托
     */
    var released: String? = null

    var walletAddress: String? = null

    /**
     * 是否为链初始化时内置的候选人
     * 0.7.3 新增字段
     */
    var isInit = false

    /**
     * 已委托  单位von
     */
    var delegated: String? = null

    /**
     * 0.7.5新增字段
     */
    var isConsensus = false

    /**
     * 待领取的奖励
     */
    var withdrawReward: String? = null

    fun getIsInit(): Boolean {
        return isInit
    }

    fun setIsInit(init: Boolean) {
        isInit = init
    }
    fun getIsConsensus(): Boolean {
        return isConsensus
    }

    fun setIsConsensus(consensus: Boolean) {
        isConsensus = consensus
    }

}