package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.ListFavoriteGames
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Favorites_Fragment : Fragment(), FavoritesAdapter.OnGamePictureClickListener {

    private lateinit var recyclerViewFavorites: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private var favoriteGamesList: MutableList<ListFavoriteGames> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerViewFavorites = view.findViewById(R.id.recycler_view_favorites)
        recyclerViewFavorites.layoutManager = LinearLayoutManager(context)
       // favoritesAdapter = FavoritesAdapter(favoriteGamesList)
       // recyclerViewFavorites.adapter = favoritesAdapter

        loadFavoriteGames()

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
                        recyclerViewFavorites.adapter = FavoritesAdapter(favoriteGames, this@Favorites_Fragment)
                    }
                } else {
                    Log.e("GamePostsFragment", "Erro na resposta: ${response.errorBody()}")
                    // Tratar erro
                }
            }

            override fun onFailure(call: Call<List<ListFavoriteGames>>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na chamada da API: ${t.message}")
                // Tratar falha
            }
        })
    }

    private fun getUserIdFromSession(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", 0)
    }
}





