package com.example.play2plat_tpcm

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Intro_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro_page)

        // Verifica se o usuário está guardado nas SharedPreferences
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("user_id", 0)

        if (currentUserId != null) {
            // Redireciona diretamente para a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Adiciona padding para evitar sobreposição com as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Localiza o botão "Get Started" pelo ID
        val btnGetStarted = findViewById<Button>(R.id.btn_get_started)
        // Define o texto do botão a partir do strings.xml
        btnGetStarted.text = getString(R.string.btn_get_started_text)

        // Define um ouvinte de clique para o botão "Get Started"
        btnGetStarted.setOnClickListener {
            // Cria uma intenção para iniciar a atividade LoginPage
            val intent = Intent(this, LoginPage::class.java)
            // Inicia a atividade LoginPage
            startActivity(intent)
        }

        val tvDescription: TextView = findViewById(R.id.tv_description)
        // Busca o texto do description a partir do strings.xml
        val text = getString(R.string.tv_description_text)
        val spannable = SpannableString(text)

        // Busca o início e o fim da palavra "plat"
        val start = text.indexOf("plat")
        val end = start + "plat".length

        // Define a cor para #FF1F53
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#FF1F53")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvDescription.text = spannable
    }
}
