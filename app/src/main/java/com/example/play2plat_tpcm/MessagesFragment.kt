package com.example.play2plat_tpcm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Comment
import com.example.play2plat_tpcm.api.UserMessage
import com.google.android.gms.location.FusedLocationProviderClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MessagesFragment : Fragment(), MessagesAdapter.OnUserMessageListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var gameTextView: TextView
    private lateinit var ReplyingTo: TextView
    private lateinit var commentEditTextView: EditText
    private lateinit var imageImageView: ImageView
    private lateinit var sendImageView: ImageView
    private lateinit var iconCrossView: ImageView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var more_options_layout: ConstraintLayout
    private lateinit var commentsTextView: TextView
    private lateinit var searchButton: Button

    private var gameId: Int = 0
    private var gameName: String? = null
    private var primaryColor: Int = 0
    private var secondaryColor: Int = 0
    private var isAnswerPostId: Int? = 0
    //private var edited: Int = 0

    private var selectedImageUri: Uri? = null

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedPostId: Int = 0

    private val navigationViewModel: FragmentNavigationViewModel by viewModels()



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
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchButton = view.findViewById(R.id.search)

        val colors = intArrayOf(primaryColor, secondaryColor)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)




        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)
        val currentUserType = sharedPreferences.getInt("user_type_id", 0)


        // Chama a API para obter os users
        getUsersByMessage(userId)

        searchButton.setOnClickListener {
            redirectToUsersSearched()
        }

        return view
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
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

    private fun redirectToUsersSearched() {

        if (isNetworkAvailable()){
            val usersSearchedFragment = UsersSearchedFragment()
            navigationViewModel.addToStack(usersSearchedFragment)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, usersSearchedFragment)
                .addToBackStack(null)
                .commit()

        }
        else{
            redirectToNoConnectionFragment()

        }
    }



    private fun getUsersByMessage(userId: Int) {
        ApiManager.apiService.getUsersByMessage(userId).enqueue(object : Callback<List<UserMessage>> {
            override fun onResponse(
                call: Call<List<UserMessage>>,
                response: Response<List<UserMessage>>
            ) {
                if (response.isSuccessful) {
                    val usersMessages = response.body()
                    if (usersMessages != null && usersMessages.isNotEmpty()) {

                        recyclerView.adapter = MessagesAdapter(usersMessages, this@MessagesFragment)

                    } else {
                        recyclerView.adapter = MessagesAdapter(emptyList(), this@MessagesFragment)
                    }
                } else {
                    recyclerView.adapter = MessagesAdapter(emptyList(), this@MessagesFragment)
                    Log.e("MessagesFragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<UserMessage>>, t: Throwable) {
                Log.e("MessagesFragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }

    override fun onUserMessageClick(userTwoId: Int) {
        Log.d("MessagesFragment", "Ver direct messages")
        if (isNetworkAvailable()) {
            val userMessagesFragment = UserMessagesFragment.newInstance(userTwoId)
            navigationViewModel.addToStack(userMessagesFragment)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, userMessagesFragment)
                .addToBackStack(null)
                .commit()
        }
        else{
            redirectToNoConnectionFragment()
        }

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


    /*
    override fun onProfilePictureClick(userId: Int) {
        val viewGameFragment = Profile_Fragment.newInstance(userId)
        navigationViewModel.addToStack(viewGameFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

     */



    private fun getCommentDetails(postId: Int) {
        ApiManager.apiService.getCommentById(postId).enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    val comment = response.body()
                    comment?.let {
                        commentEditTextView.setText(it.comments)
                    }
                } else {
                    Log.e("GamePostsFragment", "Erro ao obter detalhes do comentário: ${response.message()}")
                    Toast.makeText(
                        context,
                        getString(R.string.error_get_comment_details),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na requisição para obter detalhes do comentário: ${t.message}")
                Toast.makeText(
                    context,
                    getString(R.string.request_comment_details_failure),
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }




    companion object {
        private const val ARG_GAME_ID = "gameId"
        private const val ARG_GAME_NAME = "gameName"
        private const val ARG_PRIMARY_COLOR = "primaryColor"
        private const val ARG_SECONDARY_COLOR = "secondaryColor"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

        @JvmStatic
        fun newInstance(gameId: Int, gameName: String, primaryColor: Int, secondaryColor: Int) =
            MessagesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                    putInt(ARG_PRIMARY_COLOR, primaryColor)
                    putInt(ARG_SECONDARY_COLOR, secondaryColor)
                }
            }
    }
}