package com.example.play2plat_tpcm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Environment
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.UserLogin
import com.example.play2plat_tpcm.api.UserLoginResponse
import com.example.play2plat_tpcm.room.entities.User
import com.example.play2plat_tpcm.room.vm.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import com.example.play2plat_tpcm.api.User as User1

class LoginPage : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var ivTogglePasswordVisibility: ImageView
    private lateinit var checkBoxRememberMe: CheckBox
    private var isPasswordVisible: Boolean = false

    private val userViewModel: UserViewModel by viewModels()

    companion object {
        private const val REQUEST_WRITE_STORAGE = 112
        private const val PREFS_NAME = "user_data"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etPassword = findViewById(R.id.et_password)
        ivTogglePasswordVisibility = findViewById(R.id.iv_toggle_password_visibility)
        checkBoxRememberMe = findViewById(R.id.checkbox_remember_me)

        ivTogglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }

        val btnSignIn: Button = findViewById(R.id.btn_sign_in)
        btnSignIn.setOnClickListener {
            loginUser()
        }

        val signUpTextView: TextView = findViewById(R.id.sign_up)
        signUpTextView.setOnClickListener {
            // Navigate to RegisterPage
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }

        userViewModel.getAllUsers().observe(this, Observer { users ->
            for (user in users) {
                Log.d("LoginPage", "User: ${user.id}, ${user.username}, ${user.email}, ${user.password}, ${user.avatar}")
            }
        })

        // Solicitar permissão de gravação
        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE)
        }

        checkRememberMe()
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye_off)
        } else {
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye)
        }
        isPasswordVisible = !isPasswordVisible
        etPassword.setSelection(etPassword.text.length)
    }

    private fun loginUser() {
        val username = findViewById<EditText>(R.id.et_username).text.toString()
        val password = etPassword.text.toString()
        Log.d("Login", "${username}, ${password}")
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        if(isNetworkAvailable() == false){
            Toast.makeText(this, R.string.no_connection_login, Toast.LENGTH_SHORT).show()
            return
        }

        val userLogin = UserLogin(username, password)
        ApiManager.apiService.loginUser(userLogin).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                if (response.isSuccessful) {
                    val userLoginResponse = response.body()
                    if (userLoginResponse != null) {
                        val user = userLoginResponse.user
                        saveUserData(user)
                        if (user.avatar != null) {
                            saveAvatarImage(user.avatar!!) { imagePath ->
                                checkAndSaveUserToRoom(user, imagePath)
                            }
                        } else {
                            checkAndSaveUserToRoom(user, null)
                        }
                        // Salvar estado da CheckBox
                        saveRememberMeState(checkBoxRememberMe.isChecked)
                    } else {
                        Toast.makeText(this@LoginPage, R.string.error_response_from_server, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        441 -> getString(R.string.user_not_found)
                        442 -> getString(R.string.invalid_credentials)
                        else -> getString(R.string.login_failed)
                    }
                    Toast.makeText(this@LoginPage, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginPage, R.string.login_failed, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData(user: User1) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", user.id)
        editor.putInt("user_type_id", user.userTypeId)
        editor.apply()
    }

    private fun checkAndSaveUserToRoom(user: User1, avatarPath: String?) {
        userViewModel.getUserByIdUser(user.id).observe(this, Observer { existingUser ->
            if (existingUser == null) {
                // Se o usuário não existe no Room, insere-o
                val newUser = User(0, user.id, user.email, user.username, user.password, avatarPath, user.userTypeId)
                userViewModel.addUser(newUser)
            }
            // Redireciona para a próxima atividade
            Toast.makeText(this@LoginPage, R.string.login_successful, Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginPage, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun saveAvatarImage(avatarUrl: String, callback: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(avatarUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                val imagePath = saveImageToExternalStorage(bitmap)
                runOnUiThread {
                    callback(imagePath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap): String {
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Play2Plat")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "user_avatar.png")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun saveRememberMeState(isChecked: Boolean) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_REMEMBER_ME, isChecked)
        editor.apply()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkRememberMe() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
        if (rememberMe) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

