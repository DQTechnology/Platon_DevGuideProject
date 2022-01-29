package com.digquant.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class VerifyNode {
    /**
     * 节点ID
     */
    var nodeId: String? = null

    /**
     * 质押排名
     */

    var ranking: Int = 0

    /**
     * 节点名称
     */

    var name: String? = null

    /**
     * 节点头像
     */

    var url: String? = null

    /**
     * 预计年化率
     */

    var delegatedRatePA: String? = null

    /**
     * 竞选状态
     * Active —— 活跃中
     * Candidate —— 候选中
     * Locked —— 锁定中
     */

    var nodeStatus: String? = null

    /**
     * 是否为链初始化时内置的候选人
     */
    var isInit: Boolean = false

    /**
     * 是否共识中
     */
    var isConsensus: Boolean = false


    /**
     * 总押金
     */
    var delegateSum: String? = null

    /**
     * 委托者数
     */
    var delegate: String? = null

    fun setIsConsensus(isConsensus: Boolean) {
        this.isConsensus = isConsensus
    }

    fun setIsInit(isInit: Boolean) {
        this.isInit = isInit
    }

}