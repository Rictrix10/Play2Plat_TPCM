package com.example.play2plat_tpcm.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object ApiManager {
    private val client = OkHttpClient.Builder().build()

    private const val BASE_URL = "https://play2-plat-tpcm.vercel.app/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
