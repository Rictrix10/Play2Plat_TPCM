package com.example.play2plat_tpcm

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.Paramater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Search_Fragment : Fragment(), GamesAdapter.OnGamePictureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var GamesAdapter: GamesAdapter
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var fragmentContainer2: FrameLayout
    //private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private var GamesList: MutableList<Collections> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private var countValue: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_, container, false)

        GamesAdapter = GamesAdapter(GamesList, this)
        fragmentContainer = view.findViewById(R.id.fragment_container)
        fragmentContainer2 = view.findViewById(R.id.fragment_container2)
        //searchView = view.findViewById(R.id.search_view) // Assuming you have a SearchView with this ID
        searchButton = view.findViewById(R.id.search)

        val fragment2 = Games_List_Horizontal_Fragment.newInstance("Recent", "Recent")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment2)
            .commit()

        // Set up SearchView click listener
        searchButton.setOnClickListener {
            redirectToGamesSearched()
        }

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        countValue = sharedPreferences.getInt("countValue", 0)
        Log.d("Search_Fragment", "Valor de countValue no onCreateView: $countValue")
        val genreValue = sharedPreferences.getString("genre", null)
        Log.d("Search_Fragment", "Valor de genre nas SharedPreferences: $genreValue")


        val fragment = if (genreValue != null) {
            Games_List_Horizontal_Fragment.newInstance("Genres", genreValue)
        } else {
            // Se genreValue for nulo, você pode passar uma string vazia ou outro valor padrão
            Games_List_Horizontal_Fragment.newInstance("Genres", "Action")
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container2, fragment)
            .commit()

        return view
    }

    override fun onResume() {
        super.onResume()
        countValue++
        sharedPreferences.edit().putInt("countValue", countValue).apply()

        // Verificar se countValue é um divisor de 10
        if (countValue % 10 == 0) {
            getRandomGenre { genre ->
                sharedPreferences.edit().putString("genre", genre).apply()
                Log.d("Search_Fragment", "Novo valor de genre: $genre")
            }
            getRandomSequence { sequence ->
                sharedPreferences.edit().putString("sequence", sequence).apply()
                Log.d("Search_Fragment", "Novo valor de sequence: $sequence")
            }
        }
    }


    private fun getRandomGenre(onGenreReceived: (String) -> Unit) {
        ApiManager.apiService.getRandomGenre().enqueue(object : Callback<Paramater> {
            override fun onResponse(call: Call<Paramater>, response: Response<Paramater>) {
                if (response.isSuccessful) {
                    val genreResponse = response.body()
                    if (genreResponse != null && genreResponse.name != null) {
                        onGenreReceived(genreResponse.name)
                    } else {
                        Log.e("Games_List_Grid_Fragment", "Genre or genre paramater is null")
                    }
                } else {
                    Log.e("Games_List_Grid_Fragment", "Failed to get genre: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Paramater>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun getRandomSequence(onSequenceReceived: (String) -> Unit) {
        ApiManager.apiService.getRandomSequence().enqueue(object : Callback<Paramater> {
            override fun onResponse(call: Call<Paramater>, response: Response<Paramater>) {
                if (response.isSuccessful) {
                    val sequenceResponse = response.body()
                    if (sequenceResponse != null && sequenceResponse.name != null) {
                        onSequenceReceived(sequenceResponse.name)
                    } else {
                        Log.e("Games_List_Grid_Fragment", "Sequence or sequence paramater is null")
                    }
                } else {
                    Log.e("Games_List_Grid_Fragment", "Failed to get sequence: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Paramater>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun getRandomCompany(onCompanyReceived: (String) -> Unit) {
        ApiManager.apiService.getRandomCompany().enqueue(object : Callback<Paramater> {
            override fun onResponse(call: Call<Paramater>, response: Response<Paramater>) {
                if (response.isSuccessful) {
                    val companyResponse = response.body()
                    if (companyResponse != null && companyResponse.name != null) {
                        onCompanyReceived(companyResponse.name)
                    } else {
                        Log.e("Games_List_Grid_Fragment", "Company or company paramater is null")
                    }
                } else {
                    Log.e("Games_List_Grid_Fragment", "Failed to get company: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Paramater>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun getRandomPlatform(onPlatformReceived: (String) -> Unit) {
        ApiManager.apiService.getRandomPlatform().enqueue(object : Callback<Paramater> {
            override fun onResponse(call: Call<Paramater>, response: Response<Paramater>) {
                if (response.isSuccessful) {
                    val platformResponse = response.body()
                    if (platformResponse != null && platformResponse.name != null) {
                        onPlatformReceived(platformResponse.name)
                    } else {
                        Log.e("Games_List_Grid_Fragment", "Platform or platform paramater is null")
                    }
                } else {
                    Log.e("Games_List_Grid_Fragment", "Failed to get platform: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Paramater>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun redirectToViewGame(gameId: Int) {
        val platforms = arrayListOf<String>()

        val viewGameFragment = View_Game_Fragment.newInstance(gameId, platforms)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    override fun onGamePictureClick(gameId: Int) {
        redirectToViewGame(gameId)
    }

    private fun redirectToGamesSearched() {
        val gamesSearchedFragment = GamesSearched_Fragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, gamesSearchedFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}


