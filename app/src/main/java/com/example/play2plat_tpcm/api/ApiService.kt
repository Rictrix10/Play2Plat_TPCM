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

    @GET("users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

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



}
