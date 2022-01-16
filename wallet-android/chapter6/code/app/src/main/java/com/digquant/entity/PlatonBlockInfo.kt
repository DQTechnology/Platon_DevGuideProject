package com.digquant.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.web3j.utils.Numeric

@JsonIgnoreProperties(ignoreUnknown = true)
class PlatonBlockInfo {
    /**
     *  块编号
     */
    var number: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     * 块哈希，处于 pending 状态的块为 null
     */
    var hash: String = ""

    /**
     * 父块哈希
     */
    var parentHash: String = ""

    /**
     * 生成的 proof-of-work 的哈希，处于 pending 状态的块为 null
     */
    var nonce: String = ""

    /**
     * 块中日志的 bloom filter，处于 pending 状态的块为 null
     */
    var logsBloom: String = ""

    /**
     * 块中的交易树根节点
     */
    var transactionsRoot: String = ""

    /**
     * 块中的最终状态树根节点
     */
    var stateRoot: String = ""

    /**
     *  接收奖励的矿工地址
     */
    var miner: String = ""

    /**
     * 块 “extra data” 字段
     */
    var extraData: String = ""

    /**
     * 字节为单位的块大小
     */
    var size: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     *  该块允许的最大 gas 值
     */
    var gasLimit: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     * 该块中所有交易使用的 gas 总量
     */
    var gasUsed: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }

    /**
     * 出块的 unix 时间戳
     */
    var timestamp: String = ""
        set(value) {
            field = Numeric.decodeQuantity(value).toString()
        }
    var transactions: List<Any> = ArrayList()

}