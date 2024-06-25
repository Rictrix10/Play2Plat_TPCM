package com.example.play2plat_tpcm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.Password
import com.example.play2plat_tpcm.api.User
import com.example.play2plat_tpcm.room.vm.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private const val ARG_USER_ID = "user_id"

class Profile_Fragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var editIconImageView: ImageView
    private lateinit var containerLayout: ConstraintLayout
    private lateinit var backIconImageView: ImageView
    private lateinit var logoutButton: Button
    private lateinit var deleteButton: Button
    private var userId: Int = 0
    private var currentUserId: Int = 0
    private var user: User? = null
    private var userPassword: String = ""
    private val navigationViewModel: FragmentNavigationViewModel by activityViewModels()

    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("user_id", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameTextView = view.findViewById(R.id.username)
        profileImageView = view.findViewById(R.id.profile_picture)
        editIconImageView = view.findViewById(R.id.edit_icon)
        containerLayout = view.findViewById(R.id.container_layout)
        backIconImageView = view.findViewById(R.id.back_icon)
        logoutButton = view.findViewById(R.id.logout_button)
        deleteButton = view.findViewById(R.id.delete_button)

        // Configurar a visibilidade do ícone de edição
        if (userId == currentUserId) {
            editIconImageView.visibility = View.VISIBLE
            editIconImageView.setOnClickListener {
                redirectToEditProfile()
            }
            backIconImageView.visibility = View.GONE
        } else {
            editIconImageView.visibility = View.GONE
            backIconImageView.visibility = View.VISIBLE
            backIconImageView.setOnClickListener {
                val fragmentManager = requireActivity().supportFragmentManager

                val currentFragment = fragmentManager.primaryNavigationFragment
                if (currentFragment != null) {
                    navigationViewModel.removeFromStack(currentFragment)
                }

                requireActivity().onBackPressed()
            }
        }

        // Configurar ação do ícone de voltar
        backIconImageView.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val otherUser = if (userId == currentUserId) {
            false
        } else {
            true
        }

        // Verifique se o estado do fragmento já foi salvo antes de realizar a transação
        if (!requireActivity().supportFragmentManager.isStateSaved()) {
            val fragmentFavorite = Games_List_Horizontal_Fragment.newInstance("Favorite", "Favorite", 0, otherUser)
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentFavorite)
                .commit()

            val fragmentPlaying = Games_List_Horizontal_Fragment.newInstance("Playing", "Playing", 0, otherUser)
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container2, fragmentPlaying)
                .commit()
        } else {
            Log.d("Profile_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }

        if (isNetworkAvailable()) {
            Log.d("Profile_Fragment", "Vamos verificar se há atualizações pendentes...")
            checkAndUpdateUser { updateSuccessful ->
                    loadUserDataFromApi()
            }
        } else {
            loadUserDataFromRoom()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        deleteButton.setOnClickListener {
            deleteAccountWithConfirmation()
        }
    }

    private fun uploadAvatar(avatarFilePath: String, callback: (String?) -> Unit) {
        val file = File(avatarFilePath)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("avatar", file.name, requestFile)

        ApiManager.apiService.uploadImage(imagePart)
            .enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.string()
                    imageUrl?.let {
                        val pattern = Regex("\"url\":\"(\\S+)\"")
                        val matchResult = pattern.find(it)

                        matchResult?.let { result ->
                            val avatarUrl = result.groupValues[1]
                            //update
                        }
                    }
                } else {
                    Log.e("Profile_Fragment", "Failed to upload avatar: ${response.code()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Profile_Fragment", "Avatar upload request failed: ${t.message}")
                callback(null)
            }
        })
    }

    private fun checkAndUpdateUser(callback: (Boolean) -> Unit) {
        Log.d("Profile_Fragment", "Vamos atualizar os dados pela api...")
        val sharedPreferences = requireContext().getSharedPreferences("update_user", Context.MODE_PRIVATE)
        val sharedPreferencesUserData = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userIdToUpdate = sharedPreferences.getInt("id", 0)
        val userTypeId = sharedPreferencesUserData.getInt("user_type_id", 2)

        if (userIdToUpdate == currentUserId) {
            val email = sharedPreferences.getString("email", "")
            val username = sharedPreferences.getString("username", "")
            val password = sharedPreferences.getString("password", "")
            var avatar = sharedPreferences.getString("avatar", "")
            val platforms: List<String>? = null // Provide actual value if needed
            val imageState = sharedPreferences.getString("imageState", "")
            val netState = sharedPreferences.getString("netState", "")

            // Função para obter o avatar atual do usuário
            fun getCurrentUserAvatar(callback: (String?) -> Unit) {
                ApiManager.apiService.getUserById(currentUserId).enqueue(object : retrofit2.Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            callback(user?.avatar)
                        } else {
                            Log.e("Profile_Fragment", "Erro ao obter o usuário: ${response.message()}")
                            callback(null)
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("Profile_Fragment", "Falha na requisição para obter o usuário: ${t.message}")
                        callback(null)
                    }
                })
            }

            fun updateUser(avatar: String?) {
                val userUpdate = User(currentUserId, username ?: "", email ?: "", password ?: "", avatar ?: "", userTypeId, platforms)
                ApiManager.apiService.updateUser(currentUserId, userUpdate).enqueue(object : retrofit2.Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            // Clear update data from SharedPreferences after successful update
                            clearUpdateUserData()
                            callback(true)
                        } else {
                            Log.e("Profile_Fragment", "Failed to update user data via API: ${response.code()}")
                            callback(false)
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("Profile_Fragment", "API request failed: ${t.message}")
                        callback(false)
                    }
                })


            }

            // Verifica os estados de imageState e netState
            if (imageState == "NO" && netState == "NO") {
                getCurrentUserAvatar { currentAvatar ->
                    updateUser(currentAvatar)
                }
            }
            if (imageState == "YES" && netState == "YES"){updateUser(avatar)}
            if (imageState == "NO" && netState == "YES"){updateUser(avatar)}
            if (imageState == "YES" && netState == "NO"){
                Log.d("Profile", "YES IMAGE NO INTERNET")
                Log.d("", "${avatar}")
                val file = File(avatar)
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                Log.d("Profile_Fragment", "Uploading image...")
                ApiManager.apiService.uploadImage(imagePart)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val imageUrl = response.body()?.string()
                                Log.d("", "URL: ${imageUrl}")
                                imageUrl?.let {
                                    val pattern = Regex("\"url\":\"(\\S+)\"")
                                    val matchResult = pattern.find(it)

                                    matchResult?.let { result ->
                                        val avatarUrl = result.groupValues[1]
                                        Log.d("", "URL: ${avatarUrl}")
                                        updateUser(avatarUrl)
                                    }
                                }
                            } else {
                                Log.e("Profile_Fragment", "Failed to upload avatar: ${response.code()}")
                                callback(false)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("Profile_Fragment", "Avatar upload request failed: ${t.message}")
                            callback(false)
                        }
                    })
            }
            /*
            else {
                updateUser(avatar)
            }

             */
        } else {
            callback(false)
        }
    }

    private fun loadUserDataFromApi() {
        ApiManager.apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        usernameTextView.text = user.username
                        loadImage(user.avatar)

                        val platforms = user.platforms
                        val canEditPlatforms = userId == currentUserId
                        if (platforms != null) {
                            val platformsFragment = Platforms_List_Fragment.newInstance(platforms, canEditPlatforms, true, currentUserId, false)
                            childFragmentManager.beginTransaction().replace(R.id.platforms_fragment, platformsFragment).commit()
                        }
                    } else {
                        Log.e("Profile_Fragment", "API response did not return user data.")
                    }
                } else {
                    Log.e("Profile_Fragment", "API response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("Profile_Fragment", "Request error: ${t.message}")
                loadUserDataFromRoom()
            }
        })
    }



    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun loadUserDataFromRoom() {
        userViewModel.getUserByIdUser(currentUserId).observe(viewLifecycleOwner) { roomUser ->
            if (roomUser != null) {
                usernameTextView.text = roomUser.username
                if (roomUser.avatar != null) {
                    loadImage(roomUser.avatar)
                } else {
                    profileImageView.setImageResource(R.drawable.icon_noimageuser)
                }
            } else {
                Log.e("Profile_Fragment", "User not found in Room database")
            }
        }
    }

    private fun logout() {
        val title = getString(R.string.confirm_title)
        val message = getString(R.string.confirm_message)
        val yesText = getString(R.string.confirm_yes)
        val noText = getString(R.string.confirm_no)

        // Criar um TextView para o título
        val titleTextView = TextView(requireContext()).apply {
            text = title
            setPadding(20, 20, 20, 20)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        // Criar um TextView para a mensagem
        val messageTextView = TextView(requireContext()).apply {
            text = message
            setPadding(20, 20, 20, 20)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)  // Definir tamanho de texto conforme necessário
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))  // Definir cor do texto para branco
            gravity = Gravity.CENTER
        }

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setCustomTitle(titleTextView)
            setView(messageTextView)  // Definir o TextView customizado para a mensagem
            setPositiveButton(yesText) { dialog, which ->
                // Eliminar dados guardados no SharedPreferences
                clearSharedPreferences()

                // Redirecionar para a LoginActivity
                val intent = Intent(activity, LoginPage::class.java)
                startActivity(intent)
                activity?.finish()
            }
            setNegativeButton(noText) { dialog, which ->
                // Fecha o diálogo sem fazer logout
                dialog.dismiss()
            }
        }.create()

        // Definir propriedades do AlertDialog
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.button_bd_3)
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }

        // Mostrar o AlertDialog
        alertDialog.show()
    }


    private fun deleteAccountWithConfirmation() {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_confirm_delete, null)
        val passwordEditText = view.findViewById<EditText>(R.id.password_edit_text)

        val title = getString(R.string.confirm_title)
        val message = getString(R.string.confirm_delete_message)
        val yesText = getString(R.string.confirm_yes)
        val noText = getString(R.string.confirm_no)
        val passwordIncorrect = getString(R.string.password_incorrect)
        val passwordEmpty = getString(R.string.password_empty)

        val titleTextView = TextView(requireContext()).apply {
            text = title
            setPadding(20, 20, 20, 20)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setCustomTitle(titleTextView)
            setView(view)
            setPositiveButton(yesText) { dialog, which ->
                val password = passwordEditText.text.toString()

                if (password.isNotEmpty()) {
                    verifyPassword(currentUserId, password, object : PasswordVerificationCallback {
                        override fun onVerificationSuccess() {
                            deleteAccount(userId)
                        }

                        override fun onVerificationFailure() {
                            Toast.makeText(context, passwordIncorrect, Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(context, passwordEmpty, Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton(noText) { dialog, which ->
                dialog.dismiss()
            }
        }.create()

        // Configura as propriedades do AlertDialog
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.button_bd_3)
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }

        // Mostra o AlertDialog
        alertDialog.show()
    }





    private fun verifyPassword(userId: Int, password: String, callback: PasswordVerificationCallback) {
        val inputPass = Password(password)
        ApiManager.apiService.verifyPassword(userId, inputPass).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback.onVerificationSuccess()
                } else if (response.code() == 400) {
                    callback.onVerificationFailure()
                } else {
                    Toast.makeText(context, "Erro desconhecido: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteAccount(userId: Int) {
        ApiManager.apiService.deleteAccount(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Conta eliminada com sucesso", Toast.LENGTH_SHORT).show()
                    // Redirecionar para a tela de login após deletar a conta

                    userViewModel.getUserByIdUser(userId).observe(viewLifecycleOwner) { user ->
                        if (user != null) {
                            userViewModel.deleteUser(user)
                        } else {
                            Log.e("Profile_Fragment", "User not found in Room database")
                        }
                    }


                    clearSharedPreferences()

                    val intent = Intent(activity, LoginPage::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    Toast.makeText(context, "Falha ao eliminar a conta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearSharedPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    interface PasswordVerificationCallback {
        fun onVerificationSuccess()
        fun onVerificationFailure()
    }

    private fun loadImage(avatarUrl: String?) {
        if (!avatarUrl.isNullOrEmpty()) {
            if (avatarUrl.startsWith("http") || avatarUrl.startsWith("https")) {
                Picasso.get().load(avatarUrl).into(profileImageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
                        applyGradientFromBitmap(bitmap)
                    }

                    override fun onError(e: Exception?) {
                        Log.e("Profile_Fragment", "Error loading image: ${e?.message}")
                    }
                })
            } else {
                // Handling local file path
                Picasso.get().load(File(avatarUrl)).into(profileImageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
                        applyGradientFromBitmap(bitmap)
                    }

                    override fun onError(e: Exception?) {
                        Log.e("Profile_Fragment", "Error loading image: ${e?.message}")
                    }
                })
            }
        } else {
            profileImageView.setImageResource(R.drawable.icon_noimageuser)
            val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
            applyGradientFromBitmap(bitmap)
        }
    }

    private fun applyGradientFromBitmap(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            palette?.let {
                var dominantColor = it.dominantSwatch?.rgb ?: Color.GRAY
                var vibrantColor = it.vibrantSwatch?.rgb ?: Color.DKGRAY

                // Verificar se as cores extraídas são muito claras
                val isDominantColorTooLight = ColorUtils.calculateLuminance(dominantColor) > 0.8
                val isVibrantColorTooLight = ColorUtils.calculateLuminance(vibrantColor) > 0.8

                if (isDominantColorTooLight) {
                    dominantColor = Color.GRAY
                }
                if (isVibrantColorTooLight) {
                    vibrantColor = Color.DKGRAY
                }

                // Se as cores vibrantes e dominantes forem iguais, ajustar a cor vibrante
                if (vibrantColor == dominantColor) {
                    vibrantColor = it.lightVibrantSwatch?.rgb
                        ?: it.darkVibrantSwatch?.rgb
                                ?: it.mutedSwatch?.rgb
                                ?: it.lightMutedSwatch?.rgb
                                ?: it.darkMutedSwatch?.rgb
                                ?: Color.DKGRAY
                }

                val colors = intArrayOf(dominantColor, vibrantColor)
                val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                containerLayout.background = gradientDrawable
            } ?: run {
                val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.GRAY, Color.DKGRAY))
                containerLayout.background = gradientDrawable
            }
        }
    }

    private fun redirectToEditProfile() {

        val editProfileFragment = Edit_Profile_Fragment()
        navigationViewModel.addToStack(editProfileFragment)
        if (!requireActivity().supportFragmentManager.isStateSaved()) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout, editProfileFragment)
                .addToBackStack(null)
                .commit()
        } else {
            Log.d("Profile_Fragment", "O estado da instância já foi salvo, transação de fragmento adiada.")
        }
    }

    /*
    private fun checkAndUpdateUser() {
        Log.d("Profile_Fragment", "Vamos atualizar os dados pela api...")
        val sharedPreferences = requireContext().getSharedPreferences("update_user", Context.MODE_PRIVATE)
        val sharedPreferencesUserData = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userIdToUpdate = sharedPreferences.getInt("id", 0)
        val userTypeId = sharedPreferencesUserData.getInt("user_type_id", 2)

        if (userIdToUpdate == currentUserId) {
            val email = sharedPreferences.getString("email", "")
            val username = sharedPreferences.getString("username", "")
            val password = sharedPreferences.getString("password", "")
            var avatar = sharedPreferences.getString("avatar", "")
            val platforms: List<String>? = null // Provide actual value if needed
            val imageState = sharedPreferences.getString("imageState", "")
            val netState = sharedPreferences.getString("netState", "")
            // Função para obter o avatar atual do usuário
            fun getCurrentUserAvatar(callback: (String?) -> Unit) {
                ApiManager.apiService.getUserById(currentUserId).enqueue(object : retrofit2.Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            callback(user?.avatar)
                        } else {
                            Log.e("Profile_Fragment", "Erro ao obter o usuário: ${response.message()}")
                            callback(null)
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("Profile_Fragment", "Falha na requisição para obter o usuário: ${t.message}")
                        callback(null)
                    }
                })
            }

            // Função para atualizar o usuário
            fun updateUser(avatar: String?) {
                val userUpdate = User(currentUserId, username ?: "", email ?: "", password ?: "", avatar ?: "", userTypeId, platforms)
                ApiManager.apiService.updateUser(currentUserId, userUpdate).enqueue(object : retrofit2.Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            // Clear update data from SharedPreferences after successful update
                            clearUpdateUserData()
                        } else {
                            Log.e("Profile_Fragment", "Failed to update user data via API: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("Profile_Fragment", "API request failed: ${t.message}")
                    }
                })
            }

            // Verifica os estados de imageState e netState
            if (imageState == "NO" && netState == "NO") {
                getCurrentUserAvatar { currentAvatar ->
                    updateUser(currentAvatar)
                }
            } else {
                updateUser(avatar)
            }
        }
    }


     */

    /*
    private fun checkAndUpdateUser() {
        Log.d("Profile_Fragment", "Vamos atualizar os dados pela api...")
        val sharedPreferences = requireContext().getSharedPreferences("update_user", Context.MODE_PRIVATE)
        val sharedPreferencesUserData = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userIdToUpdate = sharedPreferences.getInt("id", 0)
        val userTypeId = sharedPreferencesUserData.getInt("user_type_id", 2)
        if (userIdToUpdate == currentUserId) {
            val email = sharedPreferences.getString("email", "")
            val username = sharedPreferences.getString("username", "")
            val password = sharedPreferences.getString("password", "")
            val avatar = sharedPreferences.getString("avatar", "")
            val platforms: List<String>? = null // Provide actual value if needed

            // Handle nullable values and provide defaults if necessary
            val userUpdate = User(currentUserId, username ?: "", email ?: "", password ?: "", avatar ?: "", userTypeId, platforms)
            ApiManager.apiService.updateUser(currentUserId, userUpdate).enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // Clear update data from SharedPreferences after successful update
                        clearUpdateUserData()
                    } else {
                        Log.e("Profile_Fragment", "Failed to update user data via API: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("Profile_Fragment", "API request failed: ${t.message}")
                }
            })
        }
    }
    */

    private fun clearUpdateUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("update_user", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int) =
            Profile_Fragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }
}

