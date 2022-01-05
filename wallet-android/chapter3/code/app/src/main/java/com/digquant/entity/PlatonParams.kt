package com.digquant.entity

data class PlatonParams(
    val jsonrpc: String = "2.0",
    val method: String,
    var params: List<Any>,
    var id: Long
) ;