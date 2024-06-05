package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.adapters.Games_List_Grid_Adapter
import com.example.play2plat_tpcm.adapters.Games_List_Horizontal_Adapter
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.GameFavorite
import com.example.play2plat_tpcm.api.ListFavoriteGames
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Games_List_Horizontal_Fragment : Fragment(), Games_List_Horizontal_Adapter.OnGameClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameCoverAdapter: Games_List_Horizontal_Adapter
    private lateinit var titleText: TextView
    private lateinit var arrowRight: ImageView
    private var filterType: String? = null
    private var paramater: String? = null
    private var paramaterInt: Int? = 0
    private var userId: Int = 0

    companion object {
        private const val ARG_FILTER_TYPE = "filter_type"
        private const val ARG_PARAMATER = "paramater"
        private const val ARG_PARAMATER_INT = "paramaterInt"

        fun newInstance(filterType: String, paramater: String, paramaterInt: Int): Games_List_Horizontal_Fragment {
            val fragment = Games_List_Horizontal_Fragment()
            val args = Bundle()
            args.putString(ARG_FILTER_TYPE, filterType)
            args.putString(ARG_PARAMATER, paramater)
            args.putInt(ARG_PARAMATER_INT, paramaterInt)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filterType = it.getString(ARG_FILTER_TYPE)
            paramater= it.getString(ARG_PARAMATER)
            paramaterInt = it.getInt(ARG_PARAMATER_INT)
        }

        // Retrieve userId from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games_list_horizontal, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_game_covers)
        arrowRight = view.findViewById(R.id.iconArrowRight)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        gameCoverAdapter = Games_List_Horizontal_Adapter(emptyList(), this)
        recyclerView.adapter = gameCoverAdapter
        titleText = view.findViewById(R.id.text_view)

        if (filterType == "Companies") {
            titleText.text = "From $paramater"
        } else {
            titleText.text = "$paramater Games"
        }

        loadGames()


        arrowRight.setOnClickListener {
            redirectToViewMoreGames_Fragment(filterType, paramater)

        }

        return view
    }

    private fun redirectToViewMoreGames_Fragment(filterType: String?, paramater: String?) {
        val viewMoreGamesFragment = ViewMoreGames_Fragment.newInstance(filterType ?: "", paramater ?: "")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewMoreGamesFragment)
            .addToBackStack(null)
            .commit()
    }


    private fun loadGames() {
        when (filterType) {
            "Playing", "Wish List", "Paused", "Concluded" -> getStateCollection(filterType!!)
            "Favorite" -> getFavoriteGames()
            "Genres" -> getGamesByGenre(paramater!!)
            "Sequences"-> getGamesBySequence(paramater!!)
            "Platforms"-> getGamesByPlatform(paramater!!)
            "Companies"-> getGamesByCompany(paramater!!)
            "Recent" -> getRecentGames()
            "SameSequence" -> getGamesSameSequence(paramaterInt!!)
            "SameCompany" -> getGamesSameCompany(paramaterInt!!)
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

    private fun getGamesByCompany(companyName: String) {
        ApiManager.apiService.getGamesByCompany(companyName).enqueue(object : Callback<List<Collections>> {
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

    private fun getGamesByPlatform(platformName: String) {
        ApiManager.apiService.getGamesByPlatform(platformName).enqueue(object : Callback<List<Collections>> {
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

    private fun getGamesBySequence(sequenceName: String) {
        ApiManager.apiService.getGamesBySequence(sequenceName).enqueue(object : Callback<List<Collections>> {
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

    private fun getGamesByGenre(genreName: String) {
        ApiManager.apiService.getGamesByGenre(genreName).enqueue(object : Callback<List<Collections>> {
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

    private fun getRecentGames() {
        ApiManager.apiService.getRecentGames().enqueue(object : Callback<List<Collections>> {
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

    private fun getGamesSameSequence(gameId: Int) {
        ApiManager.apiService.getGamesSameSequence(gameId).enqueue(object : Callback<List<Collections>> {
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

    private fun getGamesSameCompany(gameId: Int) {
        ApiManager.apiService.getGamesSameCompany(gameId).enqueue(object : Callback<List<Collections>> {
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



    // Handle game click event
    override fun onGameClick(gameId: Int) {
        val platforms = arrayListOf<String>()
        val viewGameFragment = View_Game_Fragment.newInstance(gameId, platforms)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
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

    private fun redirectToViewMoreGames() {
        val viewMoreGamesFragment= ViewMoreGames_Fragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewMoreGamesFragment)
            .addToBackStack(null)
            .commit()
    }
}
