package com.example.play2plat_tpcm.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("companies")
    fun getCompanies(): Call<List<Company>>

    @GET("sequences")
    fun getSequences(): Call<List<Sequence>>

    @POST("games")
    fun createGame(@Body game: Game): Call<Game>

    @POST("upload")
    fun uploadImage(@Body imageName: String)
}
