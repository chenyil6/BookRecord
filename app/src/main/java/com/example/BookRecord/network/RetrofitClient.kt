package com.example.BookRecord.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.BookRecord.network.BooksApi  // 确保正确导入了接口

object RetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/"

    val booksApi: BooksApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

