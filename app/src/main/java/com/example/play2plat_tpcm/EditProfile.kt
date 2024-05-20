package com.example.play2plat_tpcm

import Profile_Fragment
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditProfile : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var profileImageView: ImageView
    private lateinit var usernameEditTextView: EditText
    private lateinit var emailEditTextView: EditText
    private lateinit var newPasswordEditTextView: EditText
    private lateinit var confirmPasswordEditTextView: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var saveButton: Button
    private lateinit var selectImageView: ImageView
    private lateinit var backImageView: ImageView


    private var selectedImageUri: Uri? = null

    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Log.d("EditProfile", "Selected image URI: $selectedImageUri")
            profileImageView.setImageURI(selectedImageUri)
        } else {
            Log.d("EditProfile", "No image URI received")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profile_picture)
        usernameEditTextView = view.findViewById(R.id.username)
        emailEditTextView = view.findViewById(R.id.email)
        newPasswordEditTextView = view.findViewById(R.id.new_password)
        confirmPasswordEditTextView = view.findViewById(R.id.confirm_password)
        changePasswordButton = view.findViewById(R.id.change_password)
        saveButton = view.findViewById(R.id.save)
        selectImageView = view.findViewById(R.id.select_picture)
        backImageView = view.findViewById(R.id.back_icon)

        selectImageView.setOnClickListener {
            Log.d("EditProfile", "Select image button clicked")
            selectVisualMedia()
        }

        backImageView.setOnClickListener {
            Log.d("EditProfile", "Back to Profile")
            redirectToProfile()
        }

        changePasswordButton.setOnClickListener {
            togglePasswordFieldsVisibility()
        }

        saveButton.setOnClickListener {
            uploadImageAndSaveProfile()
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)

        lifecycleScope.launch {
            loadUserProfile(userId)
        }
    }

    private fun selectVisualMedia() {
        pickVisualMediaLauncher.launch("image/*")
    }

    private fun togglePasswordFieldsVisibility() {
        val visibility = if (newPasswordEditTextView.visibility == View.GONE) {
            View.VISIBLE
        } else {
            View.GONE
        }
        newPasswordEditTextView.visibility = visibility
        confirmPasswordEditTextView.visibility = visibility
    }

    private suspend fun loadUserProfile(userId: Int) {
        ApiManager.apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        usernameEditTextView.text = Editable.Factory.getInstance().newEditable(user.username)
                        emailEditTextView.text = Editable.Factory.getInstance().newEditable(user.email)
                        loadImage(user.avatar)

                        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("avatar_url", user.avatar)
                            apply()
                        }
                    } else {
                        Log.e("EditProfile", "API response did not return user data.")
                    }
                } else {
                    Log.e("EditProfile", "API response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("EditProfile", "Request error: ${t.message}")
            }
        })
    }


    private fun loadImage(avatarUrl: String?) {
        if (!avatarUrl.isNullOrEmpty()) {
            Picasso.get().load(avatarUrl).into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.noimageuser)
        }
    }

    private fun redirectToProfile() {
        val profileFragment = Profile_Fragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.layout, profileFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun uploadImageAndSaveProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val currentAvatarUrl = sharedPreferences.getString("avatar_url", null)

        if (selectedImageUri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
            val file = bitmapToFile(requireContext(), bitmap)

            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("file", "profile_image.jpg", requestFile)

            val call = ApiManager.apiService.uploadImage(imagePart)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.string()
                        imageUrl?.let {
                            val pattern = Regex("\"url\":\"(\\S+)\"")
                            val matchResult = pattern.find(it)

                            matchResult?.let { result ->
                                val avatarUrl = result.groupValues[1]

                                updateUserProfile(avatarUrl)
                            }
                        }
                    } else {
                        Log.e("EditProfile", "Erro no upload: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("EditProfile", "Falha na requisição: ${t.message}")
                }
            })
        } else {
            updateUserProfile(currentAvatarUrl)
        }
    }


    private fun updateUserProfile(avatarUrl: String?) {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", 0)
        val userTypeId = sharedPreferences.getInt("user_type_id", 2)

        val updatedUsername = usernameEditTextView.text.toString()
        val updatedEmail = emailEditTextView.text.toString()
        val newPassword = newPasswordEditTextView.text.toString()
        val confirmPassword = confirmPasswordEditTextView.text.toString()

        if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
            // Show an error message that passwords do not match
            return
        }

        val updatedUser = User(
            id = userId,
            username = updatedUsername,
            email = updatedEmail,
            password = if (newPassword.isNotEmpty()) newPassword else "",
            avatar = avatarUrl ?: "",  // Assign avatar URL if available
            userTypeId = userTypeId  // Assuming a default userTypeId, update as needed
        )

        ApiManager.apiService.updateUser(userId, updatedUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.d("EditProfile", "User profile updated successfully")
                    redirectToProfile()
                } else {
                    Log.e("EditProfile", "API response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("EditProfile", "Request error: ${t.message}")
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
