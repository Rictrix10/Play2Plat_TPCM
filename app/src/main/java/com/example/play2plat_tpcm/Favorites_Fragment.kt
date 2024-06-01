package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.ListFavoriteGames
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Favorites_Fragment : Fragment(), FavoritesAdapter.OnGamePictureClickListener {

    private lateinit var favoritesAdapter: FavoritesAdapter
    private var favoriteGamesList: MutableList<ListFavoriteGames> = mutableListOf()
    private lateinit var fragmentContainer: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_2, container, false)

        favoritesAdapter = FavoritesAdapter(favoriteGamesList, this)
        fragmentContainer = view.findViewById(R.id.fragment_container)

        //loadFavoriteGames()

        val textViewHeight = 50.dpToPx()

        // Obtém a altura da tela
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        // Calcula a altura disponível para o FrameLayout
        val availableHeight = screenHeight - textViewHeight

        // Define a altura do FrameLayout
        val layoutParams = fragmentContainer.layoutParams
        layoutParams.height = availableHeight
        fragmentContainer.layoutParams = layoutParams

        val fragment = Games_List_Grid_Fragment.newInstance("Favorite")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()


        return view
    }

    private fun redirectToViewGame(gameId: Int) {
        val platforms = arrayListOf<String>()

        val viewGameFragment = View_Game_Fragment.newInstance(gameId, platforms)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onGamePictureClick(gameId: Int) {
        redirectToViewGame(gameId)
    }


    /*
    private fun loadFavoriteGames() {
        val userId = getUserIdFromSession()
        ApiManager.apiService.getFavoritesByUserId(userId).enqueue(object : Callback<List<ListFavoriteGames>> {
            override fun onResponse(
                call: Call<List<ListFavoriteGames>>,
                response: Response<List<ListFavoriteGames>>
            ) {
                if (response.isSuccessful) {
                    val favoriteGames = response.body()
                    Log.d("FavoriteGames", "Resposta da API: $favoriteGames")
                    if (favoriteGames != null) {
                        favoriteGamesList.clear()
                        favoriteGamesList.addAll(favoriteGames)
                        favoritesAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("Favorites_Fragment", "Erro na resposta: ${response.errorBody()}")
                    // Tratar erro
                }
            }

            override fun onFailure(call: Call<List<ListFavoriteGames>>, t: Throwable) {
                Log.e("Favorites_Fragment", "Falha na chamada da API: ${t.message}")
                // Tratar falha
            }
        })
    }

     */

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private fun getUserIdFromSession(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", 0)
    }
}

