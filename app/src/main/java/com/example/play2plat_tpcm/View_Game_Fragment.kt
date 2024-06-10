package com.example.play2plat_tpcm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.GameInfo
import com.example.play2plat_tpcm.api.UserGameFavorite
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.play2plat_tpcm.adapters.CollectionsAdapter
import android.util.Log

import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.viewpager2.widget.ViewPager2
import com.example.play2plat_tpcm.api.Avaliation
import com.example.play2plat_tpcm.api.UserGame
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.pow
import kotlin.math.sqrt

class View_Game_Fragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var companyTextView: TextView
    private lateinit var genresTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var gameImageView: ImageView
    private lateinit var pegiInfoImageView: ImageView
    private lateinit var isFreeImageView: ImageView
    private lateinit var backButton: ImageButton
    private lateinit var containerLayout: ConstraintLayout
    private lateinit var favoriteIcon: ImageView
    private lateinit var platformButtonsContainer: LinearLayout
    private var isFavorited: Boolean = false
    private lateinit var collectionAccordion: LinearLayout
    private lateinit var collectionTitle: TextView
    private lateinit var collectionList: ListView
    private lateinit var collectionInfoValues: Array<String>
    private lateinit var collectionAdapter: CollectionsAdapter
    private var gameId: Int = 0
    private var selectedOption: String? = null
    private var currentUserType: Int = 0
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var dominantColor: Int = 0
    private var vibrantColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getInt(ARG_GAME_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_game, container, false)

        // Initialize views
        nameTextView = view.findViewById(R.id.name)
        companyTextView = view.findViewById(R.id.company)

        gameImageView = view.findViewById(R.id.game)
        pegiInfoImageView = view.findViewById(R.id.pegi_info)
        isFreeImageView = view.findViewById(R.id.isFree)
        backButton = view.findViewById(R.id.back_button)
        containerLayout = view.findViewById(R.id.container_layout)
        favoriteIcon = view.findViewById(R.id.favorite_icon)
        collectionAccordion = view.findViewById(R.id.collection_accordion)
        collectionTitle = view.findViewById(R.id.collection_title)
        collectionList = view.findViewById(R.id.collection_list)

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        currentUserType = sharedPreferences.getInt("user_type_id", 0)

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        loadCollections(view.context)

        collectionAccordion.setOnClickListener {
            toggleListVisibility(collectionList, collectionTitle)
            handleAccordionSelection()
        }

        collectionList.setOnItemClickListener { parent, view, position, id ->
            val selectedOption = if (position >= 0 && position < collectionInfoValues.size) collectionInfoValues[position] else null
            updateUserGameStateWithSelectedOption(selectedOption)
        }

        val gameId = arguments?.getInt("gameId") ?: 6
        if (gameId != 0) {
            ApiManager.apiService.getGameById(gameId).enqueue(object : Callback<GameInfo> {
                override fun onResponse(call: Call<GameInfo>, response: Response<GameInfo>) {
                    if (response.isSuccessful) {
                        val game = response.body()
                        if (game != null) {
                            nameTextView.text = game.name
                            companyTextView.text = game.company
                            if(!game.isFree){
                                isFreeImageView.visibility = View.GONE
                            }else{
                                isFreeImageView.visibility = View.VISIBLE
                            }
                            Picasso.get().load(game.coverImage).into(gameImageView, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    val bitmap = (gameImageView.drawable as BitmapDrawable).bitmap
                                    Palette.from(bitmap).generate { palette ->
                                        val dominantColor = palette?.dominantSwatch?.rgb ?: 0
                                        val vibrantColor = palette?.vibrantSwatch?.rgb ?: 0

                                        val colorDifferenceThreshold = 8 // Define o limiar de diferença entre cores
                                        val colorDifference = colourDistance(dominantColor, vibrantColor)

                                        if (colorDifference < colorDifferenceThreshold) {
                                            // Se a diferença for muito baixa, escurecer ligeiramente a cor vibrante
                                            val darkerVibrantColor = ColorUtils.blendARGB(vibrantColor, Color.BLACK, 0.7f)
                                            val colors = intArrayOf(dominantColor, darkerVibrantColor)
                                            val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                                            containerLayout.background = gradientDrawable
                                            // Usar a cor mais escura no pagerAdapter
                                            val pagerAdapter = ViewGamesAdapter(requireActivity(), game.id, game.description, game.genres, game.platforms, game.name, game.sequence, game.company, dominantColor, darkerVibrantColor)
                                            viewPager.adapter = pagerAdapter
                                        } else {
                                            // Se a diferença for suficiente, use as cores normalmente
                                            val colors = intArrayOf(dominantColor, vibrantColor)
                                            val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                                            containerLayout.background = gradientDrawable
                                            // Usar a cor vibrante normal no pagerAdapter
                                            val pagerAdapter = ViewGamesAdapter(requireActivity(), game.id, game.description, game.genres, game.platforms, game.name, game.sequence, game.company, dominantColor, vibrantColor)
                                            viewPager.adapter = pagerAdapter
                                        }


                                        Log.d("View_Game_Fragment", "Calculated Colors for Gradient: $dominantColor and $vibrantColor")

                                        // Instancie o ViewPagerAdapter aqui com as cores calculadas




                                        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                            tab.text = when (position) {
                                                0 -> "About"
                                                1 -> "Interact"
                                                else -> null
                                            }
                                        }.attach()
                                    }
                                }

                                override fun onError(e: Exception?) {
                                    // Tratar erro de carregamento da imagem
                                }
                            })



                            val pegiImageResId = when (game.pegiInfo) {
                                3 -> R.drawable.pegi3
                                7 -> R.drawable.pegi7
                                12 -> R.drawable.pegi12
                                16 -> R.drawable.pegi16
                                18 -> R.drawable.pegi18
                                else -> 0
                            }
                            if (pegiImageResId!= 0) {
                                Picasso.get().load(pegiImageResId).into(pegiInfoImageView)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GameInfo>, t: Throwable) {}
            })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load favorite state
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        ApiManager.apiService.getUserGameFavorites(userId).enqueue(object : Callback<List<UserGameFavorite>> {
            override fun onResponse(call: Call<List<UserGameFavorite>>, response: Response<List<UserGameFavorite>>) {
                if (response.isSuccessful) {
                    val favoriteGames = response.body()
                    if (favoriteGames != null && favoriteGames.any { it.gameId == gameId }) {
                        isFavorited = true
                        updateFavoriteIcon()
                    }
                }
            }

            override fun onFailure(call: Call<List<UserGameFavorite>>, t: Throwable) {
                // Handle failure
            }
        })

        favoriteIcon.setOnClickListener {
            toggleFavoriteState(userId, gameId)
        }

        handleAccordionSelection()
    }


    fun colourDistance(color1: Int, color2: Int): Double {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        return sqrt((r1 - r2).toDouble().pow(2) + (g1 - g2).toDouble().pow(2) + (b1 - b2).toDouble().pow(2))
    }

    fun darkenColor(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= (1f - factor) // Reduzir o valor da componente de brilho (Value)
        return Color.HSVToColor(hsv)
    }
    private fun loadCollections(context: Context) {
        collectionInfoValues = context.resources.getStringArray(R.array.collections_names)
        collectionAdapter = CollectionsAdapter(context, collectionInfoValues, collectionTitle) { selectedOption ->
            updateUserGameStateWithSelectedOption(selectedOption)
        }
        collectionList.adapter = collectionAdapter
    }

    private fun updateFavoriteIcon() {
        if (isFavorited) {
            favoriteIcon.setImageResource(R.drawable.icon_favorited) // Update with filled heart drawable
        } else {
            favoriteIcon.setImageResource(R.drawable.icon_unfavorite) // Update with outline heart drawable
        }
    }

    private fun toggleFavoriteState(userId: Int, gameId: Int) {
        if (isFavorited) {
            // Remove from favorites
            ApiManager.apiService.deleteUserGameFavorite(gameId, userId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        isFavorited = false
                        updateFavoriteIcon()
                    } else {
                        // Handle the error response
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Handle failure
                }
            })
        } else {
            // Add to favorites
            val userGameFavorite = UserGameFavorite(userId, gameId)
            ApiManager.apiService.addUserGameFavorite(userGameFavorite).enqueue(object : Callback<UserGameFavorite> {
                override fun onResponse(call: Call<UserGameFavorite>, response: Response<UserGameFavorite>) {
                    if (response.isSuccessful) {
                        isFavorited = true
                        updateFavoriteIcon()
                    } else {
                        // Handle the error response
                    }
                }

                override fun onFailure(call: Call<UserGameFavorite>, t: Throwable) {
                    // Handle failure
                }
            })
        }
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

    private var userGameState: String? = null
    private fun handleAccordionSelection() {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        Log.d("View_Game_Fragment", "Fetching user games for userId: $userId")

        ApiManager.apiService.getUserGame(userId).enqueue(object : Callback<List<UserGame>> {
            override fun onResponse(call: Call<List<UserGame>>, response: Response<List<UserGame>>) {
                if (response.isSuccessful) {
                    val userGames = response.body()
                    if (userGames != null) {
                        val userGame = userGames.find { it.gameId == gameId }
                        val newUserGameState = userGame?.state // Armazenar o estado do jogo do usuário

                        // Verificar se o estado do jogo do usuário é diferente do estado selecionado atualmente
                        if (newUserGameState != selectedOption) {
                            userGameState = newUserGameState
                            selectOptionInAccordion(userGameState ?: "")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<UserGame>>, t: Throwable) {
                Log.e("View_Game_Fragment", "Failed to fetch user games", t)
            }
        })
    }


    private fun updateUserGameStateWithSelectedOption(option: String?) {
        selectedOption = option
        collectionTitle.text = option ?: "Collections"

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        ApiManager.apiService.getUserGame(userId).enqueue(object : Callback<List<UserGame>> {
            override fun onResponse(call: Call<List<UserGame>>, response: Response<List<UserGame>>) {
                if (response.isSuccessful) {
                    val userGames = response.body()
                    val existingUserGame = userGames?.find { it.gameId == gameId }
                    if (existingUserGame == null) {
                        // Não existe um UserGame, então adicionamos um novo
                        if (option != null) {
                            addUserGame(userId, gameId, option)
                        }
                    } else if (option == null) {
                        Log.d("View_Game_Fragment", "DELETING....")
                        deleteUserGame(userId, gameId)
                    } else {
                        Log.d("View_Game_Fragment", "UPDATING....")
                        updateUserGameState(userId, gameId, option)
                    }
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<List<UserGame>>, t: Throwable) {
                // Handle failure
            }
        })
    }


    private fun addUserGame(userId: Int, gameId: Int, option: String) {
        val userGame = UserGame(userId, gameId, option)
        ApiManager.apiService.addUserGame(userGame).enqueue(object : Callback<UserGame> {
            override fun onResponse(call: Call<UserGame>, response: Response<UserGame>) {
                if (response.isSuccessful) {
                    // UserGame adicionado com sucesso
                    Log.d("View_Game_Fragment", "User game added successfully")
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<UserGame>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun deleteUserGame(userId: Int, gameId: Int) {
        ApiManager.apiService.deleteUserGame(userId, gameId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // UserGame deletado com sucesso
                    Log.d("View_Game_Fragment", "User game deleted successfully")
                    collectionTitle.text = "Collections" // Atualize o título para "Collections"
                    selectedOption = null
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
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
            updateUserGameState(userId, gameId, state)
        }

        // Logar o estado selecionado localmente
        Log.d("View_Game_Fragment", "Selected option: $selectedOption")
    }


    private fun updateUserGameState(userId: Int, gameId: Int, state: String) {
        val userGame = UserGame(userId, gameId, state)
        ApiManager.apiService.updateUserGame(userId, gameId, userGame).enqueue(object : Callback<UserGame> {
            override fun onResponse(call: Call<UserGame>, response: Response<UserGame>) {
                if (response.isSuccessful) {
                    // O patch foi bem-sucedido
                    Log.d("View_Game_Fragment", "User game state updated successfully")
                } else {
                    // O patch falhou
                    Log.e("View_Game_Fragment", "Failed to update user game state: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserGame>, t: Throwable) {
                // Falha ao fazer o patch
                Log.e("View_Game_Fragment", "Failed to update user game state", t)
            }
        })
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

    /*
    private fun handleStarClick(rating: Int) {
        if (rating == currentRating) {
            // Reset stars if the same rating is clicked again
            updateStarViews(0)
            currentRating = 0
        } else {
            // Update stars to the new rating
            updateStarViews(rating)
            currentRating = rating
        }
    }
     */








    private fun addAvaliation(userId: Int, gameId: Int, stars: Int) {
        val avaliation = Avaliation(userId, gameId, stars.toFloat())
        ApiManager.apiService.addAvaliation(avaliation).enqueue(object : Callback<Avaliation> {
            override fun onResponse(call: Call<Avaliation>, response: Response<Avaliation>) {
                if (response.isSuccessful) {
                    Log.d("View_Game_Fragment", "Avaliation added successfully")
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<Avaliation>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun updateAvaliation(userId: Int, gameId: Int, stars: Int) {
        val avaliation = Avaliation(userId, gameId, stars.toFloat())
        ApiManager.apiService.updateAvaliation(userId, gameId, avaliation).enqueue(object : Callback<Avaliation> {
            override fun onResponse(call: Call<Avaliation>, response: Response<Avaliation>) {
                if (response.isSuccessful) {
                    Log.d("View_Game_Fragment", "Avaliation updated successfully")
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<Avaliation>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun deleteAvaliation(userId: Int, gameId: Int) {
        ApiManager.apiService.deleteAvaliation(userId, gameId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("View_Game_Fragment", "Avaliation deleted successfully")
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
            }
        })
    }



    companion object {
        private const val ARG_GAME_ID = "gameId"
        @JvmStatic
        fun newInstance(gameId: Int, platforms: ArrayList<String>) =
            View_Game_Fragment().apply {
                arguments = Bundle().apply {
                    putInt("gameId", gameId)
                    putStringArrayList("platforms", platforms)
                    putInt(ARG_GAME_ID, gameId)
                }
            }
    }

}



