package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.play2plat_tpcm.Platforms_List_Fragment
import com.example.play2plat_tpcm.R
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.Game
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutFragment : Fragment(), Games_List_Horizontal_Fragment.OnEmptyListListener, Games_List_Horizontal_Fragment.OnNotEmptyListListener {

    //private lateinit var gameId: TextView
    private lateinit var description: TextView
    private lateinit var genres: TextView
    private lateinit var platforms: TextView
    private var currentUserType: Int = 0
    private lateinit var fragmentSequence: FrameLayout
    private lateinit var fragmentCompany: FrameLayout

    override fun onListEmpty() {
        fragmentCompany.visibility = View.GONE
    }

    override fun onListNotEmpty() {
        fragmentCompany.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        description = view.findViewById(R.id.api_description)
        genres = view.findViewById(R.id.genres)
        platforms = view.findViewById(R.id.platforms)
        fragmentSequence = view.findViewById(R.id.fragment_sequence)
        fragmentCompany = view.findViewById(R.id.fragment_company)

        val gameIdArg = arguments?.getInt("gameId", 0) ?: 0
        val descriptionArg = arguments?.getString("description", "") ?: ""
        val platformsArg = arguments?.getStringArrayList("platforms") ?: ArrayList()
        val platformsList = platformsArg.map { it ?: "" } // Converting Serializable to List<String>
        val genresArg = arguments?.getStringArrayList("genres")?.joinToString(" • ") ?: ""
        val sequenceArg = arguments?.getString("sequence") ?: "No" // Valor padrão para sequence
        val companyArg = arguments?.getString("company", "") ?: ""

        if (sequenceArg.equals("No", ignoreCase = true)) {
            fragmentSequence.visibility = View.GONE
        } else {
            ApiManager.apiService.getGamesSameSequence(gameIdArg).enqueue(object : Callback<List<Collections>> {
                override fun onResponse(
                    call: Call<List<Collections>>,
                    response: Response<List<Collections>>
                ) {
                    if (response.isSuccessful) {
                        val games = response.body()?.map { it.toGame() } ?: emptyList()
                        if (games.isEmpty()) {
                            fragmentSequence.visibility = View.GONE
                        } else {
                            fragmentSequence.visibility = View.VISIBLE
                        }

                    } else {
                        Log.e("Games_List_Grid_Fragment", "Erro na resposta: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
                    Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
                }
            })

        }

        ApiManager.apiService.getGamesSameCompany(gameIdArg).enqueue(object :
            Callback<List<Collections>> {
            override fun onResponse(
                call: Call<List<Collections>>,
                response: Response<List<Collections>>
            ) {
                if (response.isSuccessful) {
                    val games = response.body()?.map { it.toGame() } ?: emptyList()
                    if (games.isEmpty()) {
                        fragmentCompany.visibility = View.GONE
                    } else {
                        fragmentCompany.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("Games_List_Grid_Fragment", "Erro na resposta: ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
                Log.e("Games_List_Grid_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        currentUserType = sharedPreferences.getInt("user_type_id", 0)


        description.text=descriptionArg
        genres.text =genresArg

        val canEditPlatforms = currentUserType == 1
        if(platforms != null){
            val platformsFragment = Platforms_List_Fragment.newInstance(platformsList, canEditPlatforms, false, gameIdArg, false)
            childFragmentManager.beginTransaction().replace(R.id.platforms_fragment, platformsFragment).commit()
        }

        val fragmentSequence = Games_List_Horizontal_Fragment.newInstance("SameSequence", sequenceArg, gameIdArg)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_sequence, fragmentSequence)
            .commit()

        val fragmentCompany = Games_List_Horizontal_Fragment.newInstance("SameCompany", companyArg, gameIdArg)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_company, fragmentCompany)
            .commit()

        return view
    }

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

    companion object {
        @JvmStatic
        fun newInstance(gameId: Int, description: String, genres: List<String>, platforms: List<String>, sequence: String, company: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putInt("gameId", gameId)
                    putString("description", description)
                    putStringArrayList("genres", ArrayList(genres))
                    putStringArrayList("platforms", ArrayList(platforms))
                    putString("sequence", sequence)
                    putString("company", company)
                }
            }
    }
}
