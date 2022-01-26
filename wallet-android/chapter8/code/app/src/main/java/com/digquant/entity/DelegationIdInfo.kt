package com.digquant.entity


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigInteger

@JsonIgnoreProperties(ignoreUnknown = true)
class DelegationIdInfo {
    var addr: String? = null //  验证人节点的地址

    var nodeId: String? = null //  验证人的节点Id

    var stakingBlockNum: BigInteger? = null // 发起委托时的区块高度
}