package com.digquant.api


import com.digquant.entity.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.POST


interface IAtonApi {
    /**
     * 获取交易信息
     */
    @POST("/app/v0760/transaction/list")
    suspend fun GetTransactionList(@retrofit2.http.Body param: TransactionListTO): ApiResponse<List<Transaction>>

    /**
     * 获取验证节列表
     */
    @POST("/app/v0760/node/nodelist")
    suspend fun GetVerifyNodeList(): ApiResponse<List<VerifyNode>>


    /**
     * 获取我的委托列表
     */
    @POST("/app/v0760/node/listDelegateGroupByAddr")
    suspend fun GetMyDelegateList(@retrofit2.http.Body param: MyDelegateTO): ApiResponse<List<DelegateInfo>>

    /**
     * 获取委托详情数据
     */
    @POST("/app/v0760/node/delegateDetails")
    suspend fun GetDelegateDetailList(@retrofit2.http.Body param: DetailDelegateTO): ApiResponse<DelegateNodeDetail>
}


object AtonApi {
    private var url: String = "https://aton-dev.platon.network"

    fun <T> Create(tClass: Class<T>): T {
        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(AtonApi.url)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(tClass)
    }

    private val atonApi = Create(IAtonApi::class.java)

    /**
     * 获取交易列表
     */
    suspend fun GetTransactionList(param: TransactionListTO): ApiResponse<List<Transaction>> {
        return atonApi.GetTransactionList(param)
    }

    /**
     * 获取交易列表
     */
    suspend fun GetVerifyNodeList(): ApiResponse<List<VerifyNode>> {
        return atonApi.GetVerifyNodeList()
    }

    /**
     * 获取交易列表
     */
    suspend fun GetMyDelegateList(param: MyDelegateTO): ApiResponse<List<DelegateInfo>> {
        return atonApi.GetMyDelegateList(param)
    }

    /**
     * 获取交易列表
     */
    suspend fun GetDelegateDetailList(param: DetailDelegateTO): ApiResponse<DelegateNodeDetail> {
        return atonApi.GetDelegateDetailList(param)
    }
}