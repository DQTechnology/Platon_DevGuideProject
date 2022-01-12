package com.digquant.entity


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.web3j.utils.Numeric


@JsonIgnoreProperties(ignoreUnknown = true)
class PlatonReceiptInfo {
    /**
     * 区块的哈希值。 等待被打包时为null 。
     */
    var blockHash: String = ""

    /**
     * 20字节 - 交易的发送地址。
     */
    var from: String = ""

    /**
     * 32字节 - 创建此日志的事务的哈希值。当其挂起的日志时为null。
     */
    var transactionHash: String = ""

    /**
     *  区块中交易索引的位置，未打包时为null。
     */
    var transactionIndex: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     * 该交易所在的区块号。null，待处理。
     */
    var blockNumber: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     *  在区块中执行此交易时使用的天然气总量。

     */
    var cumulativeGasUsed: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     *
     */
    var contractAddress: String? = null

    /**
     * 成功状态 成功为1 失败为0
     */
    var status: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     *  仅此特定交易使用的天然气量。
     */
    var gasUsed: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     * 此事务生成的日志对象数组。
     */
    var logs: List<Any> = ArrayList();

}