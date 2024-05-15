package com.example.play2plat_tpcm.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("companies")
    fun getCompanies(): Call<List<Company>>

    @GET("sequences")
    fun getSequences(): Call<List<Sequence>>

    @POST("games")
    fun createGame(@Body game: Game): Call<Game>

    /*
    @POST("upload")
    fun uploadImage(@Body imageName: String)



    @POST("upload")
    fun uploadImage(@Body imageName: String): Call<ResponseBody>
    */


    @POST("upload")
    fun uploadImage(@Body imageData: Map<String, String>): Call<ResponseBody>

    @GET("games/{id}")
    fun getGameById(@Path("id") id: Int): Call<GameInfo>



}

