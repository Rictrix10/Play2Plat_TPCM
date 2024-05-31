package com.example.play2plat_tpcm

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
import com.example.play2plat_tpcm.api.UserRegister
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

    private fun registerUser() {
        val username = findViewById<EditText>(R.id.et_username).text.toString()
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password).text.toString()

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val user = UserRegister(username, email, password, userTypeId = 2)
        ApiManager.apiService.createUser(user).enqueue(object : Callback<UserRegister> {
            override fun onResponse(call: Call<UserRegister>, response: Response<UserRegister>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterPage, "User registered successfully", Toast.LENGTH_SHORT).show()
                    // Registro bem-sucedido, redireciona para LoginPage
                    val intent = Intent(this@RegisterPage, LoginPage::class.java)
                    startActivity(intent)
                    finish() // Fecha a RegisterPage
                } else {
                    Toast.makeText(this@RegisterPage, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserRegister>, t: Throwable) {
                Toast.makeText(this@RegisterPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
