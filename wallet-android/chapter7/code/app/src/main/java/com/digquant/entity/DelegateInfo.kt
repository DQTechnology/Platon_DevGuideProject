package com.digquant.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DelegateInfo {
    /**
     * 钱包名称
     */
    var walletName: String? = null

    /**
     * 钱包地址
     */

    var walletAddress: String? = null

    /**
     * 钱包头像
     */

    var walletIcon: String? = null

    /**
     * 累计的奖励  单位von   1LAT(ETH)=1000000000000000000von(wei)
     */

    var cumulativeReward: String? = null

    /**
     * 待领取的奖励，单位von
     */

    var withdrawReward: String? = null

    /**
     * 总委托  单位von   1LAT(ETH)=1000000000000000000von(wei)
     */

    var delegated: String? = null

    /**
     * 是否正在pending
     */

    var isPending: Boolean = false

    /**
     * 是否是观察者钱包
     */

    var isObservedWallet: Boolean = false

    fun setIsObservedWallet(isObservedWallet: Boolean) {
        this.isObservedWallet = isObservedWallet
    }

    fun setIsPending(isPending: Boolean) {
        this.isPending = isPending
    }


}
