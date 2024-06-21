package com.example.play2plat_tpcm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.GameCommentsResponse

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

    private val targetList = mutableListOf<Target>()

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        Log.d("MapsFragment", "Map is ready")
        // Chamar a API para obter os comentários do jogo
        fetchGameComments()

        // Configurar o listener para cliques nos marcadores
        googleMap.setOnMarkerClickListener { marker ->
            val username = marker.snippet  // snippet contém o username
            val comment = marker.title  // title contém o comment

            val displayText = "$username: $comment"
            commentTextView.text = displayText
            true
        }
    }

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
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        commentTextView = view.findViewById(R.id.commentTextView)
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

            if (avatarUrl.isEmpty() || avatarUrl == "" || avatarUrl == "eliminated" || comment.user.isDeleted == true) {
                val drawableResId = R.drawable.noimageprofile
                val bitmap = BitmapFactory.decodeResource(resources, drawableResId)

                // Criar um novo Bitmap com fundo branco
                val backgroundBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(backgroundBitmap)
                canvas.drawColor(Color.WHITE) // Pintar o fundo de branco

                // Redimensionar o bitmap original
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false)

                // Desenhar o bitmap redimensionado no canvas com fundo branco
                canvas.drawBitmap(resizedBitmap, 0f, 0f, null)

                val usernameVal = if (avatarUrl == "eliminated") {
                    "Deleted User"
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
                googleMap.addMarker(markerOptions)
                Log.d(
                    "MapsFragment",
                    "Marker added for comment with default avatar: ${comment.comments}"
                )

            // Carregar a imagem do avatar usando Picasso e arredondar
            } else {
                val target = object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bitmap?.let {
                            val roundedBitmap = getRoundedBitmap(it)
                            val markerOptions = MarkerOptions()
                                .position(location)
                                .title(comment.comments)  // Usando o comentário como title
                                //.snippet("${comment.user.username}")  // Usando o username como snippet
                                .snippet(if (comment.user.avatar == "eliminated") "Deleted User" else comment.user.username)  // Usando o username como snippet
                                .icon(BitmapDescriptorFactory.fromBitmap(roundedBitmap))
                            googleMap.addMarker(markerOptions)
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

    companion object {
        private const val ARG_GAME_ID = "gameId"

        @JvmStatic
        fun newInstance(gameId: Int) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GAME_ID, gameId)
                }
            }
    }
}
