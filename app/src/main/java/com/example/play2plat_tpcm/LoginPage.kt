package com.example.play2plat_tpcm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.User
import com.example.play2plat_tpcm.api.UserRegister
import com.example.play2plat_tpcm.api.UserLogin
import com.example.play2plat_tpcm.api.UserLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

    private fun loginUser() {
        val username = findViewById<EditText>(R.id.et_username).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userLogin = UserLogin(username, password)
        ApiManager.apiService.loginUser(userLogin).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                if (response.isSuccessful) {
                    val userLoginResponse = response.body()
                    if (userLoginResponse != null) {
                        // Aqui está o corpo da resposta do login
                        val user = userLoginResponse.user

                        // Salvar os dados do utilizador no SharedPreferences
                        saveUserData(user)

                        Toast.makeText(this@LoginPage, "Login successfully", Toast.LENGTH_SHORT).show()
                        // Login successful, navigate to MainActivity
                        val intent = Intent(this@LoginPage, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Finish the current activity to prevent returning to it when pressing back
                    } else {
                        // Se a resposta do servidor não contiver dados do usuário, exibir mensagem de erro
                        Toast.makeText(this@LoginPage, "Invalid response from server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Login failed, show an error message
                    Toast.makeText(this@LoginPage, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                // An error occurred, show an error message
                Toast.makeText(this@LoginPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
