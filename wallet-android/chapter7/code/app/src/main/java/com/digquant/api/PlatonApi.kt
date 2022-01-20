package com.digquant.api

import com.digquant.entity.*
import okhttp3.OkHttpClient
import org.spongycastle.util.encoders.Hex
import org.web3j.crypto.*
import org.web3j.crypto.bech32.AddressBehavior
import org.web3j.crypto.bech32.AddressManager
import org.web3j.rlp.RlpEncoder
import org.web3j.rlp.RlpList
import org.web3j.rlp.RlpString
import org.web3j.rlp.RlpType
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.POST
import java.math.BigDecimal
import java.math.BigInteger


interface IPlatonApi {

    @POST("/")
    suspend fun Call(@retrofit2.http.Body params: PlatonParams): PlatonResult

    @POST("/")
    suspend fun Call2(@retrofit2.http.Body params: PlatonParams): PlatonBlockResult

    @POST("/")
    suspend fun Call3(@retrofit2.http.Body params: PlatonParams): PlatonReceiptResult
}


object PlatonApi {

    /**
     * 测试网地址
     */
    const val url: String = "http://35.247.155.162:6789"

    /**
     * 创建retrofit的httpClient对象
     */
    fun <T> Create(tClass: Class<T>): T {

        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(tClass)
    }

    init {
        /**
         *  初始化钱包应用
         */
        WalletApplication.init(
            WalletApplication.MAINNET,
            AddressManager.ADDRESS_TYPE_BECH32,
            AddressBehavior.CHANNLE_PLATON
        )
    }

    /**
     * 创建httpClient实例
     */
    private fun getHttpApi(): IPlatonApi {
        return Create(IPlatonApi::class.java)
    }

    /**
     * 获取钱包余额
     */
    suspend fun GetBalance(walletAddress: String): Double {
        val params = ArrayList<Any>()
        with(params) {
            add(walletAddress)
            add("latest")
        }
        val platonResult = doCall("platon_getBalance", params)

        val lat = Numeric.decodeQuantity(platonResult.result)

        return Convert.fromVon(BigDecimal(lat), Convert.Unit.LAT).toDouble()
    }

    /**
     * 发送LAT到制定者账号, 返回交易哈希
     */
    suspend fun SendLATTO(
        privateKey: String,
        toAddress: String,
        lat: Long
    ): PlatonReceiptInfo? {

        return sendRawTransaction(privateKey, toAddress, lat, 21000, "")
    }

    /**
     * 委托lat
     */
    suspend fun Delegate(privateKey: String, nodeId: String, lat: String): PlatonReceiptInfo? {
        // solidity的序列化方式为RlpType, 从官网得知
        // 委托的函数名为 1004
        // 参数依次为委托类型, 委托节点Id, 委托lat的数量, 因此我们需要一次序列化参数
        val result = ArrayList<RlpType>()
        // 委托的函数名为1004
        with(result) {
            // 构造函数名
            add(RlpString.create(RlpEncoder.encode(RlpString.create(1004))))
            // 0: 自由金额； 1: 锁仓  默认自由金额即可
            add(RlpString.create(RlpEncoder.encode(RlpString.create(0L))))
            // 设置nodeId
            add(
                RlpString.create(
                    RlpEncoder.encode(
                        RlpString.create(
                            Numeric.hexStringToByteArray(
                                nodeId
                            )
                        )
                    )
                )
            )
            // 委托金额
            val von = Convert.toVon(lat, Convert.Unit.LAT).toBigInteger()
            add(RlpString.create(RlpEncoder.encode(RlpString.create(von))))
        }

        val data = Hex.toHexString(RlpEncoder.encode(RlpList(result)))
        // 委托的智能合约地址为: lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqzsjx8h7
        return sendRawTransaction(
            privateKey, "lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqzsjx8h7", 0,
            49920, data
        )
    }


    private suspend fun sendRawTransaction(
        privateKey: String,
        toAddress: String,
        lat: Long,
        gas: Long,
        data: String
    ): PlatonReceiptInfo? {
        /**
         * 转换秘钥
         */
        val iPrivateKey =
            Numeric.toBigInt(privateKey)

        val keyPair = ECKeyPair.create(iPrivateKey)

        /**
         * 将秘钥转换成sdk使用的凭证类
         */
        val credentials: Credentials = Credentials.create(keyPair)

        /**
         * 设置gaslimit
         */
        val gasLimit = BigInteger.valueOf(gas)

        /**
         * 获取当前的gasprice
         */
        val gasPrice = GetGasPrice()

        /**
         * 获取交易数量用作nonce字段
         */
        val nonce: BigInteger = GetTransactionCount(credentials.address)

        /**
         * 将lat转换为von
         */
        val value = Convert.toVon(BigDecimal(lat), Convert.Unit.LAT).toBigInteger();
        /**
         * 构建交易类
         */
        val rawTransaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            toAddress,
            value,
            data
        )

