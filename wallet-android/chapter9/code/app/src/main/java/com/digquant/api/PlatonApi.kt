package com.digquant.api

import com.digquant.entity.*
import com.digquant.service.NodeManager
import com.digquant.util.StringUtil
import okhttp3.OkHttpClient
import org.spongycastle.util.encoders.Hex
import org.web3j.crypto.*
import org.web3j.crypto.bech32.AddressBehavior
import org.web3j.crypto.bech32.AddressManager
import org.web3j.crypto.bech32.Bech32
import org.web3j.rlp.RlpEncoder
import org.web3j.rlp.RlpList
import org.web3j.rlp.RlpString
import org.web3j.rlp.RlpType
import org.web3j.utils.Convert
import org.web3j.utils.JSONUtil
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
//    const val url: String = "http://35.247.155.162:6789"

    /**
     * 创建retrofit的httpClient对象
     */
    fun <T> Create(tClass: Class<T>): T {


        val nodeInfo = NodeManager.GetCurNodeInfo()


        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(nodeInfo.nodeUrl)
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

    /**
     * 赎回委托的LAT
     */
    suspend fun UnDelegate(
        privateKey: String,
        stakingBlockNum: BigInteger,
        nodeId: String,
        lat: String
    ): PlatonReceiptInfo? {
        // 赎回委托的函数名为 1005
        // 参数依次为委托时的块高, 委托的节点ID, 赎回委托lat的数量
        val result = ArrayList<RlpType>()
        // 委托的函数名为1005
        with(result) {
            // 构造函数名
            add(RlpString.create(RlpEncoder.encode(RlpString.create(1005))))
            // 委托时的块高
            add(RlpString.create(RlpEncoder.encode(RlpString.create(stakingBlockNum))))
            // 委托的节点id
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
            //赎回委托金额
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

        val nodeInfo = NodeManager.GetCurNodeInfo()

//
        /**
         * 使用秘钥对交易数据进行签名
         */
        val signedMessage =
            TransactionEncoder.signMessage(rawTransaction, nodeInfo.chainId, credentials)

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

    /**
     * 获取指定钱包的委托的节点的信息
     */
    suspend fun GetRelatedListByDelAddr(address: String): List<DelegationIdInfo> {

        // 获取钱包委托列表函数名为 1103
        // 参数为钱包地址
        val result = ArrayList<RlpType>()
        with(result) {
            // 构造函数名
            add(RlpString.create(RlpEncoder.encode(RlpString.create(1103))))
            // 钱包地址
            add(RlpString.create(RlpEncoder.encode(RlpString.create(Bech32.addressDecode(address)))))
        }
        //
        val data = Hex.toHexString(RlpEncoder.encode(RlpList(result)))
        // 添加 0x
        val preHexData = Numeric.prependHexPrefix(data)
        val params = ArrayList<Any>()
        //param的格式为
        // {
        //  from: "" 调用钱包地址
        //  to: ""  委托智能合约地址
        //  data: ""  调用的参数
        // }
        // 这里用一个map做参数传递
        val callData = HashMap<String, String>()
        callData["from"] = address
        // 委托智能合约的地址
        callData["to"] = "lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqzsjx8h7"
        callData["data"] = preHexData
        params.add(callData)

        params.add("latest")
        val platonResult = doCall("platon_call", params)
        // 返回结果是以0x开头的16进制字符串, 需要把0x去掉
        val callResult = Numeric.cleanHexPrefix(platonResult.result)
        // 解析返回结果, 返回的是16进制格式需要, 解码成普通字符串
        val jsonMap = JSONUtil.jsonToMap(String(Hex.decode(callResult)))

        // 如果没有委托信息,则返回空列表
        val ret = jsonMap["Ret"] ?: return ArrayList()

        val jsonStr = JSONUtil.toJSONString(ret)
        //
        return JSONUtil.parseArray(jsonStr, DelegationIdInfo::class.java)
    }


    /**
     * 领取委托奖励
     */
    suspend fun WithdrawDelegateReward(privateKey: String): PlatonReceiptInfo? {

        // 领取委托的奖励函数名为 5000
        // 没有参数,因此只需要序列化调用 函数名即可
        val result = ArrayList<RlpType>()
        //
        result.add(RlpString.create(RlpEncoder.encode(RlpString.create(5000))))

        val data = Hex.toHexString(RlpEncoder.encode(RlpList(result)))

        return sendRawTransaction(
            privateKey,
            "lat1zqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqxlcypcy",
            0,
            38640,
            data
        )
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
