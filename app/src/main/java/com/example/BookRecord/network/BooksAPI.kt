package com.example.BookRecord.network

//定义一个 API 接口，这个接口使用 Retrofit 库来描述如何访问这些网络服务。使用 Retrofit 的注解来标明网络请求的类型、URL、参数等


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApi {
    @GET("books/v1/volumes")
    suspend fun searchBooks(@Query("q") query: String): Response<NetworkBookResponse>
}
