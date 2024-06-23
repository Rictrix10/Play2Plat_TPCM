package com.example.play2plat_tpcm

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.play2plat_tpcm.adapters.CompanyAdapter
import com.example.play2plat_tpcm.adapters.GenresAdapter
import com.example.play2plat_tpcm.adapters.SequenceAdapter
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Company
import com.example.play2plat_tpcm.api.Filters
import com.example.play2plat_tpcm.api.Genre
import com.example.play2plat_tpcm.api.Sequence
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Filters_Fragment : Fragment(), Platforms_List_Fragment.OnPlatformsSelectedListener {

    private lateinit var companyAdapter: CompanyAdapter
    private lateinit var companyAccordion: LinearLayout
    private lateinit var companyTitle: TextView
    private lateinit var companyList: ListView
    private lateinit var companies: List<Company>
    private lateinit var sequenceAdapter: SequenceAdapter
    private lateinit var sequenceAccordion: LinearLayout
    private lateinit var sequenceTitle: TextView
    private lateinit var sequenceList: ListView
    private lateinit var sequences: List<Sequence>
    private lateinit var genreAdapter: GenresAdapter
    private lateinit var genreAccordion: LinearLayout
    private lateinit var genreTitle: TextView
    private lateinit var genreList: ListView
    private lateinit var genres: List<Genre>
    private lateinit var alphaButton: Button
    private lateinit var recentButton: Button
    private lateinit var rateAvgButton: Button
    private lateinit var mostFavoritedButton: Button
    private lateinit var apply_filter_button: Button
    private lateinit var free_games_checkbox: CheckBox
    private lateinit var ascending: RadioButton
    private lateinit var descending: RadioButton
    private var selectedPlatforms: List<String> = emptyList()
    private var selectedGenres: List<String> = emptyList()
    private var selectedCompanies: List<String> = emptyList()
    private var selectedSequences: List<String> = emptyList()
    private var selectedOrderType: String = "Name"
    private var onlyFreeGames: Boolean = false
    private var orderPreference: String = "Ascending"
    private lateinit var backImageView: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filters, container, false)

        companyAccordion = view.findViewById(R.id.company_accordion)
        companyTitle = view.findViewById(R.id.company_title)
        companyList = view.findViewById(R.id.company_list)

        sequenceAccordion = view.findViewById(R.id.sequence_accordion)
        sequenceTitle = view.findViewById(R.id.sequence_title)
        sequenceList = view.findViewById(R.id.sequence_list)

        genreAccordion = view.findViewById(R.id.genres_accordion)
        genreTitle = view.findViewById(R.id.genres_title)
        genreList = view.findViewById(R.id.genres_list)
        apply_filter_button = view.findViewById(R.id.apply_filter_button)

        free_games_checkbox = view.findViewById(R.id.free_games_checkbox)
        ascending = view.findViewById(R.id.ascending)
        descending = view.findViewById(R.id.descending)

        backImageView = view.findViewById(R.id.back_icon)

        backImageView.setOnClickListener {
            if (isNetworkAvailable()) {
                requireActivity().onBackPressed()
            }
            else{
                redirectToNoConnectionFragment()
            }
        }

        companyAccordion.setOnClickListener {
            toggleListVisibility(companyList, companyTitle, R.drawable.icon_companies, "Company")
        }

        sequenceAccordion.setOnClickListener {
            toggleListVisibility(sequenceList, sequenceTitle, R.drawable.icon_sequence, "Sequences")
        }

        genreAccordion.setOnClickListener {
            toggleListVisibility(genreList, genreTitle, R.drawable.icon_genres, "Genres")
        }

        loadCompanies(view.context)
        loadSequences(view.context)
        loadGenres(view.context)

        apply_filter_button.setOnClickListener {
            val genresText = genreTitle.text.toString().split(", ").joinToString(",") { it.trim() }
            val companiesText = companyTitle.text.toString().split(", ").joinToString(",") { it.trim() }
            val sequencesText = sequenceTitle.text.toString().split(", ").joinToString(",") { it.trim() }

            val orderTypeText = when {
                alphaButton.alpha == 1.0f -> "alphabetical"
                recentButton.alpha == 1.0f -> "recent"
                rateAvgButton.alpha == 1.0f -> "averageStars"
                mostFavoritedButton.alpha == 1.0f -> "mostFavorited"
                else -> "Unknown"
            }

            val onlyFreeGamesText = free_games_checkbox.isChecked

            val orderPreferenceText = ascending.isChecked

            val selectedGenres = if (genresText == "Genres") emptyList<String>() else genresText.split(",")
            val selectedCompanies = if (companiesText == "Companies") emptyList<String>() else companiesText.split(",")
            val selectedSequences = if (sequencesText == "Sequences") emptyList<String>() else sequencesText.split(",")

            val filters = Filters(
                genres = if (selectedGenres.isEmpty()) emptyList() else selectedGenres,
                companies = if (selectedCompanies.isEmpty()) emptyList() else selectedCompanies,
                platforms = this.selectedPlatforms,
                sequences = if (selectedSequences.isEmpty()) emptyList()  else selectedSequences,
                free = if (onlyFreeGamesText) true else null,
                isAscending = orderPreferenceText,
                orderType = orderTypeText
            )

            val viewMoreGamesFragment = ViewMoreGames_Fragment.newInstance("Filtered", "Founded", filters)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, viewMoreGamesFragment)
                .addToBackStack(null)
                .commit()
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Criar uma instância do Platforms_List_Fragment
        fun createPlatformsListFragment(): Platforms_List_Fragment {
            return Platforms_List_Fragment.newInstance(
                platforms = listOf("PC", "XBox", "PlayStation", "Switch", "Android", "Mac/IOS"),
                canEditPlatforms = true,
                isUserPlatforms = true,
                id = 123,
                isForFilters = true // Defina como true para a lógica de filtros
            )
        }

        // Função para substituir o Platforms_List_Fragment
        fun resetPlatformsFragment() {
            val newPlatformsListFragment = createPlatformsListFragment()
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.platforms_fragment, newPlatformsListFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        resetPlatformsFragment()

        val resetButton: Button = view.findViewById(R.id.black_square)
        resetButton.setOnClickListener {
            // Resetar variáveis de filtro
            selectedPlatforms = emptyList()
            selectedGenres = emptyList()
            selectedCompanies = emptyList()
            selectedSequences = emptyList()
            selectedOrderType = "Name"
            onlyFreeGames = false
            orderPreference = "Ascending"

            // Atualizar visualizações conforme necessário
            companyTitle.text = getString(R.string.companies)
            sequenceTitle.text = getString(R.string.sequencess)
            genreTitle.text = getString(R.string.genres)
            free_games_checkbox.isChecked = false
            ascending.isChecked = true

            // Também é possível limpar a seleção em ListView ou outras visualizações de filtro, se necessário
            companyAdapter.clearSelection()
            sequenceAdapter.clearSelection()
            genreAdapter.clearSelection()

            // Resetar o Platforms_List_Fragment
            resetPlatformsFragment()

            // Resetar estados visuais dos botões
            alphaButton.alpha = 1.0f
            recentButton.alpha = 0.5f
            rateAvgButton.alpha = 0.5f
            mostFavoritedButton.alpha = 0.5f

            // Mostrar mensagem de confirmação
            Toast.makeText(context, getString(R.string.filters_reset_success), Toast.LENGTH_SHORT).show()

        }

        alphaButton = view.findViewById(R.id.alpha_button)
        recentButton = view.findViewById(R.id.recent_button)
        rateAvgButton = view.findViewById(R.id.rate_avg_button)
        mostFavoritedButton = view.findViewById(R.id.most_favorited_button)

        // Configura o botão de alphabetical como ativo
        setButtonActive(alphaButton)

        // Configura o ouvinte de clique para os botões
        alphaButton.setOnClickListener { setButtonActive(alphaButton) }
        recentButton.setOnClickListener { setButtonActive(recentButton) }
        rateAvgButton.setOnClickListener { setButtonActive(rateAvgButton) }
        mostFavoritedButton.setOnClickListener { setButtonActive(mostFavoritedButton) }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun redirectToNoConnectionFragment() {
        val noConnectionFragment= NoConnectionFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, noConnectionFragment)
            .addToBackStack(null)
            .commit()

    }

    override fun onPlatformsSelected(selectedPlatforms: List<String>) {
        // Tratar a lista de plataformas selecionadas
        val platformsString = selectedPlatforms.joinToString(", ")
        val message = getString(R.string.selected_platforms, platformsString)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        this.selectedPlatforms = selectedPlatforms
    }


    private fun toggleListVisibility(listView: ListView, titleView: TextView, iconResource: Int, filterType: String) {
        val isExpanded = listView.visibility == View.VISIBLE

        val initialHeight = if (isExpanded) listView.height else 0
        val targetHeight = if (isExpanded) 0 else getTargetHeight(listView)

        val valueAnimator = ValueAnimator.ofInt(initialHeight, targetHeight)
        valueAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            val layoutParams = listView.layoutParams
            layoutParams.height = animatedValue
            listView.layoutParams = layoutParams
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (!isExpanded) {
                    listView.visibility = View.VISIBLE
                    titleView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, R.drawable.icon_up, 0)
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (isExpanded) {
                    listView.visibility = View.GONE
                    titleView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, R.drawable.icon_down, 0)
                }
                // Reajuste a altura da ListView após a conclusão da animação

            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        valueAnimator.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        valueAnimator.start()
    }

    private fun getTargetHeight(view: View): Int {
        val listAdapter = (view as ListView).adapter ?: return 0
        val totalItems = listAdapter.count

        // Calcula a altura dos itens
        var totalHeight = 0
        for (i in 0 until totalItems) {
            val listItem = listAdapter.getView(i, null, view)
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }

        // Se houver mais de 5 itens, calcula a altura dos primeiros 5 itens
        if (totalItems > 5) {
            totalHeight = 0
            for (i in 0 until 5) {
                val listItem = listAdapter.getView(i, null, view)
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight
            }
        }

        // Define a altura da ListView
        val params = view.layoutParams
        val targetHeight = if (totalItems > 5) {
            totalHeight + (view.dividerHeight * (5 - 1)) // Altura dos 5 itens + divisores
        } else {
            totalHeight + (view.dividerHeight * (totalItems - 1)) // Altura de todos os itens + divisores
        }

        return targetHeight
    }

    private fun loadCompanies(context: Context) {
        ApiManager.apiService.getCompanies().enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList()
                    if (companies.isNotEmpty()) {
                        companyAdapter = CompanyAdapter(context, companies, companyTitle, true, null)
                        companyList.adapter = companyAdapter
                        adjustListViewHeight(companyList)
                    }
                } else {
                    Log.e("AddNewGame", "Erro ao carregar empresas: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                Log.e("AddNewGame", "Falha na requisição: ${t.message}")
            }
        })
    }

    private fun loadSequences(context: Context) {
        ApiManager.apiService.getSequences().enqueue(object : Callback<List<Sequence>> {
            override fun onResponse(call: Call<List<Sequence>>, response: Response<List<Sequence>>) {
                if (response.isSuccessful) {
                    sequences = response.body() ?: emptyList()
                    if (sequences.isNotEmpty()) {
                        sequenceAdapter = SequenceAdapter(context, sequences, sequenceTitle, true, null)
                        sequenceList.adapter = sequenceAdapter
                        adjustListViewHeight(sequenceList)
                    }
                } else {
                    Log.e("AddNewGame", "Erro ao carregar sequências: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Sequence>>, t: Throwable) {
                Log.e("AddNewGame", "Falha na requisição: ${t.message}")
            }
        })
    }

    private fun loadGenres(context: Context) {
        ApiManager.apiService.getGenres().enqueue(object : Callback<List<Genre>> {
            override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                if (response.isSuccessful) {
                    genres = response.body() ?: emptyList()
                    if (genres.isNotEmpty()) {
                        genreAdapter = GenresAdapter(context, genres, genreTitle)
                        genreList.adapter = genreAdapter
                        adjustListViewHeight(genreList)
                    }
                } else {
                    Log.e("AddNewGame", "Erro ao carregar gêneros: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                Log.e("AddNewGame", "Falha na requisição: ${t.message}")
            }
        })
    }

    private fun adjustListViewHeight(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        val totalItems = listAdapter.count

        // Calcula a altura dos itens
        var totalHeight = 0
        for (i in 0 until totalItems) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }

        // Se houver mais de 5 itens, calcula a altura dos primeiros 5 itens
        if (totalItems > 5) {
            totalHeight = 0
            for (i in 0 until 5) {
                val listItem = listAdapter.getView(i, null, listView)
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight
            }
        }

        // Define a altura da ListView
        val params = listView.layoutParams
        params.height = if (totalItems > 5) {
            totalHeight + (listView.dividerHeight * (5 - 1)) // Altura dos 5 itens + divisores
        } else {
            totalHeight + (listView.dividerHeight * (totalItems - 1)) // Altura de todos os itens + divisores
        }
        listView.layoutParams = params
        listView.requestLayout()

        // Habilita/desabilita o scroll
        listView.isScrollContainer = totalItems > 5
    }

    private fun setButtonActive(button: Button) {
        alphaButton.alpha = if (button == alphaButton) 1.0f else 0.5f
        recentButton.alpha = if (button == recentButton) 1.0f else 0.5f
        rateAvgButton.alpha = if (button == rateAvgButton) 1.0f else 0.5f
        mostFavoritedButton.alpha = if (button == mostFavoritedButton) 1.0f else 0.5f
    }
}


