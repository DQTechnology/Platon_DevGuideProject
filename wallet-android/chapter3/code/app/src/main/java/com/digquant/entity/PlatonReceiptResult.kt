package com.digquant.entity

data class PlatonReceiptResult(
    var id: Long = 0,
    var jsonrpc: String = "",
    var result: PlatonReceiptInfo? = null
)
