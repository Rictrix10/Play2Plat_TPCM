package com.example.play2plat_tpcm.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @GET("/games")
    fun getGames(): Call<List<Game>>

    @POST("games")
    fun createGame(@Body game: Game): Call<Game>
}
