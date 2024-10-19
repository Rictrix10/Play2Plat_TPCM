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
import com.example.play2plat_tpcm.api.PatchComment
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
import java.util.Locale

class UserMessagesFragment : Fragment(), UserMessagesAdapter.OnProfilePictureClickListener, UserMessagesAdapter.OnReplyClickListener, UserMessagesAdapter.onMoreOptionsClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameTextView: TextView
    private lateinit var ReplyingTo: TextView
    private lateinit var commentEditTextView: EditText
    private lateinit var imageImageView: ImageView
    private lateinit var sendImageView: ImageView
    private lateinit var seeMapButton: LinearLayout
    private lateinit var iconCrossView: ImageView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var container_layout: ConstraintLayout
    private lateinit var more_options_layout: ConstraintLayout
    private lateinit var commentsTextView: TextView

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

    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imageImageView.setImageURI(selectedImageUri)
            Log.d("EditProfile", "Selected image URI: $selectedImageUri")
        } else {
            Log.d("EditProfile", "No image URI received")
        }
    }

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Permissão concedida

        } else {
            // Permissão negada
            //Toast.makeText(requireContext(), "Permissão de localização negada", Toast.LENGTH_SHORT).show()
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
        more_options_layout = view.findViewById(R.id.more_options_layout)
        ReplyingTo = view.findViewById(R.id.replying_to_text)
        gameTextView.text = gameName
        seeMapButton = view.findViewById(R.id.button_see_map)
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
            seeMapButton.visibility = View.VISIBLE
            val params = commentsTextView.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 28.dpToPx() // Defina a margem superior desejada
            commentsTextView.layoutParams = params
        } else {
            seeMapButton.visibility = View.GONE
            val params = commentsTextView.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 0 // Remove a margem superior
            commentsTextView.layoutParams = params
        }

        // Chama a API para obter os posts do jogo
        getMessagesUsers(userId, )


        seeMapButton.setOnClickListener(){
            if (isNetworkAvailable()) {
                redirectToMapsFragment()
            }
            else{
                redirectToNoConnectionFragment()
            }
        }


        sendImageView.setOnClickListener {
            if (isNetworkAvailable()) {
                getLocationAndPostComment(userId, gameId)
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
                deleteCommentWithConfirmation(userId)
            }
            else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.delete_comment_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (!checkLocationPermissions()) {
            // Permissões não concedidas, solicitar permissão
            requestLocationPermissions()
        } else {
            // Permissões já concedidas, continuar com o fluxo normal
            Log.d("GamePostsFragment", "Permissões de localização já concedidas")
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

    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }


    private fun checkLocationPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissões concedidas, continuar com o fluxo do aplicativo
                Log.d("GamePostsFragment", "Permissões de localização concedidas pelo usuário")
            } else {
                // Permissões negadas, mas vamos prosseguir mesmo assim
                Log.d("GamePostsFragment", "Permissões de localização não concedidas pelo usuário")
            }
        }
    }


    private fun getMessagesUsers(userOneId: Int, userTwoId: Int) {
        ApiManager.apiService.getMessagesUsers(userOneId, userTwoId).enqueue(object : Callback<List<Message>> {
            override fun onResponse(
                call: Call<List<Message>>,
                response: Response<List<Message>>
            ) {
                if (response.isSuccessful) {
                    val gamePosts = response.body()
                    if (gamePosts != null && gamePosts.isNotEmpty()) {
                        getLocationName(gamePosts[0].latitude, gamePosts[0].longitude) { locationInfo ->
                            recyclerView.adapter = UserMessagesAdapter(gamePosts, this@UserMessagesFragment, this@UserMessagesFragment, this@UserMessagesFragment, false)
                        }
                    } else {
                        Log.e("GamePostsFragment", "A lista de posts do jogo está vazia ou nula.")
                        recyclerView.adapter = UserMessagesAdapter(emptyList(), this@UserMessagesFragment, this@UserMessagesFragment, this@UserMessagesFragment, false)
                    }
                } else {
                    recyclerView.adapter = UserMessagesAdapter(emptyList(), this@UserMessagesFragment, this@UserMessagesFragment, this@UserMessagesFragment, false)
                    Log.e("GamePostsFragment", "Erro na resposta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.e("GamePostsFragment", "Falha na chamada da API: ${t.message}")
            }
        })
    }


    private fun getLocationAndPostComment(userId: Int, gameId: Int) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { loc ->
                    latitude = loc.latitude
                    longitude = loc.longitude

                    getLocationName(latitude, longitude) { locationInfo ->
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
                                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                        Log.e("AddNewComment", "Erro no upload: $errorBody")
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
                            postComment(comments, null, userId, gameId, latitude, longitude, locationInfo)
                        }
                    }
                } ?: run {
                    postCommentWithNoLocation(userId, gameId)
                }
            }


        } else {
            //requestLocationPermissions()
            postCommentWithNoLocation(userId, gameId)
        }
    }


    private fun getLocationAndPatchComment(userId: Int, gameId: Int) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { loc ->
                    latitude = loc.latitude
                    longitude = loc.longitude

                    getLocationName(latitude, longitude) { locationInfo ->
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
                                                patchComment(comments, coverImageUrl, userId, gameId, latitude, longitude, locationInfo)
                                            }
                                        }
                                    } else {
                                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                        Log.e("AddNewComment", "Erro no upload: $errorBody")
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
                            patchComment(comments, null, userId, gameId, latitude, longitude, locationInfo)
                        }
                    }
                } ?: run {
                    patchCommentWithNoLocation(userId, gameId)
                }
            }

        } else {
            patchCommentWithNoLocation(userId, gameId)
        }
    }


    private fun postCommentWithNoLocation(userId: Int, gameId: Int) {
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
                                postComment(comments, coverImageUrl, userId, gameId, null, null, LocationInfo(null, null, null, null, null, null))
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
            postComment(comments, null, userId, gameId, null, null, LocationInfo(null, null, null, null, null, null))
        }
    }

    private fun patchCommentWithNoLocation(userId: Int, gameId: Int) {
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
                                patchComment(comments, coverImageUrl, userId, gameId, null, null, LocationInfo(null, null, null, null, null, null))
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
            patchComment(comments, null, userId, gameId, null, null, LocationInfo(null, null, null, null, null, null))
        }
    }



    private fun postComment(comments: String, imageUrl: String?, userId: Int, gameId: Int, latitude: Double?, longitude: Double?, locationInfo: LocationInfo) {
        val locationName = "${locationInfo.countryName}, ${locationInfo.adminName2}"
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

        if(comments.isEmpty()){
            Toast.makeText(
                context,
                getString(R.string.comment_empty_error),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        ApiManager.apiService.addComment(newComment)
            .enqueue(object : Callback<Comment> {
                override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                    if (response.isSuccessful) {
                        val postComment = response.body()
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
                        getGamePosts(gameId, userId)  // Refresh the posts after posting a new comment
                    } else {
                        Log.e("AddNewComment", "Error posting comment: ${response.message()}")
                        Toast.makeText(
                            context,
                            getString(R.string.error_posting_comment),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<Comment>, t: Throwable) {
                    Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                }
            })
    }

    private fun patchComment(comments: String, imageUrl: String?, userId: Int, gameId: Int, latitude: Double?, longitude: Double?, locationInfo: LocationInfo) {
        val locationName = "${locationInfo.countryName}, ${locationInfo.adminName2}"
        val newComment = PatchComment(
            comments = comments,
            userId = userId,
            gameId = gameId,
            latitude = latitude,
            longitude = longitude,
            location = locationName
        )

        if(commentEditTextView.getText().toString().trim().isEmpty()){
            Toast.makeText(
                context,
                getString(R.string.comment_empty_error),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        ApiManager.apiService.updateComment(selectedPostId, newComment)
            .enqueue(object : Callback<PatchComment> {
                override fun onResponse(call: Call<PatchComment>, response: Response<PatchComment>) {
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
                        getGamePosts(gameId, userId)  // Refresh the posts after posting a new comment
                    } else {
                        Log.e("AddNewComment", "Error updating comment: ${response.message()}")
                        Toast.makeText(
                            context,
                            getString(R.string.error_update_comment),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<PatchComment>, t: Throwable) {
                    Log.e("AddNewComment", "Erro na requisição: ${t.message}")
                }
            })
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
        navigationViewModel.addToStack(viewGameFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, viewGameFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun redirectToMapsFragment() {
        val mapsFragment = MapsFragment.newInstance(gameId, gameName!!, primaryColor, secondaryColor)
        navigationViewModel.addToStack(mapsFragment)
        if (!requireActivity().supportFragmentManager.isStateSaved()) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, mapsFragment)
                .addToBackStack(null)
                .commit()
        } else {
            Log.d("GamePostsFragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }
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
                getCommentDetails(postId)
                sendImageView.setOnClickListener(null)

                sendImageView.setOnClickListener {

                    if(sharedPreferencesEdits.getInt("edited", 0) == 0){
                        if(isNetworkAvailable()) {
                            getLocationAndPatchComment(userId, gameId)
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
                            getLocationAndPostComment(userId, gameId)
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
                    getLocationAndPostComment(userId, gameId)
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



    private fun deleteComment(postId: Int, userId: Int) {
        ApiManager.apiService.deleteComment(postId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        getString(R.string.comment_delete_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Redirecionar para a tela de login após deletar a conta
                    more_options_layout.visibility = View.GONE
                    getGamePosts(gameId, userId)

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


    private fun deleteCommentWithConfirmation(userId: Int) {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.delete_comment, null)

        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.confirm))
            setView(view)
            setPositiveButton(getString(R.string.confirm_yes)) { dialog, which ->
                deleteComment(selectedPostId, userId)
            }
            setNegativeButton(getString(R.string.confirm_no)) { dialog, which ->
                dialog.dismiss()
            }
            create()
            show()
        }
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
