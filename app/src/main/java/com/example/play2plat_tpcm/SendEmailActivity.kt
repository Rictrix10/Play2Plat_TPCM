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
import com.example.play2plat_tpcm.api.Email
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

class SendEmailActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText

    companion object {
        private const val REQUEST_WRITE_STORAGE = 112
        private const val PREFS_NAME = "user_data"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_send_email)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etEmail = findViewById(R.id.et_email)

        val btnSend: Button = findViewById(R.id.btn_send)
        btnSend.setOnClickListener {
            sendEmail()
        }

    }

    private fun sendEmail() {

        // COLOCAR MENSAGENS DE SUCESSO E ERRO NO PEDIDO


        val emailText = etEmail.text.toString()
        if (emailText.isEmpty()) {
            //Toast.makeText(this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        if(isNetworkAvailable() == false){
            //Toast.makeText(this, R.string.no_connection_login, Toast.LENGTH_SHORT).show()
            return
        }

        val email = Email(emailText)
        ApiManager.apiServiceFlask.sendEmail(email).enqueue(object : Callback<Email> {
            override fun onResponse(call: Call<Email>, response: Response<Email>) {

                if (response.isSuccessful) {
                    val requestResponse = response.body()

                } else {
                    /*
                    val errorMessage = when (response.code()) {

                        441 -> getString(R.string.user_not_found)
                        442 -> getString(R.string.invalid_credentials)
                        else -> getString(R.string.login_failed)
                    }
                    Toast.makeText(this@SendEmailActivity, errorMessage, Toast.LENGTH_SHORT).show()

                     */
                }
            }

            override fun onFailure(call: Call<Email>, t: Throwable) {
                //Toast.makeText(this@SendEmailActivity, R.string.login_failed, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}

