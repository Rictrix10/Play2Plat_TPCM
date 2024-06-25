package com.example.play2plat_tpcm

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Avaliation
import com.example.play2plat_tpcm.api.AverageStars
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.example.play2plat_tpcm.api.GeoNamesResponse
import com.example.play2plat_tpcm.api.GeoNamesServiceBuilder
import com.example.play2plat_tpcm.api.LocationInfo
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class InteractFragment : Fragment(),
    GamePostsAdapter.OnProfilePictureClickListener,
    GamePostsAdapter.OnReplyClickListener,
    GamePostsAdapter.onMoreOptionsClickListener {

    private lateinit var starViews: List<ImageView>
    private lateinit var postsLayout: ConstraintLayout
    private lateinit var circularProgress: CircularProgressIndicator
    private lateinit var averageRatingText: TextView
    private lateinit var starIcon: ImageView
    private var currentRating = 0
    private var gameId: Int = 0
    private var userId: Int = 0
    private var gameName: String? = null
    private var primaryColor: Int = 0
    private var secondaryColor: Int = 0
    private var averageRating: Float = 0.0f
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var seeMoreText: TextView
    private lateinit var iconDown1: ImageView
    private lateinit var iconDown2: ImageView
    private lateinit var iconAdd: ImageView
    private val navigationViewModel: FragmentNavigationViewModel by viewModels()

    override fun onPause() {
        super.onPause()
        // Qualquer ação que precise ser interrompida quando o fragmento não está visível
    }

    override fun onResume() {
        super.onResume()
        // Retomar operações específicas quando o fragmento se torna visível novamente
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getInt(ARG_GAME_ID)
            gameName = it.getString(ARG_GAME_NAME)
            primaryColor = it.getInt(ARG_PRIMARY_COLOR)
            secondaryColor = it.getInt(ARG_SECONDARY_COLOR)
            averageRating = it.getFloat(ARG_AVERAGE_STARS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_interact, container, false)
        postsLayout = view.findViewById(R.id.posts_box)
        circularProgress = view.findViewById(R.id.circular_progress)
        averageRatingText = view.findViewById(R.id.average_rating_text)
        starIcon = view.findViewById(R.id.star_icon)
        emptyView = view.findViewById(R.id.empty_view)
        seeMoreText = view.findViewById(R.id.see_more_text)
        iconDown1 = view.findViewById(R.id.icon_down_1)
        iconDown2 = view.findViewById(R.id.icon_down_2)
        iconAdd = view.findViewById(R.id.icon_add)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize star views
        starViews = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )

        // Set click listeners for stars
        for ((index, starView) in starViews.withIndex()) {
            starView.setOnClickListener {
                if (isNetworkAvailable()) {
                    handleStarClick(index + 1)
                }
                else{
                    Toast.makeText(
                        requireContext(),
                        "Erro ao avaliar, verifique a sua internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", 0)

        loadUserAvaliation(userId, gameId)

        getPreviewPosts(gameId, userId)

        // Set average rating and circular progress

        circularProgress.setProgressCompat((averageRating * 20).toInt(), true)

        averageRatingText.text = String.format("%.1f", averageRating)

        // Click listener for posts layou
        postsLayout.setOnTouchListener { _, event ->
            // Redirect to game posts when any touch event occurs within the postsBox
            if (event.action == MotionEvent.ACTION_DOWN) {
                redirectToGamePosts(gameId)
                return@setOnTouchListener true
            }
            false
        }


        return view
    }

    override fun onProfilePictureClick(userId: Int) {
        // Implement your code here to handle profile picture click
    }

    override fun onReplyClick(postId: Int, username: String) {
        // Implement your code here to handle reply click
    }

    override fun onOptionsClick(postId: Int) {
        // Implement your code here to handle options click
    }

    private fun handleStarClick(rating: Int) {
        if (rating == currentRating) {
            // Reset stars if the same rating is clicked again
            updateStarViews(0)
            currentRating = 0
            deleteAvaliation(userId, gameId)
            Toast.makeText(
                requireContext(),
                "Avaliação removida com sucesso",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Update stars to the new rating
            updateStarViews(rating)
            currentRating = rating
            ApiManager.apiService.getAvaliation(userId).enqueue(object : Callback<List<Avaliation>> {
                override fun onResponse(call: Call<List<Avaliation>>, response: Response<List<Avaliation>>) {
                    if (response.isSuccessful) {
                        val avaliations = response.body()
                        val userAvaliation = avaliations?.find { it.gameId == gameId }
                        if (userAvaliation == null) {
                            addAvaliation(userId, gameId, rating)
                            Toast.makeText(
                                requireContext(),
                                "Avaliação adicionada com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            updateAvaliation(userId, gameId, rating)
                            Toast.makeText(
                                requireContext(),
                                "Avaliação atualizada com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Avaliation>>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }

    private fun updateStarViews(rating: Int) {
        for ((index, starView) in starViews.withIndex()) {
            if (index < rating) {
                starView.setImageResource(R.drawable.icon_star_full)
            } else {
                starView.setImageResource(R.drawable.icon_star_outline)
            }
        }
    }

    private fun loadUserAvaliation(userId: Int, gameId: Int) {
        ApiManager.apiService.getAvaliation(userId).enqueue(object : Callback<List<Avaliation>> {
            override fun onResponse(call: Call<List<Avaliation>>, response: Response<List<Avaliation>>) {
                if (response.isSuccessful) {
                    val avaliations = response.body()
                    val userAvaliation = avaliations?.find { it.gameId == gameId }
                    if (userAvaliation != null) {
                        updateStarViews(userAvaliation.stars.toInt())
                        currentRating = userAvaliation.stars.toInt()
                    }
                }
            }

            override fun onFailure(call: Call<List<Avaliation>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun addAvaliation(userId: Int, gameId: Int, stars: Int) {
        val avaliation = Avaliation(userId, gameId, stars.toFloat())
        ApiManager.apiService.addAvaliation(avaliation).enqueue(object : Callback<Avaliation> {
            override fun onResponse(call: Call<Avaliation>, response: Response<Avaliation>) {
                if (response.isSuccessful) {
                    Log.d("View_Game_Fragment", "Avaliation added successfully")
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<Avaliation>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun updateAvaliation(userId: Int, gameId: Int, stars: Int) {
        val avaliation = Avaliation(userId, gameId, stars.toFloat())
        ApiManager.apiService.updateAvaliation(userId, gameId, avaliation).enqueue(object : Callback<Avaliation> {
            override fun onResponse(call: Call<Avaliation>, response: Response<Avaliation>) {
                if (response.isSuccessful) {
                    Log.d("View_Game_Fragment", "Avaliation updated successfully")
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<Avaliation>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun deleteAvaliation(userId: Int, gameId: Int) {
        ApiManager.apiService.deleteAvaliation(userId, gameId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("View_Game_Fragment", "Avaliation deleted successfully")
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun redirectToGamePosts(gameId: Int) {
        if (isNetworkAvailable()) {
            val gamePostsFragment =
                GamePostsFragment.newInstance(gameId, gameName!!, primaryColor, secondaryColor)

            navigationViewModel.addToStack(gamePostsFragment)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, gamePostsFragment)
                .addToBackStack(null)
                .commit()
        }
        else{
            redirectToNoConnectionFragment()
        }
    }

    private fun getPreviewPosts(gameId: Int, userId: Int) {
        ApiManager.apiService.getPostsPreview(gameId).enqueue(object : Callback<List<GameCommentsResponse>> {
            override fun onResponse(
                call: Call<List<GameCommentsResponse>>,
                response: Response<List<GameCommentsResponse>>
            ) {
                if (response.isSuccessful) {
                    val gamePosts = response.body()
                    updateEmptyView(gamePosts!!)
                    if (gamePosts != null && gamePosts.isNotEmpty()) {
                        getLocationName(gamePosts[0].latitude, gamePosts[0].longitude) { locationInfo ->
                            recyclerView.adapter = GamePostsAdapter(gamePosts, this@InteractFragment, this@InteractFragment, this@InteractFragment, true)
                        }
                    } else {
                        Log.e("GamePostsFragment", "A lista de posts do jogo está vazia ou nula.")
                    }
                } else {
                    Log.e("GamePostsFragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<GameCommentsResponse>>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun updateEmptyView(posts: List<GameCommentsResponse>) {
        if (posts.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            seeMoreText.visibility = View.GONE
            iconDown1.visibility = View.GONE
            iconDown2.visibility = View.GONE
            iconAdd.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            seeMoreText.visibility = View.VISIBLE
            iconDown1.visibility = View.VISIBLE
            iconDown2.visibility = View.VISIBLE
            iconAdd.visibility = View.GONE
        }
    }

    private fun getLocationName(
        latitude: Double,
        longitude: Double,
        onResult: (LocationInfo) -> Unit
    ) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var locationInfo = LocationInfo(null, null, null, null, null, null)

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val cityName = address.locality
                val countryName = address.countryName
                val countryCode = address.countryCode
                val postalCode = address.postalCode

                locationInfo = LocationInfo(cityName, countryName, countryCode, postalCode, null, null)

                if (postalCode != null && countryCode != null) {
                    getPostalCodeInfo(postalCode, countryCode, "rictrix") { updatedLocationInfo ->
                        val finalLocationInfo = locationInfo.copy(
                            adminName1 = updatedLocationInfo.adminName1,
                            adminName2 = updatedLocationInfo.adminName2
                        )
                        onResult(finalLocationInfo)
                    }
                } else {
                    onResult(locationInfo)
                }
            } else {
                onResult(locationInfo)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            onResult(locationInfo)
        }
    }

    private fun getPostalCodeInfo(
        postalCode: String,
        countryCode: String,
        username: String,
        onResult: (LocationInfo) -> Unit
    ) {
        val call = GeoNamesServiceBuilder.service.getLocationInfo(postalCode, countryCode, username)
        call.enqueue(object : Callback<GeoNamesResponse> {
            override fun onResponse(call: Call<GeoNamesResponse>, response: Response<GeoNamesResponse>) {
                if (response.isSuccessful) {
                    val locationInfoResponse = response.body()
                    if (locationInfoResponse != null && locationInfoResponse.postalCodes.isNotEmpty()) {
                        val firstResult = locationInfoResponse.postalCodes[0]
                        val locationInfo = LocationInfo(
                            null, null, null, postalCode,
                            firstResult.adminName1, firstResult.adminName2
                        )
                        onResult(locationInfo)
                    } else {
                        Log.e("PostalCodeInfo", "No results found for postal code: $postalCode")
                        onResult(LocationInfo(null, null, null, postalCode, null, null))
                    }
                } else {
                    Log.e("PostalCodeInfo", "Error: ${response.message()}")
                    onResult(LocationInfo(null, null, null, postalCode, null, null))
                }
            }

            override fun onFailure(call: Call<GeoNamesResponse>, t: Throwable) {
                Log.e("PostalCodeInfo", "Failure: ${t.message}")
                onResult(LocationInfo(null, null, null, postalCode, null, null))
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun redirectToNoConnectionFragment() {
        val noConnectionFragment= NoConnectionFragment()
        navigationViewModel.addToStack(noConnectionFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, noConnectionFragment)
            .addToBackStack(null)
            .commit()

    }

    companion object {
        private const val ARG_GAME_ID = "gameId"
        private const val ARG_GAME_NAME = "gameName"
        private const val ARG_PRIMARY_COLOR = "primaryColor"
        private const val ARG_SECONDARY_COLOR = "secondaryColor"
        private const val ARG_AVERAGE_STARS = "averageStars"

        @JvmStatic
        fun newInstance(gameId: Int, gameName: String, primaryColor: Int, secondaryColor: Int, averageStars: Float) =
            InteractFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                    putInt(ARG_PRIMARY_COLOR, primaryColor)
                    putInt(ARG_SECONDARY_COLOR, secondaryColor)
                    putFloat(ARG_AVERAGE_STARS, averageStars)
                }
            }
    }
}