        /**
         * 使用秘钥对交易数据进行签名
         */
        val signedMessage =
            TransactionEncoder.signMessage(rawTransaction, 210309, credentials)

        /**
         * 将交易数据转换为16进制
         */
        val hexValue = Numeric.toHexString(signedMessage)

        val params = ArrayList<Any>()

        params.add(hexValue)
        /**
         * 发送给节点,返回为交易哈希
         */
        val platonResult = doCall("platon_sendRawTransaction", params)

        /**
         * 根据交易哈希获取交易收据
         */
        return GetTransactionReceipt(platonResult.result)
    }

    /**
     * 获取交易收据数据
     */
    suspend fun GetTransactionReceipt(txHash: String): PlatonReceiptInfo? {
        val params = ArrayList<Any>()

        with(params) {
            add(txHash)
        }

        return doCall3("platon_getTransactionReceipt", params).result
    }


    /**
     * 获取交易数量
     */
    suspend fun GetTransactionCount(walletAddress: String): BigInteger {
        val params = ArrayList<Any>()

        with(params) {
            add(walletAddress)
            add("latest")
        }
        val platonResult = doCall("platon_getTransactionCount", params)

        return Numeric.decodeQuantity(platonResult.result)
    }

    /**
     * 通过块的编号获取块的交易数量
     */
    suspend fun GetBlockTransactionCountByNumber(blockNumber: Long): Long {
        val params = ArrayList<Any>()
        val blockNumberBN = BigInteger(blockNumber.toString())
        with(params) {
            add(Numeric.encodeQuantity(blockNumberBN))
        }
        val platonResult = doCall("platon_getBlockTransactionCountByNumber", params)
        return Numeric.decodeQuantity(platonResult.result).toLong()
    }

    /**
     * 通过块的哈希值获取块的交易数量
     */
    suspend fun GetBlockTransactionCountByHash(blockHash: String): Long {
        val params = ArrayList<Any>()
        params.add(blockHash)
        val platonResult = doCall("platon_getBlockTransactionCountByHash", params)
        return Numeric.decodeQuantity(platonResult.result).toLong()
    }

    /**
     * 获取指定块的信息
     */
    suspend fun GetBlockByNumber(blockNumber: Long): PlatonBlockInfo {
        val params = ArrayList<Any>()
        val blockNumberBN = BigInteger(blockNumber.toString())
        with(params) {
            add(Numeric.encodeQuantity(blockNumberBN))
            add(false)
        }
        return doCall2("platon_getBlockByNumber", params).result
    }


    suspend fun GetBlockNumber(): Long {

        val params = ArrayList<Any>()
        val platonResult = doCall("platon_blockNumber", params)
        return Numeric.decodeQuantity(platonResult.result).toLong()
    }

    /**
     * 获取当前gas价格
     */
    suspend fun GetGasPrice(): BigInteger {
        val params = ArrayList<Any>()
        val platonResult = doCall("platon_gasPrice", params)
        return Numeric.decodeQuantity(platonResult.result)
    }


    /**
     * 获取HRP
     */
    suspend fun GetAddressHrp(): String? {
        val params = ArrayList<Any>()
        val platonResult = doCall("platon_getAddressHrp", params)
        return platonResult.result
    }


    suspend fun doCall(method: String, params: ArrayList<Any>): PlatonResult {

        val platonParams = PlatonParams(
            method = method,
            params = params,
            id = 1
        )
        val httpApi = getHttpApi()
        return httpApi.Call(platonParams)

    }

    suspend fun doCall2(method: String, params: ArrayList<Any>): PlatonBlockResult {

        val platonParams = PlatonParams(
            method = method,
            params = params,
            id = 1
        )
        val httpApi = getHttpApi()
        return httpApi.Call2(platonParams)

    }

    suspend fun doCall3(method: String, params: ArrayList<Any>): PlatonReceiptResult {

        val platonParams = PlatonParams(
            method = method,
            params = params,
            id = 1
        )
        val httpApi = getHttpApi()
        return httpApi.Call3(platonParams)
    }
}
