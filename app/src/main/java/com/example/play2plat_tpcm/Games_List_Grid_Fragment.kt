package com.example.play2plat_tpcm

import android.content.res.Configuration
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.adapters.Games_List_Grid_Adapter
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.Filters
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.GameFavorite
import com.example.play2plat_tpcm.api.GameFiltered
import com.example.play2plat_tpcm.api.ListFavoriteGames
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Games_List_Grid_Fragment : Fragment(), Games_List_Grid_Adapter.OnGameClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noGamesImageView: ImageView
    private lateinit var noGamesTextView: TextView
    private lateinit var gameCoverAdapter: Games_List_Grid_Adapter
    private var filterType: String? = null
    private var paramater: String? = null
    private var userId: Int = 0
    private var filters: Filters? = null
    private val navigationViewModel: FragmentNavigationViewModel by viewModels()


    companion object {
        private const val ARG_FILTER_TYPE = "filter_type"
        private const val ARG_PARAMATER = "paramater"
        private const val ARG_FILTERS = "filters"

        fun newInstance(filterType: String, paramater: String, filters: Filters?): Games_List_Grid_Fragment {
            val fragment = Games_List_Grid_Fragment()
            val args = Bundle()
            args.putString(ARG_FILTER_TYPE, filterType)
            args.putString(ARG_PARAMATER, paramater)
            args.putParcelable(ARG_FILTERS, filters)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filterType = it.getString(ARG_FILTER_TYPE)
            paramater= it.getString(ARG_PARAMATER)
            filters = it.getParcelable(ARG_FILTERS)
        }

        // Retrieve userId from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", 0)
    }
    private fun updateGridLayoutManager() {
        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            5
        } else {
            3
        }
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games_list_grid, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_game_covers)
        noGamesImageView = view.findViewById(R.id.no_games_image)
        noGamesTextView = view.findViewById(R.id.no_games_text)
        updateGridLayoutManager()

        gameCoverAdapter = Games_List_Grid_Adapter(emptyList(), this)
        recyclerView.adapter = gameCoverAdapter


        Log.d("Filters", "o seus filtros $filters")

        val validFilterTypes = listOf("Wish List", "Playing", "Paused", "Concluded")
        val collectionsNames = resources.getStringArray(R.array.collections_names)

        if (filterType == "Favorite") {
            noGamesTextView.text = getString(R.string.no_favorite_games);
        } else if (filterType in validFilterTypes) {
            val index = validFilterTypes.indexOf(filterType)
            val collectionName = collectionsNames[index]
            val noCollectionGames = getString(R.string.no_collection_games, collectionName)
            noGamesTextView.text = noCollectionGames
        } else {
            noGamesTextView.text = "No Games found"
        }


        //noGamesTextView.text = "From $paramater"
        /*
        else {
            noGamesTextView.text = "$paramater Games"
        }

         */

        loadGames()

        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateGridLayoutManager()
    }




    private fun loadGames() {
        when (filterType) {
            "Playing", "Wish List", "Paused", "Concluded" -> getStateCollection(filterType!!)
            "Favorite" -> getFavoriteGames()
            "Search"->getSearchedGamesbyName(paramater!!)
            "Genres" -> getGamesByGenre(paramater!!)
            "Sequences"-> getGamesBySequence(paramater!!)
            "Platforms"-> getGamesByPlatform(paramater!!)
            "Companies"-> getGamesByCompany(paramater!!)
            "SameSequence"->getGamesBySequence(paramater!!)
            "SameCompany"-> getGamesByCompany(paramater!!)
            "Recent" -> getRecentGames()
            "Filtered" -> getFilteredGames(filters!!)
            else -> getStateCollection("Playing")
        }
    }

    private fun updateUI(games: List<Game>) {
        if (games.isEmpty()) {
            noGamesImageView.visibility = View.VISIBLE
            noGamesTextView.visibility = View.VISIBLE
        } else {
            noGamesImageView.visibility = View.GONE
            noGamesTextView.visibility = View.GONE
        }
        gameCoverAdapter.updateGames(games)
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
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
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
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
                } else {
                    Log.e("Games_List_Grid_Fragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<ListFavoriteGames>>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }


    private fun getSearchedGamesbyName(name: String) {
        ApiManager.apiService.getSearchedGamesByName(name).enqueue(object : Callback<List<Collections>> {
            override fun onResponse(
                call: Call<List<Collections>>,
                response: Response<List<Collections>>
            ) {
                if (response.isSuccessful) {
                    val games = response.body()?.map { it.toGame() } ?: emptyList()
                    Log.d("Games_List_Grid_Fragment", "Resposta da API: $games")
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
                } else {
                    Log.e("Games_List_Grid_Fragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
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
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
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
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
                } else {
                    Log.e("Games_List_Grid_Fragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun getFilteredGames(filtros: Filters) {
        // Log the filters being passed to the request
        Log.d("Games_List_Grid_Fragment", "Filters passed to request: $filtros")



        ApiManager.apiService.getFilteredGames(filtros).enqueue(object : Callback<List<GameFiltered>> {
            override fun onResponse(call: Call<List<GameFiltered>>, response: Response<List<GameFiltered>>) {
                if (response.isSuccessful) {
                    val games = response.body()?.map { gameFiltered ->
                        Game(
                            id = gameFiltered.id,
                            name = gameFiltered.name,
                            description = gameFiltered.description,
                            isFree = gameFiltered.isFree,
                            releaseDate = gameFiltered.releaseDate,
                            pegiInfo = gameFiltered.pegiInfo,
                            coverImage = gameFiltered.coverImage,
                            sequenceId = gameFiltered?.sequence?.id ?: null,
                            companyId = gameFiltered.company.id,
                        )
                    } ?: emptyList()

                    Log.d("Games_List_Grid_Fragment", "Games response from API: $games")
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
                } else {
                    Log.e("ViewMoreGames_Fragment", "Error in response: ${response.errorBody()}")
                    // Log the raw response body for debugging
                    response.errorBody()?.let { Log.e("Games_List_Grid_Fragment", it.string()) }
                }
            }

            override fun onFailure(call: Call<List<GameFiltered>>, t: Throwable) {
                Log.e("ViewMoreGames_Fragment", "API call failed: ${t.message}")
            }
        })
    }

    private fun reconstructFilters(filtros: Filters): Filters {
        val companies = filtros.companies ?: emptyList()
        val sequences = filtros.sequences ?: emptyList()

        // Reconstruct companies list
        val reconstructedCompanies = if (companies.isEmpty()) {
            null
        } else {
            companies
        }

        // Reconstruct sequences list
        val reconstructedSequences = if (sequences.isEmpty()) {
            null
        } else {
            sequences
        }

        return filtros.copy(companies = reconstructedCompanies, sequences = reconstructedSequences)
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
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
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
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
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
                    //gameCoverAdapter.updateGames(games)
                    updateUI(games)
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

        if (isNetworkAvailable()) {
            val platforms = arrayListOf<String>()
            val viewGameFragment = View_Game_Fragment.newInstance(gameId, platforms)
            navigationViewModel.addToStack(viewGameFragment)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, viewGameFragment)
                .addToBackStack(null)
                .commit()
        }
        else{
            redirectToNoConnectionFragment()
        }

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

    fun GameFiltered.toGame(): Game {
        return Game(
            id = this.id,
            name = this.name,
            description = this.description,
            isFree = this.isFree,
            releaseDate = this.releaseDate,
            pegiInfo = this.pegiInfo,
            coverImage = this.coverImage,
            companyId = this.company.id,
            sequenceId = this.sequence?.id
        )
    }

}
