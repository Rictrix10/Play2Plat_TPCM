package com.example.play2plat_tpcm

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Comment
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.example.play2plat_tpcm.api.GeoNamesResponse
import com.example.play2plat_tpcm.api.GeoNamesServiceBuilder.service
import com.example.play2plat_tpcm.api.LocationInfo
import com.example.play2plat_tpcm.api.Message
import com.example.play2plat_tpcm.api.MessagesDetails
import com.example.play2plat_tpcm.api.PatchComment
import com.example.play2plat_tpcm.api.PatchMessage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale

class UserMessagesFragment : Fragment(), UserMessagesAdapter.OnProfilePictureClickListener, UserMessagesAdapter.OnReplyClickListener, UserMessagesAdapter.onMoreOptionsClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameTextView: TextView
    private lateinit var ReplyingTo: TextView
    private lateinit var commentEditTextView: EditText
    private lateinit var imageImageView: ImageView
    private lateinit var sendImageView: ImageView
    private lateinit var iconCrossView: ImageView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var container_layout: ConstraintLayout
    private lateinit var more_options_layout: ConstraintLayout
    private lateinit var commentsTextView: TextView

    private var userTwoId: Int = 0
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
            userTwoId = it.getInt(ARG_USER_TWO_ID)
            //primaryColor = it.getInt(ARG_PRIMARY_COLOR)
            //secondaryColor = it.getInt(ARG_SECONDARY_COLOR)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_messages, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        gameTextView = view.findViewById(R.id.game_title)
        commentEditTextView = view.findViewById(R.id.comment)
        imageImageView = view.findViewById(R.id.image_icon)
        sendImageView = view.findViewById(R.id.send_icon)
        container_layout = view.findViewById(R.id.container)
        more_options_layout = view.findViewById(R.id.more_options_layout)
        ReplyingTo = view.findViewById(R.id.replying_to_text)
        gameTextView.text = gameName
        editButton = view.findViewById(R.id.option_edit)
        deleteButton = view.findViewById(R.id.option_delete)
        iconCrossView = view.findViewById(R.id.icon_cross)
        commentsTextView = view.findViewById(R.id.comments)


        val colors = intArrayOf(primaryColor, secondaryColor)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        container_layout.background = gradientDrawable

        imageImageView.setImageResource(R.drawable.icon_image)

        imageImageView.setOnClickListener {
            selectVisualMedia()
        }

        val backButton = view.findViewById<ImageButton>(R.id.back_button)
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

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)
        val currentUserType = sharedPreferences.getInt("user_type_id", 0)


        if (currentUserType == 1) {

            val params = commentsTextView.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 28.dpToPx() // Defina a margem superior desejada
            commentsTextView.layoutParams = params
        } else {

            val params = commentsTextView.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 0 // Remove a margem superior
            commentsTextView.layoutParams = params
        }

        // Chama a API para obter os posts do jogo
        getMessagesUsers(userId, userTwoId)



        sendImageView.setOnClickListener {
            if (isNetworkAvailable()) {
                sendMessage(userId, userTwoId)
            }
            else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.comment_post_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        deleteButton.setOnClickListener(){
            if (isNetworkAvailable()) {
                deleteMessageWithConfirmation(userId)
            }
            else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.delete_comment_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
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



    private fun getMessagesUsers(userOneId: Int, userTwoId: Int) {
        ApiManager.apiService.getMessagesBetweenUsersDetails(userOneId, userTwoId).enqueue(object : Callback<List<MessagesDetails>> {
            override fun onResponse(
                call: Call<List<MessagesDetails>>,
                response: Response<List<MessagesDetails>>
            ) {
                if (response.isSuccessful) {
                    val messages = response.body()
                    if (messages != null && messages.isNotEmpty()) {
                        recyclerView.adapter = UserMessagesAdapter(messages, this@UserMessagesFragment, this@UserMessagesFragment, this@UserMessagesFragment, false)

                    } else {
                        Log.e("UserMessagesFragment", "Sem mensagens.")
                        recyclerView.adapter = UserMessagesAdapter(emptyList(), this@UserMessagesFragment, this@UserMessagesFragment, this@UserMessagesFragment, false)
                    }
                } else {
                    recyclerView.adapter = UserMessagesAdapter(emptyList(), this@UserMessagesFragment, this@UserMessagesFragment, this@UserMessagesFragment, false)
                    Log.e("GamePostsFragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<MessagesDetails>>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }



    private fun sendMessage(userId: Int, userTwoId: Int) {
        val message = commentEditTextView.text.toString()
        if (selectedImageUri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
            val file = bitmapToFile(requireContext(), bitmap)
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

            val call = ApiManager.apiService.uploadImage(imagePart)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.string()
                        imageUrl?.let {
                            val pattern = Regex("\"url\":\"(\\S+)\"")
                            val matchResult = pattern.find(it)
                            matchResult?.let { result ->
                                val coverImageUrl = result.groupValues[1]
                                postMessage(message, coverImageUrl, userId, userTwoId, null)
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error_upload_image),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                }
            })
        } else {
            postMessage(message, null, userId, userTwoId, null)
        }
    }

    private fun editMessage(userId: Int, userTwoId: Int) {
        val message = commentEditTextView.text.toString()
        if (selectedImageUri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
            val file = bitmapToFile(requireContext(), bitmap)
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

            val call = ApiManager.apiService.uploadImage(imagePart)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.string()
                        imageUrl?.let {
                            val pattern = Regex("\"url\":\"(\\S+)\"")
                            val matchResult = pattern.find(it)
                            matchResult?.let { result ->
                                val coverImageUrl = result.groupValues[1]
                                patchMessage(message, userId, userTwoId, null)
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error_upload_image),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                }
            })
        } else {
            patchMessage(message, userId, userTwoId, null)
        }
    }



    private fun postMessage(message: String, image: String?, userOneId: Int, userTwoId: Int, date: String?) {
        val newMessage = Message(
            id = 0,
            message = message,
            image = image,
            isAnswer = if (isAnswerPostId != 0) isAnswerPostId else null,
            userOneId = userOneId,
            userTwoId = userTwoId,
            date = date
        )

        if(message.isEmpty()){
            Toast.makeText(
                context,
                getString(R.string.comment_empty_error),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        ApiManager.apiService.sendMessage(newMessage)
            .enqueue(object : Callback<Message> {
                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    if (response.isSuccessful) {
                        val postMessage = response.body()
                        Toast.makeText(
                            context,
                            getString(R.string.comment_post_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        more_options_layout.visibility = View.GONE
                        commentEditTextView.text.clear()
                        selectedImageUri = null
                        imageImageView.setImageResource(R.drawable.icon_image)
                        ReplyingTo.visibility = View.GONE
                        ReplyingTo.text = null
                        isAnswerPostId = null
                        iconCrossView.visibility = View.GONE
                        getMessagesUsers(userOneId, userTwoId)  // Refresh the posts after posting a new comment
                    } else {
                        Log.e("AddNewComment", "Error posting comment: ${response.message()}")
                        Toast.makeText(
                            context,
                            getString(R.string.error_posting_comment),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                }
            })
    }

    private fun patchMessage(message: String, userOneId: Int, userTwoId: Int, date: String?) {
        val editedMessage = PatchMessage(
            message = message,
            userOneId = userOneId,
            userTwoId = userTwoId,
            date = date
        )

        if(commentEditTextView.getText().toString().trim().isEmpty()){
            Toast.makeText(
                context,
                getString(R.string.comment_empty_error),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        ApiManager.apiService.editMessage(selectedPostId, editedMessage)
            .enqueue(object : Callback<PatchMessage> {
                override fun onResponse(call: Call<PatchMessage>, response: Response<PatchMessage>) {
                    if (response.isSuccessful) {
                        val patchComment = response.body()
                        Toast.makeText(
                            context,
                            getString(R.string.comment_update_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        more_options_layout.visibility = View.GONE
                        commentEditTextView.text.clear()
                        selectedImageUri = null
                        imageImageView.setImageResource(R.drawable.icon_image)
                        isAnswerPostId = null
                        ReplyingTo.visibility = View.GONE
                        iconCrossView.visibility = View.GONE
                        //edited = 1
                        getMessagesUsers(userOneId, userTwoId)  // Refresh the posts after posting a new comment
                    } else {
                        Log.e("EditMessage", "Error updating message: ${response.message()}")
                        Toast.makeText(
                            context,
                            getString(R.string.error_update_comment),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<PatchMessage>, t: Throwable) {
                    Log.e("AddNewComment", "Erro na requisição: ${t.message}")
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

    override fun onProfilePictureClick(userId: Int) {
        val viewGameFragment = Profile_Fragment.newInstance(userId)
        navigationViewModel.addToStack(viewGameFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onReplyClick(postId: Int, username: String?) {
        val sharedPreferencesEdits = requireActivity().getSharedPreferences("Editions", Context.MODE_PRIVATE)
        if(ReplyingTo.visibility == View.GONE ){
            ReplyingTo.visibility = View.VISIBLE
            iconCrossView.visibility = View.VISIBLE


            more_options_layout.visibility = View.GONE
            isAnswerPostId = postId
            ReplyingTo.text = SpannableStringBuilder().append(context?.getString(R.string.replying)).append(" ").append(username)
        }

        else if(sharedPreferencesEdits.getInt("edited", 0) == 0){
            ReplyingTo.visibility = View.VISIBLE
            iconCrossView.visibility = View.VISIBLE


            more_options_layout.visibility = View.GONE
            isAnswerPostId = postId
            commentEditTextView.text.clear()
            ReplyingTo.text = SpannableStringBuilder().append(context?.getString(R.string.replying)).append(" ").append(username)
            val editor = sharedPreferencesEdits.edit()
            editor.putInt("edited", 1)
            editor.apply()
        }


        else{
            ReplyingTo.visibility = View.GONE
            iconCrossView.visibility = View.GONE
            ReplyingTo.text = null
            isAnswerPostId = null
        }

        iconCrossView.setOnClickListener{
            ReplyingTo.visibility = View.GONE
            iconCrossView.visibility = View.GONE
            ReplyingTo.text = null
            isAnswerPostId = null
        }
    }

    override fun onOptionsClick(postId: Int) {
        var clicked = 0
        val sharedPreferencesEdits = requireActivity().getSharedPreferences("Editions", Context.MODE_PRIVATE)
        val editor = sharedPreferencesEdits.edit()
        editor.putInt("edited", 0)
        editor.apply()


        Log.d("Clicked: ", "${clicked}")
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)
        selectedPostId = postId
        if (more_options_layout.visibility == View.VISIBLE) {
            more_options_layout.visibility = View.GONE
        } else {
            more_options_layout.visibility = View.VISIBLE

            ReplyingTo.visibility = View.GONE
            iconCrossView.visibility = View.GONE
            ReplyingTo.text = null
            isAnswerPostId = null
        }
        editButton.setOnClickListener {
            if (clicked == 0) {
                clicked = 1
                more_options_layout.visibility = View.GONE
                ReplyingTo.visibility = View.VISIBLE
                iconCrossView.visibility = View.VISIBLE

                imageImageView.setOnClickListener(null)

                val editing = getString(R.string.editing)
                ReplyingTo.text = SpannableStringBuilder().append(editing).append(" ")
                getMessageDetails(postId)
                sendImageView.setOnClickListener(null)

                sendImageView.setOnClickListener {

                    if(sharedPreferencesEdits.getInt("edited", 0) == 0){
                        if(isNetworkAvailable()) {
                            editMessage(userId, userTwoId)
                            //getLocationAndPatchComment(userId, gameId)
                            if (commentEditTextView.getText().toString().trim().isEmpty()) {
                            } else {
                                clicked = 1

                                val editor = sharedPreferencesEdits.edit()
                                editor.putInt("edited", 1)
                                editor.apply()
                            }
                        }
                        else{
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.edit_comment_error),
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                    else{
                        if(isNetworkAvailable()) {
                            //getLocationAndPostComment(userId, gameId)
                            sendMessage(userId, userTwoId)
                            sendMessage(userId, userTwoId)
                        }
                        else{
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_posting_comment),
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                }
            } else {
                clicked = 0
                commentEditTextView.setText(null)

                sendImageView.setOnClickListener(null)

                sendImageView.setOnClickListener {
                    //getLocationAndPostComment(userId, gameId)
                }
            }

            iconCrossView.setOnClickListener{
                val editor = sharedPreferencesEdits.edit()
                editor.putInt("edited", 1)
                editor.apply()
                ReplyingTo.visibility = View.GONE
                iconCrossView.visibility = View.GONE
                commentEditTextView.setText(null)
                ReplyingTo.text = null
                isAnswerPostId = null
            }
        }


    }


    private fun getMessageDetails(postId: Int) {
        ApiManager.apiService.getMessage(postId).enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.isSuccessful) {
                    val comment = response.body()
                    comment?.let {
                        commentEditTextView.setText(it.message)
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

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na requisição para obter detalhes do comentário: ${t.message}")
                Toast.makeText(
                    context,
                    getString(R.string.request_comment_details_failure),
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }



    private fun deleteMessage(postId: Int, userId: Int) {
        ApiManager.apiService.deleteMessage(postId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        getString(R.string.comment_delete_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Redirecionar para a tela de login após deletar a conta
                    more_options_layout.visibility = View.GONE
                    getMessagesUsers(userId, userTwoId)

                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.delete_comment_failure),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun deleteMessageWithConfirmation(userId: Int) {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.delete_comment, null)

        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.confirm))
            setView(view)
            setPositiveButton(getString(R.string.confirm_yes)) { dialog, which ->
                deleteMessage(selectedPostId, userId)
            }
            setNegativeButton(getString(R.string.confirm_no)) { dialog, which ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }



    companion object {
        private const val ARG_USER_TWO_ID = "userTwoId"
        //private const val ARG_PRIMARY_COLOR = "primaryColor"
        //private const val ARG_SECONDARY_COLOR = "secondaryColor"

        @JvmStatic
        //fun newInstance(userTwoId: Int, primaryColor: Int, secondaryColor: Int) =
        fun newInstance(userTwoId: Int) =
            UserMessagesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_TWO_ID, userTwoId)
                    //putInt(ARG_PRIMARY_COLOR, primaryColor)
                    //putInt(ARG_SECONDARY_COLOR, secondaryColor)
                }
            }
    }
}
