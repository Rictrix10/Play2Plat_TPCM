package com.example.play2plat_tpcm.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object ApiManager {
    private val client = OkHttpClient.Builder().build()

    private const val BASE_URL = "https://play2-plat-tpcm.vercel.app/api/"
    //private const val BASE_URL = "http://10.0.2.2:3001/api/"

    private val gson = GsonBuilder()
        .serializeNulls() // Configura Gson para serializar campos nulos
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson)) // Passa o Gson configurado para o Retrofit
        .client(client)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

