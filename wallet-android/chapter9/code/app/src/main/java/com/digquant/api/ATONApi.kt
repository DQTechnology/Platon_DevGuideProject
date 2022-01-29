package com.digquant.api


import com.digquant.entity.*
import com.digquant.service.NodeManager
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


    fun <T> Create(tClass: Class<T>): T {

        val nodeInfo = NodeManager.GetCurNodeInfo()


        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(nodeInfo.atonUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(tClass)
    }



    /**
     * 获取交易列表
     */
    suspend fun GetTransactionList(param: TransactionListTO): ApiResponse<List<Transaction>> {




        val atonApi = Create(IAtonApi::class.java)


        return atonApi.GetTransactionList(param)
    }

    /**
     * 获取验证节点列表
     */
    suspend fun GetVerifyNodeList(): ApiResponse<List<VerifyNode>> {

        val atonApi = Create(IAtonApi::class.java)
        return atonApi.GetVerifyNodeList()
    }

    /**
     * 获取所有钱包的委托列表
     */
    suspend fun GetMyDelegateList(param: MyDelegateTO): ApiResponse<List<DelegateInfo>> {

        val atonApi = Create(IAtonApi::class.java)
        return atonApi.GetMyDelegateList(param)
    }

    /**
     * 获取指定钱包委托详情列表
     */
    suspend fun GetDelegateDetailList(param: DetailDelegateTO): ApiResponse<DelegateNodeDetail> {

        val atonApi = Create(IAtonApi::class.java)
        return atonApi.GetDelegateDetailList(param)
    }
}