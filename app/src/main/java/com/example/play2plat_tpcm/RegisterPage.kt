package com.example.play2plat_tpcm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.UserRegister
import com.example.play2plat_tpcm.room.entities.User
import com.example.play2plat_tpcm.room.vm.UserViewModel
import androidx.lifecycle.Observer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPage : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var ivTogglePasswordVisibility: ImageView
    private lateinit var ivToggleConfirmPasswordVisibility: ImageView
    private var isPasswordVisible: Boolean = false
    private var isConfirmPasswordVisible: Boolean = false

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        ivTogglePasswordVisibility = findViewById(R.id.iv_toggle_password_visibility)
        ivToggleConfirmPasswordVisibility = findViewById(R.id.iv_toggle_confirm_password_visibility)

        ivTogglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }

        ivToggleConfirmPasswordVisibility.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }

        val btnSignUp: Button = findViewById(R.id.btn_sign_up)
        btnSignUp.setOnClickListener {
            registerUser()
        }

        val signInTextView: TextView = findViewById(R.id.sign_in)
        signInTextView.setOnClickListener {
            // Redireciona para LoginPage quando clicado
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish() // Fecha a RegisterPage
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye_off)
        } else {
            etPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye)
        }
        isPasswordVisible = !isPasswordVisible
        etPassword.setSelection(etPassword.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            etConfirmPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            ivToggleConfirmPasswordVisibility.setImageResource(R.drawable.icon_eye_off)
        } else {
            etConfirmPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
            ivToggleConfirmPasswordVisibility.setImageResource(R.drawable.icon_eye)
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        etConfirmPassword.setSelection(etConfirmPassword.text.length)
    }

    private fun registerUser() {
        val username = findViewById<EditText>(R.id.et_username).text.toString()
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (password != confirmPassword) {
            Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show()
            return
        }

        val user = UserRegister(username, email, password, userTypeId = 2)
        ApiManager.apiService.createUser(user).enqueue(object : Callback<UserRegisterResponse> {
            override fun onResponse(call: Call<UserRegisterResponse>, response: Response<UserRegisterResponse>) {
                if (response.isSuccessful) {
                    // Save user data to Room database
                    val newUser = User(0, response.body()!!.id, email, username, password, null, 2)
                    userViewModel.addUser(newUser)

                    // Observe to check if the user has been added
                    userViewModel.getUserByEmailAndPassword(email, password).observe(this@RegisterPage, Observer { user ->
                        if (user != null) {
                            Toast.makeText(this@RegisterPage, R.string.user_registered_successfully, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterPage, LoginPage::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
                } else {
                    val errorMessage = when (response.code()) {
                        440 -> getString(R.string.username_in_use)
                        441 -> getString(R.string.email_in_use)
                        442 -> getString(R.string.password_invalid)
                        443 -> getString(R.string.email_invalid)
                        else -> getString(R.string.registration_failed)
                    }
                    Toast.makeText(this@RegisterPage, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


