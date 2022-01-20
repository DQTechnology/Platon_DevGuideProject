package com.digquant.entity

import com.alibaba.fastjson.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DelegateNodeDetail {


     var availableDelegationBalance: String? = null

     var delegated: String? = null

    @JSONField(name = "item")
     var item: List<DelegateItemInfo>? = null


}