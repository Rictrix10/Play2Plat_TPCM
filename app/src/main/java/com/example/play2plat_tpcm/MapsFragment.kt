package com.example.play2plat_tpcm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import retrofit2.Call
import retrofit2.Callback as RetrofitCallback
import retrofit2.Response

class MapsFragment : Fragment() {

    private var gameId: Int = 0
    private lateinit var googleMap: GoogleMap
    private lateinit var commentTextView: TextView
    private lateinit var profilePicture: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var textPostTextView: TextView
    private lateinit var imagePost: ImageView
    private lateinit var container_layout: ConstraintLayout
    private val navigationViewModel: FragmentNavigationViewModel by viewModels()
    private var gameName: String? = null
    private var primaryColor: Int = 0
    private var secondaryColor: Int = 0
    private lateinit var gameTextView: TextView

    private val targetList = mutableListOf<Target>()

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        Log.d("MapsFragment", "Map is ready")
        // Chamar a API para obter os comentários do jogo
        fetchGameComments()

        // Configurar o listener para cliques nos marcadores
        googleMap.setOnMarkerClickListener { marker ->
            val username = marker.snippet  // snippet contém o username
            val comment = marker.title
            // title contém o comment

            // Atualizar os campos de texto com os dados do usuário
            usernameTextView.text = username
            textPostTextView.text = comment

            // Buscar os detalhes do comentário associado ao marker
            val commentData = marker.tag as? GameCommentsResponse
            commentData?.let {
                // Atualizar a imagem de perfil
                if (!it.user.avatar.isNullOrEmpty()) {
                    Picasso.get().load(it.user.avatar).into(profilePicture)
                } else {
                    profilePicture.setImageResource(R.drawable.icon_noimageuser)
                }

                // Atualizar a imagem do post
                if (!it.image.isNullOrEmpty()) {
                    imagePost.visibility = View.VISIBLE
                    Picasso.get().load(it.image).into(imagePost)
                } else {
                    imagePost.visibility = View.GONE
                }

                // Atualizar a localização
                locationTextView.text = it.location
            }

            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getInt(ARG_GAME_ID)
            gameName = it.getString(ARG_GAME_NAME)
            primaryColor = it.getInt(ARG_PRIMARY_COLOR)
            secondaryColor = it.getInt(ARG_SECONDARY_COLOR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        profilePicture = view.findViewById(R.id.profile_picture)
        usernameTextView = view.findViewById(R.id.username)
        locationTextView = view.findViewById(R.id.location)
        textPostTextView = view.findViewById(R.id.text_post)
        imagePost = view.findViewById(R.id.image_post)
        container_layout = view.findViewById(R.id.container)
        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        gameTextView = view.findViewById(R.id.game_title)

        gameTextView.text = gameName

        val colors = intArrayOf(primaryColor, secondaryColor)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        container_layout.background = gradientDrawable

        backButton.setOnClickListener {
            if (isNetworkAvailable()) {
                val fragmentManager = requireActivity().supportFragmentManager

                val currentFragment = fragmentManager.primaryNavigationFragment
                if (currentFragment != null) {
                    navigationViewModel.removeFromStack(currentFragment)
                }

                requireActivity().onBackPressed()
            }
            else{
                redirectToNoConnectionFragment()
            }
        }
        Log.d("MapsFragment", "onViewCreated called")
    }

    private fun fetchGameComments() {
        ApiManager.apiService.getPostsByGame(gameId).enqueue(object : RetrofitCallback<List<GameCommentsResponse>> {
            override fun onResponse(
                call: Call<List<GameCommentsResponse>>,
                response: Response<List<GameCommentsResponse>>
            ) {
                if (response.isSuccessful) {
                    val gameComments = response.body()
                    gameComments?.let {
                        // Adicionar marcadores no mapa para cada comentário
                        Log.d("MapsFragment", "Comments fetched successfully: ${gameComments.size} comments")
                        addMarkersToMap(gameComments)
                    }
                } else {
                    Log.e("MapsFragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<GameCommentsResponse>>, t: Throwable) {
                Log.e("MapsFragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    private fun addMarkersToMap(comments: List<GameCommentsResponse>) {
        for (comment in comments) {
            if (comment.latitude == null || comment.longitude == null) {
                Log.w(
                    "MapsFragment",
                    "Skipping comment with null latitude or longitude: ${comment.comments}"
                )
                continue
            }
            val location = LatLng(comment.latitude, comment.longitude)
            Log.d("MapsFragment", "Adding marker for comment: ${comment.comments} at $location")

            // Verificar se o avatar é nulo e definir a imagem padrão se necessário
            val avatarUrl = comment.user.avatar ?: ""
            val image = comment.image

            if (avatarUrl.isEmpty() || avatarUrl == "" || comment.user.username == null || comment.user.isDeleted == true) {
                val drawableResId = R.drawable.icon_noimageuser
                val bitmap = BitmapFactory.decodeResource(resources, drawableResId)

                // Criar um novo Bitmap com fundo branco
                val backgroundBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(backgroundBitmap)
                val whiteColor = resources.getColor(R.color.white, null)
                canvas.drawColor(whiteColor)

                // Redimensionar o bitmap original
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false)

                // Desenhar o bitmap redimensionado no canvas com fundo branco
                canvas.drawBitmap(resizedBitmap, 0f, 0f, null)

                val usernameVal = if (comment.user.username == null) {
                    context?.getString(R.string.deleted_user)
                } else {
                    comment.user.username
                }

                // Arredondar o bitmap com fundo branco
                val roundedBitmap = getRoundedBitmap(backgroundBitmap)
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(comment.comments)  // Usando o comentário como title
                    .snippet(usernameVal)  // Usando o username como snippet
                    .icon(BitmapDescriptorFactory.fromBitmap(roundedBitmap))

                val marker = googleMap.addMarker(markerOptions)
                marker?.tag = comment  // Associa o comentário ao marcador
                Log.d(
                    "MapsFragment",
                    "Marker added for comment with default avatar: ${comment.comments}"
                )

            } else {
                val target = object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bitmap?.let {
                            val roundedBitmap = getRoundedBitmap(it)
                            val markerOptions = MarkerOptions()
                                .position(location)
                                .title(comment.comments)  // Usando o comentário como title
                                .snippet(if (comment.user.username == null) context?.getString(R.string.deleted_user) else comment.user.username)  // Usando o username como snippet
                                .icon(BitmapDescriptorFactory.fromBitmap(roundedBitmap))

                            val marker = googleMap.addMarker(markerOptions)
                            marker?.tag = comment  // Associa o comentário ao marcador
                            Log.d("MapsFragment", "Marker added for comment: ${comment.comments}")
                        }
                        targetList.remove(this) // Remover o target da lista após o carregamento
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: android.graphics.drawable.Drawable?) {
                        Log.e(
                            "MapsFragment",
                            "Failed to load avatar for comment: ${comment.comments}, error: ${e?.message}"
                        )
                        targetList.remove(this) // Remover o target da lista após falha
                    }

                    override fun onPrepareLoad(placeHolderDrawable: android.graphics.drawable.Drawable?) {
                        // Placeholder, se necessário
                    }
                }

                // Adicionar o target à lista para manter a referência
                targetList.add(target)
                Picasso.get().load(comment.user.avatar).resize(50, 50).into(target)
            }
        }

        // Movimentar a câmera para o primeiro marcador (opcional)
        comments.firstOrNull { it.latitude != null && it.longitude != null }?.let {
            val firstLocation = LatLng(it.latitude, it.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f))
            Log.d("MapsFragment", "Camera moved to first comment location: $firstLocation")
        }
    }

    private fun getRoundedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 2f, paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }


    private fun redirectToNoConnectionFragment() {
        val noConnectionFragment= NoConnectionFragment()
        navigationViewModel.addToStack(noConnectionFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, noConnectionFragment)
            .addToBackStack(null)
            .commit()

    }

    companion object {
        private const val ARG_GAME_ID = "gameId"
        private const val ARG_GAME_NAME = "gameName"
        private const val ARG_PRIMARY_COLOR = "primaryColor"
        private const val ARG_SECONDARY_COLOR = "secondaryColor"

        @JvmStatic
        fun newInstance(gameId: Int, gameName: String, primaryColor: Int, secondaryColor: Int) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                    putInt(ARG_PRIMARY_COLOR, primaryColor)
                    putInt(ARG_SECONDARY_COLOR, secondaryColor)
                }
            }
    }
}
