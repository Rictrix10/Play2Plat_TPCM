import com.example.play2plat_tpcm.api.GeoNamesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesService {
    @GET("postalCodeSearchJSON")
    fun getLocationInfo(
        @Query("postalcode") postalCode: String,
        @Query("country") country: String,
        @Query("username") username: String,
        @Query("maxRows") maxRows: Int = 10
    ): Call<GeoNamesResponse>
}
