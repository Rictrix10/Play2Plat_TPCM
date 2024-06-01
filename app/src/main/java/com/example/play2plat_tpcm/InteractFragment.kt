package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Avaliation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InteractFragment : Fragment() {

    private lateinit var starViews: List<ImageView>
    private var currentRating = 0
    private var gameId: Int = 0
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getInt(ARG_GAME_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_interact, container, false)

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
            starView.setOnClickListener { handleStarClick(index + 1) }
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", 0)

        loadUserAvaliation(userId, gameId)

        return view
    }

    private fun handleStarClick(rating: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        if (rating == currentRating) {
            // Reset stars if the same rating is clicked again
            updateStarViews(0)
            currentRating = 0
            deleteAvaliation(userId, gameId)
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
                        } else {
                            updateAvaliation(userId, gameId, rating)
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

    companion object {
        private const val ARG_GAME_ID = "gameId"

        @JvmStatic
        fun newInstance(gameId: Int) =
            InteractFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GAME_ID, gameId)
                }
            }
    }
}
