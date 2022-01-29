package com.digquant.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiResponse<T>(
    val result: Int = 0,
    val code: Int? = 0,
    val data: T? = null,
    val errMsg: String = ""
)