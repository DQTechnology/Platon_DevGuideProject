package com.digquant.entity

data class TransactionListTO(
    var walletAddrs: List<String>? = null,// 数据列表
    var beginSequence: Int = 1, // 页数
    var listSize: Int = 20, // 每一页显示的数量
    var direction: String = "new" //向前取最新的
)