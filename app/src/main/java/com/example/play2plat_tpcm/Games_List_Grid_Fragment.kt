package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.adapters.Games_List_Grid_Adapter
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.GameFavorite
import com.example.play2plat_tpcm.api.ListFavoriteGames
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Games_List_Grid_Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameCoverAdapter: Games_List_Grid_Adapter
    private var filterType: String? = null
    private var userId: Int = 0

    companion object {
        private const val ARG_FILTER_TYPE = "filter_type"

        fun newInstance(filterType: String): Games_List_Grid_Fragment {
            val fragment = Games_List_Grid_Fragment()
            val args = Bundle()
            args.putString(ARG_FILTER_TYPE, filterType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filterType = it.getString(ARG_FILTER_TYPE)
        }

        // Retrieve userId from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games_list_grid, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_game_covers)

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        gameCoverAdapter = Games_List_Grid_Adapter(emptyList())
        recyclerView.adapter = gameCoverAdapter

        loadGames()

        return view
    }

    private fun loadGames() {
        when (filterType) {
            "Playing", "Wish List", "Paused", "Concluded" -> getStateCollection(filterType!!)
            "Favorite" -> getFavoriteGames()
            else -> getStateCollection("Playing")
        }
    }

    private fun getStateCollection(state: String) {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)
        ApiManager.apiService.getStateCollection(userId, state).enqueue(object : Callback<List<Collections>> {
            override fun onResponse(
                call: Call<List<Collections>>,
                response: Response<List<Collections>>
            ) {
                if (response.isSuccessful) {
                    val games = response.body()?.map { it.toGame() } ?: emptyList()
                    Log.d("Games_List_Grid_Fragment", "Resposta da API: $games")
                    gameCoverAdapter.updateGames(games)
                } else {
                    Log.e("Games_List_Grid_Fragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun getFavoriteGames() {
        ApiManager.apiService.getFavoritesByUserId(userId).enqueue(object : Callback<List<ListFavoriteGames>> {
            override fun onResponse(
                call: Call<List<ListFavoriteGames>>,
                response: Response<List<ListFavoriteGames>>
            ) {
                if (response.isSuccessful) {
                    val games = response.body()?.map { it.game.toGame() } ?: emptyList()
                    Log.d("Games_List_Grid_Fragment", "Resposta da API: $games")
                    gameCoverAdapter.updateGames(games)
                } else {
                    Log.e("Games_List_Grid_Fragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<ListFavoriteGames>>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    // Inner class for the fragment
    private fun Collections.toGame(): Game {
        return Game(
            id = this.id,
            name = this.name,
            description = "", // Se não tiver essa informação, pode deixar em branco ou ajustar conforme necessário
            isFree = this.isFree,
            releaseDate = "", // Se não tiver essa informação, pode deixar em branco ou ajustar conforme necessário
            pegiInfo = 0, // Se não tiver essa informação, pode deixar 0 ou ajustar conforme necessário
            coverImage = this.coverImage,
            sequenceId = 0, // Se não tiver essa informação, pode deixar 0 ou ajustar conforme necessário
            companyId = 0 // Se não tiver essa informação, pode deixar 0 ou ajustar conforme necessário
        )
    }

    private fun GameFavorite.toGame(): Game {
        return Game(
            id = this.id,
            name = this.name,
            description = "", // Se não tiver essa informação, pode deixar em branco ou ajustar conforme necessário
            isFree = false,
            releaseDate = "", // Se não tiver essa informação, pode deixar em branco ou ajustar conforme necessário
            pegiInfo = 0, // Se não tiver essa informação, pode deixar 0 ou ajustar conforme necessário
            coverImage = this.coverImage,
            sequenceId = 0, // Se não tiver essa informação, pode deixar 0 ou ajustar conforme necessário
            companyId = 0
        )
    }


}
