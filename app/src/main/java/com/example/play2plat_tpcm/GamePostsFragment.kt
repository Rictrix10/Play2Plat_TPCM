package com.example.play2plat_tpcm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Comment
import com.example.play2plat_tpcm.api.GameCommentsResponse
import com.example.play2plat_tpcm.api.GeoNamesResponse
import com.example.play2plat_tpcm.api.GeoNamesServiceBuilder.service
import com.example.play2plat_tpcm.api.LocationInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import java.io.IOException
import java.util.Locale

class GamePostsFragment : Fragment(), GamePostsAdapter.OnProfilePictureClickListener, GamePostsAdapter.OnReplyClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameTextView: TextView
    private lateinit var ReplyingTo: TextView
    private lateinit var commentEditTextView: EditText
    private lateinit var imageImageView: ImageView
    private lateinit var sendImageView: ImageView
    private lateinit var container_layout: ConstraintLayout

    private var gameId: Int = 0
    private var gameName: String? = null
    private var primaryColor: Int = 0
    private var secondaryColor: Int = 0
    private var isAnswerPostId: Int = 0

    private var selectedImageUri: Uri? = null

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
            gameName = it.getString(ARG_GAME_NAME)
            primaryColor = it.getInt(ARG_PRIMARY_COLOR)
            secondaryColor = it.getInt(ARG_SECONDARY_COLOR)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_posts, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        gameTextView = view.findViewById(R.id.game_title)
        commentEditTextView = view.findViewById(R.id.comment)
        imageImageView = view.findViewById(R.id.image_icon)
        sendImageView = view.findViewById(R.id.send_icon)
        container_layout = view.findViewById(R.id.container)
        ReplyingTo = view.findViewById(R.id.replying_to_text)
        gameTextView.text = gameName

        val colors = intArrayOf(primaryColor, secondaryColor)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        container_layout.background = gradientDrawable

        imageImageView.setOnClickListener {
            selectVisualMedia()
        }

        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        // Chama a API para obter os posts do jogo
        getGamePosts(gameId, userId)

        sendImageView.setOnClickListener {
            getLocationAndPostComment(userId, gameId)
        }

        return view
    }

    private fun getGamePosts(gameId: Int, userId: Int) {
        ApiManager.apiService.getPosts(userId, gameId).enqueue(object : Callback<List<GameCommentsResponse>> {
            override fun onResponse(
                call: Call<List<GameCommentsResponse>>,
                response: Response<List<GameCommentsResponse>>
            ) {
                if (response.isSuccessful) {
                    val gamePosts = response.body()
                    if (gamePosts != null && gamePosts.isNotEmpty()) {
                        getLocationName(gamePosts[0].latitude, gamePosts[0].longitude) { locationInfo ->
                            recyclerView.adapter = GamePostsAdapter(gamePosts, this@GamePostsFragment, this@GamePostsFragment)
                        }
                    } else {
                        Log.e("GamePostsFragment", "A lista de posts do jogo está vazia ou nula.")
                    }
                } else {
                    Log.e("GamePostsFragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<GameCommentsResponse>>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }


    private fun getLocationAndPostComment(userId: Int, gameId: Int) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude

                getLocationName(latitude, longitude) { locationInfo ->  // Passando o lambda
                    val comments = commentEditTextView.text.toString()
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
                                            postComment(comments, coverImageUrl, userId, gameId, latitude, longitude, locationInfo)
                                        }
                                    }
                                } else {
                                    Log.e("AddNewComment", "Erro no upload: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                            }
                        })
                    } else {
                        postComment(comments, null, userId, gameId, latitude, longitude, locationInfo)
                    }
                }
            } else {
                Toast.makeText(context, "Could not get location. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getLocationName(
        latitude: Double,
        longitude: Double,
        onResult: (LocationInfo) -> Unit
    ) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var locationInfo = LocationInfo(null, null, null, null, null, null)

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val cityName = address.locality
                val countryName = address.countryName
                val countryCode = address.countryCode
                val postalCode = address.postalCode

                locationInfo = LocationInfo(cityName, countryName, countryCode, postalCode, null, null)

                if (postalCode != null && countryCode != null) {
                    getPostalCodeInfo(postalCode, countryCode, "rictrix") { updatedLocationInfo ->
                        val finalLocationInfo = locationInfo.copy(
                            adminName1 = updatedLocationInfo.adminName1,
                            adminName2 = updatedLocationInfo.adminName2
                        )
                        onResult(finalLocationInfo)
                    }
                } else {
                    onResult(locationInfo)
                }
            } else {
                onResult(locationInfo)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            onResult(locationInfo)
        }
    }




    private fun postComment(comments: String, imageUrl: String?, userId: Int, gameId: Int, latitude: Double, longitude: Double, locationInfo: LocationInfo) {
        val locationName = "${locationInfo.countryName}, ${locationInfo.adminName1}, ${locationInfo.adminName2}"
        val newComment = Comment(
            comments = comments,
            image = imageUrl,
            isAnswer = if (isAnswerPostId != 0) isAnswerPostId else null,
            userId = userId,
            gameId = gameId,
            latitude = latitude,
            longitude = longitude,
            location = locationName
        )

        ApiManager.apiService.addComment(newComment)
            .enqueue(object : Callback<Comment> {
                override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                    if (response.isSuccessful) {
                        val postComment = response.body()
                        Toast.makeText(context, "Comment posted successfully!", Toast.LENGTH_SHORT).show()
                        getGamePosts(gameId, userId)  // Refresh the posts after posting a new comment
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

    private fun getPostalCodeInfo(
        postalCode: String,
        countryCode: String,
        username: String,
        onResult: (LocationInfo) -> Unit
    ) {
        val call = service.getLocationInfo(postalCode, countryCode, username)
        call.enqueue(object : Callback<GeoNamesResponse> {
            override fun onResponse(call: Call<GeoNamesResponse>, response: Response<GeoNamesResponse>) {
                if (response.isSuccessful) {
                    val locationInfoResponse = response.body()
                    if (locationInfoResponse != null && locationInfoResponse.postalCodes.isNotEmpty()) {
                        val firstResult = locationInfoResponse.postalCodes[0]
                        val locationInfo = LocationInfo(
                            null, null, null, postalCode,
                            firstResult.adminName1, firstResult.adminName2
                        )
                        onResult(locationInfo)
                    } else {
                        Log.e("PostalCodeInfo", "No results found for postal code: $postalCode")
                        onResult(LocationInfo(null, null, null, postalCode, null, null))
                    }
                } else {
                    Log.e("PostalCodeInfo", "Error: ${response.message()}")
                    onResult(LocationInfo(null, null, null, postalCode, null, null))
                }
            }

            override fun onFailure(call: Call<GeoNamesResponse>, t: Throwable) {
                Log.e("PostalCodeInfo", "Failure: ${t.message}")
                onResult(LocationInfo(null, null, null, postalCode, null, null))
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
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onReplyClick(postId: Int, username: String) {
        isAnswerPostId = postId
        ReplyingTo.visibility = View.VISIBLE
        ReplyingTo.text = SpannableStringBuilder().append("Replying to ").append(username)
    }

    companion object {
        private const val ARG_GAME_ID = "gameId"
        private const val ARG_GAME_NAME = "gameName"
        private const val ARG_PRIMARY_COLOR = "primaryColor"
        private const val ARG_SECONDARY_COLOR = "secondaryColor"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

        @JvmStatic
        fun newInstance(gameId: Int, gameName: String, primaryColor: Int, secondaryColor: Int) =
            GamePostsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                    putInt(ARG_PRIMARY_COLOR, primaryColor)
                    putInt(ARG_SECONDARY_COLOR, secondaryColor)
                }
            }
    }
}
