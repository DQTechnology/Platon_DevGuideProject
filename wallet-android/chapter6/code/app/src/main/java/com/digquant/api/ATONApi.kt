package com.digquant.api


import com.digquant.entity.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.POST


interface IAtonApi {

    @POST("/app/v0760/transaction/list")
    suspend fun GetTransactionList(@retrofit2.http.Body param: TransactionListTO): ApiResponse<List<Transaction>>
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
}