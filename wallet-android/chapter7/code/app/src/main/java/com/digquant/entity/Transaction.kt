package com.digquant.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Transaction(
    /**
     * 交易hash
     */
    val hash: String? = null,

    /**
     * 当前交易所在快高
     */
    val blockNumber: String? = null,

    /**
     * 当前交易的链id
     */
    val chainId: String? = null,

    /**
     * 交易实际花费值(手续费)，单位：wei
     * “21168000000000”
     */
    val actualTxCost: String? = null,

    /**
     * 交易发送方
     */
    val from: String? = null,

    /**
     * 交易接收方
     */
    val to: String? = null,

    /**
     * 交易序列号
     */
    val sequence: Long = 0,

    /**
     * 交易状态2 pending 1 成功 0 失败
     */
    val txReceiptStatus: Int = 0,

    /**
     * 0: 转账
     * 1: 合约发布(合约创建)
     * 2: 合约调用(合约执行)
     * 3: 其他收入
     * 4: 其他支出
     * 5: MPC交易
     * 1000: 发起质押(创建验证人)
     * 1001: 修改质押信息(编辑验证人)
     * 1002: 增持质押(增加自有质押)
     * 1003: 撤销质押(退出验证人)
     * 1004: 发起委托(委托)
     * 1005: 减持/撤销委托(赎回委托)
     * 2000: 提交文本提案(创建提案)
     * 2001: 提交升级提案(创建提案)
     * 2002: 提交参数提案(创建提案)
     * 2003: 给提案投票(提案投票)
     * 2004: 版本声明
     * 3000: 举报多签(举报验证人)
     * 4000: 创建锁仓计划(创建锁仓)
     */
    val txType: String? = null,

    /**
     * 交易金额
     */
    val value: String? = null,

    /**
     * 发送者钱包名称
     */
    val senderWalletName: String? = null,

    /**
     * {json}交易详细信息
     */
    val txInfo: String? = null,

    /**
     * 提交时间（单位：毫秒）
     */
    val timestamp: Long = 0,

    /**
     * to类型
     * contract —— 合约
     * address —— 地址
     */
    val toType: String? = null,

    /**
     * Sent发送/Receive接收
     */
    val direction: String? = null,

    /**
     * 节点名称/委托给/验证人
     * //txType = 1004,1005,1000,1001,1002,1003,3000,2000,2001,2002,2003,2004,nodeName不为空
     * 详细描述：txType =  2000,2001,2002,2003(验证人)
     * 详细描述：txType =  1004,1005(委托给，同时也是节点名称)
     */
    val nodeName: String? = null,

    /**
     * txType =  1004,1005,1000,1001,1002,1003,3000,2004,nodeId不为空
     */
    val nodeId: String? = null,

    /**
     * txType =  4000,lockAddress不为空
     */
    val lockAddress: String? = null,

    /**
     * 举报类型
     */
    val reportType: String? = null,

    /**
     * 版本
     */
    val version: String? = null,

    /**
     * 提案id(截取最后一个破折号)
     */
    val url: String? = null,

    /**
     * PIP编号   eip-100(EIP-由前端拼接)
     */
    val piDID: String? = null,

    /**
     * 提案类型
     */
    val proposalType: String? = null,

    /**
     * 提案id
     */
    val proposalId: String? = null,

    /**
     * 投票
     */
    val vote: String? = null,


    //=======================下面是新加的三个字段=================================
    //=======================下面是新加的三个字段=================================
    /**
     * //赎回状态， 1： 退回中   2：退回成功     赎回失败查看交易txReceiptStatus
     */
    val redeemStatus: String? = null,

    /**
     * 钱包头像
     */
    val walletIcon: String? = null,

    /**
     * 钱包名称
     */
    val walletName: String? = null,

    /**
     * "unDelegation":"10000",       //赎回金额 txType = 1005(赎回数量)
     */
    val unDelegation: String? = null,

    /**
     * 质押金额 txType = 1003(退回数量)
     */
    val stakingValue: String? = null,

    /**
     * 领取数量 单位von   1LAT(ETH)=1000000000000000000von(wei)
     */
    val totalReward: String? = null,

    /**
     * 交易备注
     */
    val remark: String? = null,
)
