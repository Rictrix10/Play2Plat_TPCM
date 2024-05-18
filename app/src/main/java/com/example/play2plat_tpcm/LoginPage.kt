package com.example.play2plat_tpcm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.play2plat_tpcm.api.ApiManager
import com.example.play2plat_tpcm.api.UserLogin
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
    }

    private fun loginUser() {
        val username = findViewById<EditText>(R.id.et_username).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()

        val userLogin = UserLogin(username, password)
        ApiManager.apiService.loginUser(userLogin).enqueue(object : Callback<UserLogin> {
            override fun onResponse(call: Call<UserLogin>, response: Response<UserLogin>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginPage, "Login sucessfully", Toast.LENGTH_SHORT).show()
                    // Login successful, navigate to the next activity
                    //startActivity(Intent(this@LoginPage, AddNewGame::class.java))
                    //finish() // Finish the current activity to prevent returning to it when pressing back
                } else {
                    // Login failed, show an error message
                    Toast.makeText(this@LoginPage, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserLogin>, t: Throwable) {
                // An error occurred, show an error message
                Toast.makeText(this@LoginPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
