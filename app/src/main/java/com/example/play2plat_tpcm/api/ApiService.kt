package com.example.play2plat_tpcm.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("games")
    fun getAllGames(): Call<List<Game>>
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

    // User Game Comments
    @GET("user-game-comments/{id}")
    fun getCommentByGame(@Path("id") id: Int): Call<List<GameCommentsResponse>>

    @POST("user-game-comment")
    fun addComment(@Body comment: Comment): Call<Comment>

    // User Game Favorite
    @GET("user-game-favorite/user/{userId}")
    fun getUserGameFavorites(@Path("userId") userId: Int): Call<List<UserGameFavorite>>

    @POST("user-game-favorite")
    fun addUserGameFavorite(@Body userGameFavorite: UserGameFavorite): Call<UserGameFavorite>

    @DELETE("user-game-favorite/game/{gameId}/user/{userId}")
    fun deleteUserGameFavorite(@Path("gameId") gameId: Int, @Path("userId") userId: Int): Call<Void>

    // User Game

    @GET("user-game/user/{userId}")
    fun getUserGame(@Path("userId") userId: Int): Call<List<UserGame>>

    @POST("user-game")
    fun addUserGame(@Body userGame: UserGame): Call<UserGame>

    @DELETE("user-game/user/{userId}/game/{gameId}")
    fun deleteUserGame(@Path("userId") userId: Int, @Path("gameId") gameId: Int): Call<Void>

    @PATCH("user-game/user/{userId}/game/{gameId}")
    fun updateUserGame(@Path("userId") userId: Int, @Path("gameId") gameId: Int, @Body userGame: UserGame): Call<UserGame>

    // Avaliation
    @GET("avaliation/user/{userId}")
    fun getAvaliation(@Path("userId") userId: Int): Call<List<Avaliation>>

    @POST("avaliation")
    fun addAvaliation(@Body avaliation: Avaliation): Call<Avaliation>

    @DELETE("avaliation/user/{userId}/game/{gameId}")
    fun deleteAvaliation(@Path("userId") userId: Int, @Path("gameId") gameId: Int): Call<Void>

    @PATCH("avaliation/user/{userId}/game/{gameId}")
    fun updateAvaliation(@Path("userId") userId: Int, @Path("gameId") gameId: Int, @Body avaliation: Avaliation): Call<Avaliation>


    // Collections
    @GET("user-game/user/{userId}/state/{state}")
    fun getStateCollection(@Path("userId") userId: Int, @Path("state") state: String): Call<List<Collections>>


    @GET("user-game-favorite/user/{userId}")
    fun getFavoritesByUserId(@Path("userId") userId: Int): Call<List<ListFavoriteGames>>


}

