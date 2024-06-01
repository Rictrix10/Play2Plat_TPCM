package com.example.play2plat_tpcm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.User
import com.example.play2plat_tpcm.api.UserLogin
import com.example.play2plat_tpcm.api.UserLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var ivTogglePasswordVisibility: ImageView
    private var isPasswordVisible: Boolean = false

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
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.ic_eye_off)
        } else {
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.ic_eye)
        }
        isPasswordVisible = !isPasswordVisible
        etPassword.setSelection(etPassword.text.length)
    }

    private fun loginUser() {
        val username = findViewById<EditText>(R.id.et_username).text.toString()
        val password = etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@LoginPage, R.string.login_successful, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginPage, MainActivity::class.java)
                        startActivity(intent)
                        finish()
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


    private fun saveUserData(user: User) {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", user.id)
        editor.putString("user_email", user.email)
        editor.putString("user_username", user.username)
        editor.putString("user_password", user.password)
        editor.putInt("user_type_id", user.userTypeId)
        editor.apply()
    }
}

