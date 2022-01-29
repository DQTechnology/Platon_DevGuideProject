package com.digquant.entity

data class NodeInfo(
    val nodeName: String,
    var hrp: String,
    val nodeUrl: String,
    val chainId: Long,
    val atonUrl: String,
    val themeColor: String,
    var isSelected: Boolean = false
)
