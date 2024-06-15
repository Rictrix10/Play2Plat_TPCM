package com.example.play2plat_tpcm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.adapters.CollectionsAdapter
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Collections
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.example.play2plat_tpcm.api.UserGame
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Games_Fragment : Fragment(), GamesAdapter.OnGamePictureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var collectionAccordion: LinearLayout
    private lateinit var collectionTitle: TextView
    private lateinit var collectionList: ListView
    private lateinit var collectionInfoValues: Array<String>
    private lateinit var collectionAdapter: CollectionsAdapter

    private var selectedOption: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_games_, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        collectionAccordion = view.findViewById(R.id.collection_accordion)
        collectionTitle = view.findViewById(R.id.collection_title)
        collectionList = view.findViewById(R.id.collection_list)

        val initialState = collectionTitle.text.toString()
        getStateCollection(initialState)

        loadCollections(view.context)

        collectionAccordion.setOnClickListener {
            toggleListVisibility(collectionList, collectionTitle)
            handleAccordionSelection()
        }

        collectionList.setOnItemClickListener { parent, view, position, id ->
            val selectedOption = if (position >= 0 && position < collectionInfoValues.size) collectionInfoValues[position] else null
            //updateUserGameStateWithSelectedOption(selectedOption)
            // Atualize o estado da coleção com a opção selecionada
            if (selectedOption != null) {
                getStateCollection(selectedOption)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleAccordionSelection()
    }

    private fun loadCollections(context: Context) {
        collectionInfoValues = context.resources.getStringArray(R.array.collections_names)
        collectionAdapter = CollectionsAdapter(context, collectionInfoValues, collectionTitle, 1, 1)
        collectionList.adapter = collectionAdapter
    }

    private fun toggleListVisibility(listView: ListView, titleView: TextView) {
        // Obter a altura em pixels correspondente a 40dp
        val heightInPx = 40.dpToPx()

        if (listView.visibility == View.VISIBLE) {
            // Se a lista está visível, oculta e ajusta a altura e as margens
            listView.visibility = View.GONE
            collectionAccordion.layoutParams.height = heightInPx
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_spinner_down, 0)
        } else {
            // Se a lista está oculta, exibe e ajusta a altura e as margens
            listView.visibility = View.VISIBLE
            setListViewHeightBasedOnItems(listView)
            collectionAccordion.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_spinner_up, 0)
        }
        // Solicite uma nova medida do layout após alterar os parâmetros de layout
        collectionAccordion.requestLayout()

        // Verifique se a lista está visível para evitar chamadas desnecessárias
        if (listView.visibility == View.VISIBLE) {
            handleAccordionSelection()
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
                    val games = response.body()
                    Log.d("Games_Fragment", "Resposta da API: $games")
                    if (games != null) {
                        recyclerView.adapter = GamesAdapter(games, this@Games_Fragment)
                    }
                } else {
                    Log.e("Games_Fragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
                Log.e("Games_Fragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private var userGameState: String? = null
    private fun handleAccordionSelection() {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)
        val state = collectionTitle.text.toString() // Use o título atual da coleção

        Log.d("View_Game_Fragment", "Fetching user games for userId: $userId with state: $state")

        ApiManager.apiService.getStateCollection(userId, state).enqueue(object : Callback<List<Collections>> {
            override fun onResponse(call: Call<List<Collections>>, response: Response<List<Collections>>) {
                if (response.isSuccessful) {
                    val games = response.body()
                    if (games != null) {
                        recyclerView.adapter = GamesAdapter(games, this@Games_Fragment)
                    }
                }
            }

            override fun onFailure(call: Call<List<Collections>>, t: Throwable) {
                Log.e("View_Game_Fragment", "Failed to fetch user games", t)
            }
        })
    }

    private fun selectOptionInAccordion(state: String) {
        collectionTitle.text = state // Selecionar a opção no accordion
        Log.d("View_Game_Fragment", "Selected state in accordion: $state")
        Log.d("View_Game_Fragment", "OPCAO ATUAL: $selectedOption")
        // Verificar se o estado do jogo do usuário foi alterado
        if (state != selectedOption) {
            // Atualizar o estado do jogo do usuário localmente
            selectedOption = state
            // Atualizar o estado do jogo do usuário no servidor
            val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", 0)

            //val newstate = "Concluded"
            //updateUserGameState(userId, gameId, state)
        }

        // Logar o estado selecionado localmente
        Log.d("View_Game_Fragment", "Selected option: $selectedOption")
    }

    fun setListViewHeightBasedOnItems(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }

    // Extensão de Int para converter dp em pixels
    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
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
}
