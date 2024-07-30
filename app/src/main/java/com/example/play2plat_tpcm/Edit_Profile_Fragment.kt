package com.example.play2plat_tpcm

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.User
import com.example.play2plat_tpcm.room.vm.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Edit_Profile_Fragment : Fragment() {
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
    private lateinit var containerLayout: View
    private lateinit var ivToggleNewPasswordVisibility: ImageView
    private lateinit var ivToggleConfirmPasswordVisibility: ImageView
    private var isNewPasswordVisible: Boolean = false
    private var isConfirmPasswordVisible: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var customizeButton: Button
    private lateinit var customizeLayout: View
    private lateinit var PassText: TextView
    private lateinit var CFPassText: TextView

    private val userViewModel: UserViewModel by viewModels()
    private val navigationViewModel: FragmentNavigationViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    private val pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Log.d("EditProfile", "Selected image URI: $selectedImageUri")
            profileImageView.setImageURI(selectedImageUri)
            // Atualize o gradiente após selecionar uma nova imagem
            val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
            applyGradientFromBitmap(bitmap)
        } else {
            Log.d("EditProfile", "No image URI received")
        }
    }

    interface UserCallback {
        fun onUserLoaded(roomUser: com.example.play2plat_tpcm.room.entities.User)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialização das views
        profileImageView = view.findViewById(R.id.profile_picture)
        usernameEditTextView = view.findViewById(R.id.username)
        emailEditTextView = view.findViewById(R.id.email)
        newPasswordEditTextView = view.findViewById(R.id.new_password)
        confirmPasswordEditTextView = view.findViewById(R.id.confirm_password)
        changePasswordButton = view.findViewById(R.id.change_password)
        saveButton = view.findViewById(R.id.save)
        selectImageView = view.findViewById(R.id.select_picture)
        backImageView = view.findViewById(R.id.back_icon)
        containerLayout = view.findViewById(R.id.container_layout)
        customizeButton = view.findViewById(R.id.customize_button)
        customizeLayout = view.findViewById(R.id.radiogroup)
        PassText = view.findViewById(R.id.password_label)
        CFPassText = view.findViewById(R.id.cf_password_label)

        customizeButton.setOnClickListener {
            toggleCustomizeLayoutVisibility()
        }

        // Adicionando os ícones de visibilidade da senha
        ivToggleNewPasswordVisibility = view.findViewById(R.id.ivToggleNewPasswordVisibility)
        ivToggleConfirmPasswordVisibility = view.findViewById(R.id.ivToggleConfirmPasswordVisibility)

        // Listener para alternar a visibilidade da nova senha
        ivToggleNewPasswordVisibility.setOnClickListener {
            togglePasswordVisibility(newPasswordEditTextView, ivToggleNewPasswordVisibility, isNewPasswordVisible)
            isNewPasswordVisible = !isNewPasswordVisible
        }

        // Listener para alternar a visibilidade da confirmação da senha
        ivToggleConfirmPasswordVisibility.setOnClickListener {
            togglePasswordVisibility(confirmPasswordEditTextView, ivToggleConfirmPasswordVisibility, isConfirmPasswordVisible)
            isConfirmPasswordVisible = !isConfirmPasswordVisible
        }

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
            // Mostrar ícones de visibilidade de senha quando os campos de senha são mostrados
            if (newPasswordEditTextView.visibility == View.VISIBLE) {
                ivToggleNewPasswordVisibility.visibility = View.VISIBLE
                ivToggleConfirmPasswordVisibility.visibility = View.VISIBLE
            } else {
                ivToggleNewPasswordVisibility.visibility = View.GONE
                ivToggleConfirmPasswordVisibility.visibility = View.GONE
            }
        }

        saveButton.setOnClickListener {
            uploadImageAndSaveProfile()
        }

        val userId = sharedPreferences.getInt("user_id", 0)

        val themeRadioGroup = view.findViewById<RadioGroup>(R.id.theme_radio_group)
        val languageRadioGroup = view.findViewById<RadioGroup>(R.id.language_radio_group)

        // Set initial state based on saved preferences or system settings
        setupThemeRadioButtons(themeRadioGroup)
        setupLanguageRadioButtons(languageRadioGroup)

        themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.light_mode_button -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
                R.id.dark_mode_button -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
                R.id.system_mode_button -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.english_button -> setLocale(Locale.ENGLISH)
                R.id.portuguese_button -> setLocale(Locale("pt", "PT"))
                R.id.spanish_button -> setLocale(Locale("es", "ES"))
                R.id.french_button -> setLocale(Locale("fr", "FR"))
                R.id.german_button -> setLocale(Locale("de", "DE"))
                R.id.japanese_button -> setLocale(Locale("ja", "JA"))
                R.id.system_language_button -> setLocaleToSystem()
            }
        }

        lifecycleScope.launch {
            loadUserProfile(userId)
        }
    }

    private fun setupThemeRadioButtons(themeRadioGroup: RadioGroup) {
        val selectedTheme = sharedPreferences.getInt("selected_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val selectedThemeId = when (selectedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> R.id.light_mode_button
            AppCompatDelegate.MODE_NIGHT_YES -> R.id.dark_mode_button
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.id.system_mode_button
            else -> R.id.system_mode_button // Caso padrão para evitar problemas
        }
        themeRadioGroup.check(selectedThemeId)
    }

    private fun setupLanguageRadioButtons(languageRadioGroup: RadioGroup) {
        val selectedLocaleTag = sharedPreferences.getString("selected_locale", "system")
        val selectedLanguageId = when (selectedLocaleTag) {
            "en" -> R.id.english_button
            "pt" -> R.id.portuguese_button
            "es" -> R.id.spanish_button
            "fr" -> R.id.french_button
            "de" -> R.id.german_button
            "ja" -> R.id.japanese_button
            else -> R.id.system_language_button
        }
        languageRadioGroup.check(selectedLanguageId)
    }

    private fun setThemeMode(mode: Int) {
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
            sharedPreferences.edit().putInt("selected_theme", mode).apply()
            // Não chame requireActivity().recreate() aqui
        }
    }

    private fun setLocale(locale: Locale) {
        if (!isCurrentLocale(locale)) {
            sharedPreferences.edit().putString("selected_locale", locale.language).apply()
            updateLocale(requireContext(), locale)
            requireActivity().recreate() // Recrea a atividade apenas quando o idioma for alterado
        }
    }

    private fun setLocaleToSystem() {
        val systemLocale = Resources.getSystem().getConfiguration().locale;
        Log.d("SystemLocale", "System language: ${systemLocale.language}")

            sharedPreferences.edit().putString("selected_locale", "auto").apply()
            updateLocale(requireContext(), systemLocale)
            requireActivity().recreate() // Recrea a atividade apenas quando o idioma for alterado

    }


    private fun isCurrentLocale(locale: Locale): Boolean {
        val currentLocaleTag = sharedPreferences.getString("selected_locale", "system")
        return currentLocaleTag == locale.language
    }

    private fun updateLocale(context: Context, locale: Locale) {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        Locale.setDefault(locale)
    }

    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.icon_eye_off)
        } else {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.icon_eye)
        }
        editText.setSelection(editText.text.length)
    }

    private fun toggleCustomizeLayoutVisibility() {
        if (customizeLayout.visibility == View.VISIBLE) {
            customizeLayout.visibility = View.GONE
        } else {
            customizeLayout.visibility = View.VISIBLE
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


        ivToggleNewPasswordVisibility.visibility = visibility
        ivToggleConfirmPasswordVisibility.visibility = visibility

        CFPassText.visibility = visibility
        PassText.visibility = visibility
    }


    private suspend fun loadUserProfile(userId: Int) {
        if (isNetworkAvailable(requireContext())) {
        ApiManager.apiService.getUserById(userId).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        usernameEditTextView.text =
                            Editable.Factory.getInstance().newEditable(user.username)
                        emailEditTextView.text =
                            Editable.Factory.getInstance().newEditable(user.email)
                        loadImage(user.avatar)
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
    } else{
            loadUserDataFromRoom(userId)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            return networkInfo != null && networkInfo.isConnected
    }

    private fun loadUserDataFromRoom(userId: Int) {
        userViewModel.getUserByIdUser(userId).observe(viewLifecycleOwner) { roomUser ->
            if (roomUser != null) {
                usernameEditTextView.text = Editable.Factory.getInstance().newEditable(roomUser.username)
                emailEditTextView.text = Editable.Factory.getInstance().newEditable(roomUser.email)
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

    /*
    private fun loadImage(avatarUrl: String?) {
        if (!avatarUrl.isNullOrEmpty()) {
            Picasso.get().load(avatarUrl).into(profileImageView, object : Callback {
                override fun onSuccess() {
                    val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
                    applyGradientFromBitmap(bitmap)
                }

                override fun onError(e: Exception?) {
                    Log.e("EditProfile", "Error loading image: ${e?.message}")
                }
            })
        } else {
            profileImageView.setImageResource(R.drawable.noimageuser)
            val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
            applyGradientFromBitmap(bitmap)
        }
    }
     */

    private fun loadUserRoomData(userId: Int, callback: UserCallback) {
        userViewModel.getUserByIdUser(userId).observe(viewLifecycleOwner) { roomUser ->
            if (roomUser != null) {
                callback.onUserLoaded(roomUser)
            } else {
                Log.e("Profile_Fragment", "User not found in Room database")
            }
        }
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


    private fun redirectToProfile() {
        if (!requireActivity().supportFragmentManager.isStateSaved) {
            val fragmentManager = requireActivity().supportFragmentManager

            // Remove the current fragment from back stack
            fragmentManager.popBackStack()

            // Remove the current fragment from ViewModel's stack
            val currentFragment = fragmentManager.primaryNavigationFragment
            if (currentFragment != null) {
                navigationViewModel.removeFromStack(currentFragment)
            }

            // Se houver um fragmento anterior na pilha, mostra ele novamente
            if (fragmentManager.backStackEntryCount > 0) {
                val previousFragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
                val previousFragment = fragmentManager.findFragmentByTag(previousFragmentTag)

                if (previousFragment != null) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.layout, previousFragment)
                        .commit()
                }
            } else {
                // Se não houver fragmento anterior na pilha, volta para a tela anterior ou faz outra ação necessária
                requireActivity().onBackPressed()
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.activity_state_saved), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentUserAvatar(userId: Int, callback: (String?) -> Unit) {
        if (isNetworkAvailable(requireContext())) {
            ApiManager.apiService.getUserById(userId).enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        callback(user?.avatar)
                    } else {
                        Log.e("EditProfile", "Erro ao obter o usuário: ${response.message()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("EditProfile", "Falha na requisição para obter o usuário: ${t.message}")
                    callback(null)
                }
            })
        } else {
            userViewModel.getUserByIdUser(userId).observe(viewLifecycleOwner) { roomUser ->
                if (roomUser != null) {
                    callback(roomUser.avatar)
                } else {
                    Log.e("EditProfile", "User not found in Room database")
                    callback(null)
                }
            }
        }
    }


    private fun uploadImageAndSaveProfile() {
        val newPasswordEditTextView = view?.findViewById<EditText>(R.id.new_password)
        val confirmPasswordEditTextView = view?.findViewById<EditText>(R.id.confirm_password)

        // Obter os textos dos campos
        val newPassword = newPasswordEditTextView?.text.toString()
        val confirmPassword = confirmPasswordEditTextView?.text.toString()

        // Verificação se os campos de senha estão visíveis
        if (newPasswordEditTextView?.visibility == View.VISIBLE && confirmPasswordEditTextView?.visibility == View.VISIBLE) {
            // Verificação se os campos de senha não estão vazios
            if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
                // Verifique se algum dos campos de senha está vazio
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.passwords_not_empty), Toast.LENGTH_SHORT).show()
                    return
                }

                // Verifique se as senhas não coincidem
                if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                    return
                }
            } else {
                // Se os campos estão visíveis, mas estão vazios
                Toast.makeText(requireContext(), getString(R.string.passwords_not_empty), Toast.LENGTH_SHORT).show()
                return
            }
        }
        if (selectedImageUri != null) {
            // YES IMAGE
            uploadImageAndSaveProfileWithNewAvatar()
        } else {
            // NO IMAGE
            val sharedPreferences =
                requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", 0)

            if (isNetworkAvailable(requireContext())) {
                // NO IMAGE YES NET
                ApiManager.apiService.getUserById(userId)
                    .enqueue(object : retrofit2.Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                val user = response.body()
                                val currentAvatarUrl = user?.avatar

                                updateUserProfile(currentAvatarUrl, "NO", "YES")
                            } else {
                                Log.e(
                                    "EditProfile",
                                    "Erro ao obter o usuário: ${response.message()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Log.e(
                                "EditProfile",
                                "Falha na requisição para obter o usuário: ${t.message}"
                            )
                        }
                    })

            }
            else{
                // NO IMAGE NO NET
                Log.d("EditProfile", "SEM INTERNET E SEM IMAGEM")
                userViewModel.getUserByIdUser(userId).observe(viewLifecycleOwner) { roomUser ->
                    Log.d("EditProfile", "${roomUser.avatar}")
                    updateUserProfile(roomUser.avatar, "NO", "NO")
                }
            }
        }
    }


    private fun uploadImageAndSaveProfileWithNewAvatar() {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
        val file = bitmapToFile(requireContext(), bitmap)


        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("file", "profile_image.jpg", requestFile)

        if (isNetworkAvailable(requireContext())) {

            val call = ApiManager.apiService.uploadImage(imagePart)
            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.string()
                        imageUrl?.let {
                            val pattern = Regex("\"url\":\"(\\S+)\"")
                            val matchResult = pattern.find(it)

                            matchResult?.let { result ->
                                val avatarUrl = result.groupValues[1]
                                updateUserProfile(avatarUrl, "YES", "YES")
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
                    Log.e("EditProfile", "Falha na requisição: ${t.message}")
                }
            })

        }
        else{
            val avatarUrl = file.absolutePath
            updateUserProfile(avatarUrl, "YES", "NO")
        }
    }



    private fun updateUserProfile(avatarUrl: String?, imageState: String?, netState: String?) {
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

        val sharedPreferences2 = requireContext().getSharedPreferences("update_user", Context.MODE_PRIVATE)
        with(sharedPreferences2.edit()) {
            putString("imageState", imageState)
            putString("netState", netState)
            apply()
        }

        val updatedUser = User(
            id = userId,
            username = updatedUsername,
            email = updatedEmail,
            password = if (newPassword.isNotEmpty()) newPassword else "",
            avatar = avatarUrl ?: null,
            userTypeId = userTypeId,
            platforms = null
        )

        if(updatedUsername.isEmpty()){
            Toast.makeText(
                requireContext(),
                getString(R.string.username_not_null),
                Toast.LENGTH_SHORT
            ).show()
        }
        else if(updatedEmail.isEmpty()){
            Toast.makeText(
                requireContext(),
                getString(R.string.email_not_null),
                Toast.LENGTH_SHORT
            ).show()
        }
        else {

            // First, fetch the Room User ID based on the userId
            userViewModel.getUserByIdUser(userId).observe(viewLifecycleOwner) { roomUser ->
                if (roomUser != null) {
                    val updatedUserRoom = com.example.play2plat_tpcm.room.entities.User(
                        id = roomUser.id, // Use the correct Room User ID
                        idUser = userId,
                        username = updatedUsername,
                        email = updatedEmail,
                        password = if (newPassword.isNotEmpty()) newPassword else "",
                        avatar = avatarUrl ?: null,
                        userTypeId = userTypeId
                    )

                    if (isNetworkAvailable(requireContext())) {
                        ApiManager.apiService.updateUser(userId, updatedUser)
                            .enqueue(object : retrofit2.Callback<User> {
                                override fun onResponse(
                                    call: Call<User>,
                                    response: Response<User>
                                ) {
                                    if (response.isSuccessful) {
                                        // Save to Room database
                                        saveUserToRoom(updatedUserRoom)
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.profile_updated_successfully),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        redirectToProfile()
                                    } else {
                                        val errorMessage = when (response.code()) {
                                            440 -> getString(R.string.username_in_use)
                                            441 -> getString(R.string.email_in_use)
                                            442 -> getString(R.string.password_invalid)
                                            443 -> getString(R.string.email_invalid)
                                            else -> getString(R.string.registration_failed)
                                        }
                                        Toast.makeText(
                                            requireContext(),
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        //saveUserToRoom(updatedUserRoom)
                                        //redirectToProfile()
                                    }
                                }

                                override fun onFailure(call: Call<User>, t: Throwable) {
                                    Log.e("EditProfile", "Request error: ${t.message}")
                                    // Save to Room database even if API fails
                                    saveUserToRoom(updatedUserRoom)
                                    redirectToProfile()
                                }
                            })
                    } else {
                        // Save to Room database when no internet
                        saveUserToRoom(updatedUserRoom)
                        val sharedPreferences2 = requireContext().getSharedPreferences(
                            "update_user",
                            Context.MODE_PRIVATE
                        )
                        with(sharedPreferences2.edit()) {
                            putInt("id", userId)
                            putString("username", updatedUsername)
                            putString("email", updatedEmail)
                            putString("password", if (newPassword.isNotEmpty()) newPassword else "")
                            putString("avatar", avatarUrl ?: "")
                            putInt("userTypeId", userTypeId)
                            apply()
                        }
                        redirectToProfile()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.profile_updated_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("EditProfile", "User not found in Room database")
                }
            }

        }

    }


    private fun saveUserToRoom(user: com.example.play2plat_tpcm.room.entities.User) {
        lifecycleScope.launch {
            userViewModel.updateUser(user)
            Log.d("EditProfile", "ROOM ATUALIZADO")
        }
    }
    /*
    private fun saveUserToRoom(user: com.example.play2plat_tpcm.room.entities.User) {
        userViewModel.updateUser(user)
        Log.d("EditProfile", "ROOM ATUALIZADO")
    }

     */


    private fun bitmapToFile(context: Context, bitmap: Bitmap): File {
        val directory = File(context.filesDir, "profile_images")
        if (!directory.exists()) {
            directory.mkdirs()  // Cria o diretório se não existir
        }

        // Crie um nome de arquivo único usando um timestamp
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "profile_image_$timestamp.jpg"

        val imageFile = File(directory, imageFileName)

        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return imageFile
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Edit_Profile_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
