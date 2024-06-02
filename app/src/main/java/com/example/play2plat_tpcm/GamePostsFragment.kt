package com.example.play2plat_tpcm

import com.example.play2plat_tpcm.Profile_Fragment
import com.example.play2plat_tpcm.User_Profile_Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Comment
import com.example.play2plat_tpcm.api.Game
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.example.play2plat_tpcm.api.UserRegister
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class GamePostsFragment : Fragment(), GamePostsAdapter.OnProfilePictureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameTextView: TextView
    private lateinit var commentEditTextView: EditText
    private lateinit var imageImageView: ImageView
    private lateinit var sendImageView: ImageView
    private var gameId: Int = 0

    private var selectedImageUri: Uri? = null

    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imageImageView.setImageURI(selectedImageUri)
            Log.d("EditProfile", "Selected image URI: $selectedImageUri")
        } else {
            Log.d("EditProfile", "No image URI received")
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
        // Inflate o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_game_posts, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        gameTextView = view.findViewById(R.id.game_title)
        commentEditTextView = view.findViewById(R.id.comment)
        imageImageView = view.findViewById(R.id.image_icon)
        sendImageView = view.findViewById(R.id.send_icon)

        imageImageView.setOnClickListener {
            Log.d("Image for Comment", "Select image button clicked")
            selectVisualMedia()
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        // Chama a API para obter os posts do jogo

        getGamePosts(gameId)

        sendImageView.setOnClickListener {
            val comments = commentEditTextView.text.toString()
            val context = requireContext()
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
            val file = bitmapToFile(context, bitmap)
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

            // Envia a imagem para o servidor
            val call = ApiManager.apiService.uploadImage(imagePart)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.string()
                        imageUrl?.let {
                            val pattern = Regex("\"url\":\"(\\S+)\"") // Cria um padrão regex para extrair a URL
                            val matchResult = pattern.find(it)

                            matchResult?.let { result ->
                                val coverImageUrl = result.groupValues[1] // Obtém o valor correspondente ao grupo capturado

                                val newComment = Comment(
                                    comments = comments,
                                    image = coverImageUrl,
                                    isAnswer = null,
                                    userId = userId,
                                    gameId = gameId,
                                    latitude = 0.0,
                                    longitude = 0.0,
                                )

                                Log.d("AddNewComment", "Novo comentário: $newComment")

                                // Fazer a chamada para a API para salvar o comentário
                                ApiManager.apiService.addComment(newComment)
                                    .enqueue(object : Callback<Comment> {
                                        override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                                            if (response.isSuccessful) {
                                                val postComment = response.body()
                                                Log.d("AddNewComment", "Comentário criado com sucesso: $postComment")
                                                Toast.makeText(context, "Comment posted successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Log.e("AddNewComment", "Error posting comment: ${response.message()}")
                                                Toast.makeText(context, "Error posting comment", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<Comment>, t: Throwable) {
                                            Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                                        }
                                    })
                            }
                        }
                    } else {
                        // Erro no upload
                        Log.e("AddNewComment", "Erro no upload: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Erro na requisição
                    Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                }
            })
        }

        return view
    }

    private fun redirectToMyProfile() {
        val profileFragment = Profile_Fragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, profileFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun redirectToUserProfile(userId: Int) {
        val userProfileFragment = User_Profile_Fragment.newInstance(userId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, userProfileFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onProfilePictureClick(userId: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("user_id", 0)

        if (userId == currentUserId) {
            redirectToMyProfile()
        } else {
            redirectToUserProfile(userId)
        }

    }

    private fun getGamePosts(gameId: Int) {
        // Defina o ID do jogo para obter os comentários
        ApiManager.apiService.getCommentByGame(gameId).enqueue(object : Callback<List<GameCommentsResponse>> {
            override fun onResponse(
                call: Call<List<GameCommentsResponse>>,
                response: Response<List<GameCommentsResponse>>
            ) {
                if (response.isSuccessful) {
                    val gamePosts = response.body()
                    Log.d("GamePostsFragment", "Resposta da API: $gamePosts")
                    if (gamePosts != null) {
                        recyclerView.adapter = GamePostsAdapter(gamePosts, this@GamePostsFragment)
                    }
                } else {
                    Log.e("GamePostsFragment", "Erro na resposta: ${response.errorBody()}")
                    // Tratar erro
                }
            }

            override fun onFailure(call: Call<List<GameCommentsResponse>>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na chamada da API: ${t.message}")
                // Tratar falha
            }
        })
    }

    private fun bitmapToFile(context: Context, bitmap: Bitmap): File {
        val filesDir = context.filesDir
        val imageFile = File(filesDir, "profile_image.jpg")

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return imageFile
    }

    private fun selectVisualMedia() {
        pickVisualMediaLauncher.launch("image/*")
    }

    private fun loadImage(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imageImageView)
        }
        /*else {
            profileImageView.setImageResource(R.drawable.noimageuser)
        }

         */
    }

    companion object {
        private const val ARG_GAME_ID = "gameId"
        @JvmStatic
        fun newInstance(gameId: Int) =
            GamePostsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GAME_ID, gameId)
                }
            }
    }
}
