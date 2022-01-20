package com.digquant.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlatonResult(
    var id: Long = 0,
    var jsonrpc: String = "",
    var result: String = "",
)
