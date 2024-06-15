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
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.Paramater
import com.example.play2plat_tpcm.api.RandomGenresResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Search_Fragment : Fragment(), GamesAdapter.OnGamePictureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var GamesAdapter: GamesAdapter
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var fragmentContainer2: FrameLayout
    private lateinit var fragmentContainer3: FrameLayout
    private lateinit var fragmentContainer4: FrameLayout
    private lateinit var fragmentContainer5: FrameLayout
    private lateinit var fragmentContainer6: FrameLayout
    private lateinit var fragmentContainer7: FrameLayout
    //private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private lateinit var filtersButton: ImageView
    private var GamesList: MutableList<Collections> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private var countValue: Int = 0
    private val uniqueGenres: Set<String> = HashSet()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_, container, false)

        GamesAdapter = GamesAdapter(GamesList, this)
        fragmentContainer = view.findViewById(R.id.fragment_container)
        fragmentContainer2 = view.findViewById(R.id.fragment_container2)
        fragmentContainer3 = view.findViewById(R.id.fragment_container3)
        fragmentContainer4 = view.findViewById(R.id.fragment_container4)
        fragmentContainer5 = view.findViewById(R.id.fragment_container5)
        fragmentContainer6 = view.findViewById(R.id.fragment_container6)
        fragmentContainer7 = view.findViewById(R.id.fragment_container7)
        searchButton = view.findViewById(R.id.search)
        filtersButton = view.findViewById(R.id.black_square)

        if (!parentFragmentManager.isStateSaved()) {
            val fragment = Games_List_Horizontal_Fragment.newInstance("Recent", "Recent", 0)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        } else {
            Log.d("Search_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        // Set up SearchView click listener
        searchButton.setOnClickListener {
            redirectToGamesSearched()
        }

        filtersButton.setOnClickListener {
            redirectToFilters()
        }

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        countValue = sharedPreferences.getInt("countValue", 0)
        Log.d("Search_Fragment", "Valor de countValue no onCreateView: $countValue")
        val genreValue = sharedPreferences.getString("genre", null)
        val companyValue = sharedPreferences.getString("company", null)
        val genreValue2 = sharedPreferences.getString("genre2", null)
        val platformValue = sharedPreferences.getString("platform", null)
        val genreValue3 = sharedPreferences.getString("genre3", null)
        val sequenceValue = sharedPreferences.getString("sequence", null)

        if (!parentFragmentManager.isStateSaved()) {
            val fragment2 = if (genreValue != null) {
                Games_List_Horizontal_Fragment.newInstance("Genres", genreValue, 0)
            } else {
                Games_List_Horizontal_Fragment.newInstance("Genres", "Action", 0)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container2, fragment2)
                .commit()
        } else {
            Log.d("Search_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        if (!parentFragmentManager.isStateSaved()) {
            val fragment3 = if (companyValue != null) {
                Games_List_Horizontal_Fragment.newInstance("Companies", companyValue, 0)
            } else {
                Games_List_Horizontal_Fragment.newInstance("Companies", "Sony", 0)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container3, fragment3)
                .commit()
        } else {
            Log.d("Search_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        if (!parentFragmentManager.isStateSaved()) {
            val fragment4 = if (genreValue2 != null) {
                Games_List_Horizontal_Fragment.newInstance("Genres", genreValue2, 0)
            } else {
                Games_List_Horizontal_Fragment.newInstance("Genres", "Online Multiplayer", 0)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container4, fragment4)
                .commit()
        } else {
            Log.d("Search_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        if (!parentFragmentManager.isStateSaved()) {
            val fragment5 = if (platformValue != null) {
                Games_List_Horizontal_Fragment.newInstance("Platforms", platformValue, 0)
            } else {
                Games_List_Horizontal_Fragment.newInstance("Platforms", "PC", 0)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container5, fragment5)
                .commit()
        } else {
            Log.d("Search_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        if (!parentFragmentManager.isStateSaved()) {
            val fragment6 = if (genreValue3 != null) {
                Games_List_Horizontal_Fragment.newInstance("Genres", genreValue3, 0)
            } else {
                Games_List_Horizontal_Fragment.newInstance("Genres", "Survival", 0)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container6, fragment6)
                .commit()
        } else {
            Log.d("Search_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        if (!parentFragmentManager.isStateSaved()) {
            val fragment7 = if (sequenceValue != null) {
                Games_List_Horizontal_Fragment.newInstance("Sequences", sequenceValue, 0)
            } else {
                Games_List_Horizontal_Fragment.newInstance("Sequences", "Super Mario", 0)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container7, fragment7)
                .commit()
        } else {
            Log.d("Search_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        countValue++
        sharedPreferences.edit().putInt("countValue", countValue).apply()

        // Verificar se countValue é um divisor de 10
        if (countValue % 10 == 0) {

            getRandomNames { genres ->
                genres.take(3).forEachIndexed { index, genre ->
                    when (index) {
                        0 -> sharedPreferences.edit().putString("genre", genre).apply()
                        1 -> sharedPreferences.edit().putString("genre2", genre).apply()
                        2 -> sharedPreferences.edit().putString("genre3", genre).apply()
                    }
                    Log.d("Search_Fragment", "Novo valor de genre${index + 1}: $genre")
                }
            }

            getRandomCompany { company ->
                sharedPreferences.edit().putString("company", company).apply()
                Log.d("Search_Fragment", "Novo valor de company: $company")
            }

            getRandomPlatform { platform ->
                sharedPreferences.edit().putString("platform", platform).apply()
                Log.d("Search_Fragment", "Novo valor de platform: $platform")
            }

            getRandomSequence { sequence ->
                sharedPreferences.edit().putString("sequence", sequence).apply()
                Log.d("Search_Fragment", "Novo valor de sequence: $sequence")
            }
        }
    }

    private fun getRandomNames(onGenresReceived: (List<String>) -> Unit) {
        ApiManager.apiService.getRandomNames().enqueue(object : Callback<RandomGenresResponse> {
            override fun onResponse(call: Call<RandomGenresResponse>, response: Response<RandomGenresResponse>) {
                if (response.isSuccessful) {
                    val genreResponse = response.body()
                    if (genreResponse != null) {
                        onGenresReceived(genreResponse.names)
                    } else {
                        Log.e("Search_Fragment", "Resposta de gêneros ou nomes de gêneros é nula")
                    }
                } else {
                    Log.e("Search_Fragment", "Falha ao obter nomes de gêneros aleatórios: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<RandomGenresResponse>, t: Throwable) {
                Log.e("Search_Fragment", "Falha na chamada da API para obter nomes de gêneros aleatórios: ${t.message}")
            }
        })
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

    private fun redirectToFilters() {
        val filtersFragment = Filters_Fragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, filtersFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}


