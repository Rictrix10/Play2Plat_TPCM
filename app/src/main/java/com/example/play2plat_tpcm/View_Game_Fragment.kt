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
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.play2plat_tpcm.adapters.CollectionsAdapter


import android.widget.Button
import androidx.core.content.ContextCompat

class View_Game_Fragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var companyTextView: TextView
    private lateinit var genresTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var gameImageView: ImageView
    private lateinit var pegiInfoImageView: ImageView
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
    private lateinit var starViews: List<ImageView>
    private var currentRating = 0
    private var currentUserType: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_game, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        currentUserType = sharedPreferences.getInt("user_type_id", 0)


        // Initialize views
        nameTextView = view.findViewById(R.id.name)
        companyTextView = view.findViewById(R.id.company)
        genresTextView = view.findViewById(R.id.genres)
        descriptionTextView = view.findViewById(R.id.api_description)
        gameImageView = view.findViewById(R.id.game)
        pegiInfoImageView = view.findViewById(R.id.pegi_info)
        backButton = view.findViewById(R.id.back_button)
        containerLayout = view.findViewById(R.id.container_layout)
        favoriteIcon = view.findViewById(R.id.favorite_icon)
        // Adicionado

        collectionAccordion = view.findViewById(R.id.collection_accordion)
        collectionTitle = view.findViewById(R.id.collection_title)
        collectionList = view.findViewById(R.id.collection_list)

        starViews = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )

        for ((index, starView) in starViews.withIndex()) {
            starView.setOnClickListener { handleStarClick(index + 1) }
        }

        // Set up back button click listener
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        loadCollections(view.context)

        collectionAccordion.setOnClickListener {
            toggleListVisibility(collectionList, collectionTitle)
        }

        // Set up favorite icon click listener
        favoriteIcon.setOnClickListener {
            isFavorited = !isFavorited
            updateFavoriteIcon()
        }

        // Get the game ID from arguments or default to 53
        val gameId = arguments?.getInt("gameId") ?: 6
        if (gameId != 0) {
            ApiManager.apiService.getGameById(gameId).enqueue(object : Callback<GameInfo> {
                override fun onResponse(call: Call<GameInfo>, response: Response<GameInfo>) {
                    if (response.isSuccessful) {
                        val game = response.body()
                        if (game != null) {
                            // Update views with game data
                            nameTextView.text = game.name
                            companyTextView.text = game.company
                            genresTextView.text = game.genres.joinToString(" • ")
                            descriptionTextView.text = game.description
                            Picasso.get().load(game.coverImage).into(gameImageView, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    // Carregamento da imagem bem-sucedido, agora vamos extrair as cores principais
                                    val bitmap = (gameImageView.drawable as BitmapDrawable).bitmap
                                    Palette.from(bitmap).generate { palette ->
                                        // Obtendo as cores extraídas do Palette
                                        val dominantColor = palette?.dominantSwatch?.rgb ?: 0
                                        val vibrantColor = palette?.vibrantSwatch?.rgb ?: 0

                                        // Definindo o gradiente de duas cores usando as cores extraídas
                                        val colors = intArrayOf(dominantColor, vibrantColor)
                                        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                                        containerLayout.background = gradientDrawable
                                    }
                                }

                                override fun onError(e: Exception?) {
                                    // Tratamento de erro
                                }
                            })

                            // Set the correct PEGI image
                            val pegiImageResId = when (game.pegiInfo) {
                                3 -> R.drawable.pegi3
                                7 -> R.drawable.pegi7
                                12 -> R.drawable.pegi12
                                16 -> R.drawable.pegi16
                                18 -> R.drawable.pegi18
                                else -> 0 // Default to a placeholder image or handle as needed
                            }
                            if (pegiImageResId != 0) {
                                Picasso.get().load(pegiImageResId).into(pegiInfoImageView)
                            }

                            // Add platform buttons

                            // Obtenha as plataformas do argumento
                            val platforms = game.platforms
                            val canEditPlatforms = currentUserType == 1
                            if(platforms != null){
                                val platformsFragment = Platforms_List_Fragment.newInstance(platforms, canEditPlatforms)
                                childFragmentManager.beginTransaction().replace(R.id.platforms_fragment, platformsFragment).commit()
                            }

                        }
                    }
                }


                override fun onFailure(call: Call<GameInfo>, t: Throwable) {
                    // Handle failure
                }
            })
        }

        return view
    }

    private fun loadCollections(context: Context) {
        collectionInfoValues = context.resources.getStringArray(R.array.collections_names)
        collectionAdapter = CollectionsAdapter(context, collectionInfoValues, collectionTitle)
        collectionList.adapter = collectionAdapter
    }

    private fun updateFavoriteIcon() {
        if (isFavorited) {
            favoriteIcon.setImageResource(R.drawable.icon_favorited) // Update with filled heart drawable
        } else {
            favoriteIcon.setImageResource(R.drawable.icon_unfavorite) // Update with outline heart drawable
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

    private fun updateStarViews(rating: Int) {
        for ((index, starView) in starViews.withIndex()) {
            if (index < rating) {
                starView.setImageResource(R.drawable.icon_star_full)
            } else {
                starView.setImageResource(R.drawable.icon_star_outline)
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(gameId: Int, platforms: ArrayList<String>) =
            View_Game_Fragment().apply {
                arguments = Bundle().apply {
                    putInt("gameId", gameId)
                    putStringArrayList("platforms", platforms)
                }
            }
    }

}



