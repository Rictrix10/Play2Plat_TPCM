package com.example.play2plat_tpcm.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
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



    @POST("upload")
    fun uploadImage(@Body imageData: Map<String, String>): Call<ResponseBody>
    */

    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("games/{id}")
    fun getGameById(@Path("id") id: Int): Call<GameInfo>

    @POST("users")
    fun createUser(@Body user: UserRegister): Call<UserRegister>

    @POST("users/login")
    fun loginUser(@Body userLogin: UserLogin): Call<UserLoginResponse>
//    @POST("users")
//    fun createUser(@Body user: UserRegister): Call<UserRegister>
//
//    @POST("users/login")
//    fun loginUser(@Body userLogin: UserLogin): Call<UserLoginResponse>
//
//    @GET("users/{id}")
//    fun getUserById(@Path("id") id: Int): Call<User>

    @GET("genres")
    fun getGenres(): Call<List<Genre>>

    @POST("game-genre")
    fun addGenresToGame(@Body game_genre: GameGenre): Call<GameGenre>

    @POST("upload")
    fun uploadImage(@Body imageName: String)

    // Na sua interface ApiService
    @GET("platforms")
    fun getPlatforms(): Call<List<Platforms>>

    @POST("platform-game")
    fun addPlatformsToGame(@Body game_platform: GamePlatform): Call<GamePlatform>


    @GET("users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

    @PATCH("users/{id}")
    fun updateUser(@Path("id") userId: Int, @Body user: User): Call<User>



    /*
    @GET("companies")
    fun getCompanies(): Call<List<Company>>

    @GET("sequences")
    fun getSequences(): Call<List<Sequence>>

    @GET("genres")
    fun getGenres(): Call<List<Genre>>

    @POST("game-genre")
    fun addGenresToGame(@Body game_genre: GameGenre): Call<GameGenre>

     */

}

