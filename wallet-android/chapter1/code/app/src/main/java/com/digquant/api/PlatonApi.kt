package com.digquant.api

import okhttp3.OkHttpClient
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.PlatonGetBalance
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger
import java.util.concurrent.TimeUnit


class PlatonApi {
    //
    companion object {
        // 测试节点地址
        const val url: String = "http://35.247.155.162:6789"

        //连接超时时间
        const val TRANSACTION_SEND_CONNECT_TIMEOUT: Long = 10000

        // 读取超时
        const val TRANSACTION_SEND_READ_TIMEOUT: Long = 50000

        // PlatonApi为单例
        val platonApi: PlatonApi = PlatonApi()
    }

    // 手续费
    private val GasPrice: BigInteger = BigInteger.valueOf(2000000000000L)

    // 手续费上线
    private val GasLimit: BigInteger = BigInteger.valueOf(99999L)

    // 手续费类
    var gasProvider = ContractGasProvider(GasPrice, GasLimit)

    // 和PlatON通讯的对象
    var mWeb3j: Web3j = Web3jFactory.build(
        HttpService(
            url, OkHttpClient().newBuilder()
                .connectTimeout(
                    TRANSACTION_SEND_CONNECT_TIMEOUT,
                    TimeUnit.MILLISECONDS
                )
                .readTimeout(
                    TRANSACTION_SEND_READ_TIMEOUT,
                    TimeUnit.MILLISECONDS
                )
                .build(), false
        )
    );

    /**
     * 获取余额
     */
    fun GetBalance(walletAddress: String): PlatonGetBalance {
        return mWeb3j.platonGetBalance(
            walletAddress,
            DefaultBlockParameterName.LATEST
        ).send()
    }

}