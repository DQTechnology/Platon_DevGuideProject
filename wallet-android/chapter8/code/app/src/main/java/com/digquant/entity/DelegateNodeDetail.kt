package com.digquant.entity

import com.alibaba.fastjson.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DelegateNodeDetail {

    /**
     * 可委托LAT
     */
    var availableDelegationBalance: String? = null

    /**
     * 总委托LAT数量
     */
    var delegated: String? = null

    /**
     * 委托的节点列表
     */
    @JSONField(name = "item")
    var item: List<DelegateItemInfo>? = null

}