package com.example.play2plat_tpcm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import com.example.play2plat_tpcm.api.UserGameStateResponse
import com.google.android.material.tabs.TabLayout
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
    private var userId: Int = 0
    private lateinit var tabLayout: TabLayout
    private var dominantColor: Int = 0
    private var vibrantColor: Int = 0
    private var clickCount: Int = 0
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mushroomImage: ImageView
    private lateinit var editIcon: ImageView
    private lateinit var frameLayout: FrameLayout


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
        tabLayout = view.findViewById(R.id.tab_layout)
        editIcon = view.findViewById(R.id.Edit_Icon)

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        currentUserType = sharedPreferences.getInt("user_type_id", 0)
        userId = sharedPreferences.getInt("user_id", 0)

        // Show or hide edit icon based on user type
        if (currentUserType == 1) {
            editIcon.visibility = View.VISIBLE
        } else {
            editIcon.visibility = View.GONE
        }

        backButton.setOnClickListener {
            if (isNetworkAvailable()) {
                requireActivity().onBackPressed()
            }
            else{
                redirectToNoConnectionFragment()
            }
        }

        loadCollections(view.context)
        loadUserGameState()

        collectionAccordion.setOnClickListener {
            toggleListVisibility(collectionList, collectionTitle)
        }

        val gameId = arguments?.getInt("gameId") ?: 6
        if (gameId != 0) {
            ApiManager.apiService.getGameById(gameId).enqueue(object : Callback<GameInfo> {
                override fun onResponse(call: Call<GameInfo>, response: Response<GameInfo>) {
                    if (response.isSuccessful) {
                        val game = response.body()
                        if (game != null) {
                            editIcon.setOnClickListener {
                                if (isNetworkAvailable()) {
                                    val editGameFragment = Edit_Game_Fragment.newInstance(game)
                                    requireActivity().supportFragmentManager.beginTransaction()
                                        .replace(R.id.layout, editGameFragment)
                                        .addToBackStack(null)
                                        .commit()
                                }
                                else{
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.need_online_edit_game),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }

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

                                        val gradientDrawable: GradientDrawable
                                        if (colorDifference < colorDifferenceThreshold) {
                                            // Se a diferença for muito baixa, escurecer ligeiramente a cor vibrante
                                            val darkerVibrantColor = ColorUtils.blendARGB(vibrantColor, Color.BLACK, 0.7f)
                                            val colors = intArrayOf(dominantColor, darkerVibrantColor)
                                            gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                                        } else {
                                            // Se a diferença for suficiente, use as cores normalmente
                                            val colors = intArrayOf(dominantColor, vibrantColor)
                                            gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                                        }

                                        containerLayout.background = gradientDrawable

                                        // Após definir o fundo, configure os fragmentos nos botões do TabLayout
                                        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

                                        // Adicionando listener para selecionar fragmentos ao alternar abas
                                        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                                            override fun onTabSelected(tab: TabLayout.Tab) {
                                                when (tab.position) {
                                                    0 -> {
                                                        val aboutFragment = AboutFragment.newInstance(gameId, game.description, game.genres, game.platforms, game.sequence, game.company)
                                                        replaceFragment(aboutFragment, "AboutFragmentTag")
                                                    }
                                                    1 -> {
                                                        val interactFragment = InteractFragment.newInstance(gameId, game.name, dominantColor, vibrantColor, game.averageStars)
                                                        replaceFragment(interactFragment, "InteractFragmentTag")
                                                    }
                                                }
                                            }

                                            override fun onTabUnselected(tab: TabLayout.Tab?) {
                                                // O fragmento pode ser pausado aqui, se necessário
                                            }

                                            override fun onTabReselected(tab: TabLayout.Tab?) {
                                                // Não faz nada aqui
                                            }
                                        })

                                        tabLayout.addTab(tabLayout.newTab().setText("About"))
                                        tabLayout.addTab(tabLayout.newTab().setText("Interact"))

                                        // Inicialmente, mostra o primeiro fragmento
                                        tabLayout.getTabAt(0)?.select()

                                        Log.d("View_Game_Fragment", "Calculated Colors for Gradient: $dominantColor and $vibrantColor")
                                    }
                                }

                                override fun onError(e: Exception?) {
                                    // Tratar erro de carregamento da imagem, se necessário
                                }
                            })


                            val pegiImageResId = when (game.pegiInfo) {
                                3 -> R.drawable.image_pegi3
                                7 -> R.drawable.image_pegi7
                                12 -> R.drawable.image_pegi12
                                16 -> R.drawable.image_pegi16
                                18 -> R.drawable.image_pegi18
                                else -> 0
                            }
                            if (pegiImageResId != 0) {
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



        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.mario_1up) // Replace with your audio file
        mushroomImage = view.findViewById(R.id.mushroom_image)
        mushroomImage.visibility = View.GONE

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
            if (isNetworkAvailable()) {
                toggleFavoriteState(userId, gameId, mediaPlayer)
            }
            else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_favoriting),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

        if (clickCount >= 20) {
            startMushroomAnimation()
        }

    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment, tag)
        transaction.commit()  // Remove `addToBackStack(tag)`
    }



    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos do mediaPlayer ao destruir o fragment
        mediaPlayer.release()
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


    private fun loadUserGameState() {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        ApiManager.apiService.getUserGameState(userId, gameId).enqueue(object : Callback<UserGameStateResponse> {
            override fun onResponse(call: Call<UserGameStateResponse>, response: Response<UserGameStateResponse>) {
                if (response.isSuccessful) {
                    val userGameStateResponse = response.body()
                    if (userGameStateResponse?.error == null) {
                        // Jogo encontrado, manipule o estado do jogo
                        val gameState = userGameStateResponse?.state
                        selectOptionInAccordion(gameState)
                    } else {
                        // Erro encontrado, manipule o erro
                        selectOptionInAccordion(null)
                    }
                }
            }

            override fun onFailure(call: Call<UserGameStateResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun selectOptionInAccordion(gameState: String?) {
        val selectedPosition = collectionInfoValues.indexOf(gameState)
        if (selectedPosition != -1) {
            collectionAdapter.updateSelectedPosition(selectedPosition)
            collectionTitle.text = gameState
        } else {
            collectionTitle.text = "Collections"
        }
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
        collectionAdapter = CollectionsAdapter(context, collectionInfoValues, collectionTitle, userId, gameId)
        collectionList.adapter = collectionAdapter
    }

    private fun updateFavoriteIcon() {
        if (isFavorited) {
            favoriteIcon.setImageResource(R.drawable.icon_favorited) // Update with filled heart drawable
        } else {
            favoriteIcon.setImageResource(R.drawable.icon_unfavorite) // Update with outline heart drawable
        }
    }

    private fun toggleFavoriteState(userId: Int, gameId: Int, mediaPlayer: MediaPlayer) {
        if (isFavorited) {
            // Remove from favorites
            ApiManager.apiService.deleteUserGameFavorite(gameId, userId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        isFavorited = false
                        clickCount++

                        // Verificar se atingiu 20 cliques consecutivos
                        if (clickCount >= 20) {
                            // Reiniciar contador de cliques
                            startMushroomAnimation()
                        }
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
                        clickCount++

                        // Verificar se atingiu 20 cliques consecutivos
                        if (clickCount >= 20) {
                            // Reiniciar contador de cliques
                            startMushroomAnimation()
                        }
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

    private fun startMushroomAnimation() {
        clickCount = 0 // Reinicia o contador de cliques

        // Exibe o cogumelo
        mushroomImage.visibility = View.VISIBLE

        // Configura a animação
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.mushroom_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // Inicia o som do 1up do Mario quando a animação começa
                mediaPlayer.start()
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Esconde o cogumelo quando a animação termina
                mushroomImage.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Inicia a animação no cogumelo
        mushroomImage.startAnimation(animation)
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
            setListViewHeightBasedOnItems(listView) // Chame a função para ajustar a altura do ListView
            collectionAccordion.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT // Defina a altura para wrap_content
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_spinner_up, 0)
        }
        // Solicite uma nova medida do layout após alterar os parâmetros de layout
        collectionAccordion.requestLayout()
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



