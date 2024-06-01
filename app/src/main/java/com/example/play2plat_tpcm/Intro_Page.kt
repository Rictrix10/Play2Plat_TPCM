package com.example.play2plat_tpcm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import android.graphics.Color


class Intro_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro_page)

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
            // Cria uma intenção para iniciar a atividade MainActivity
            val intent = Intent(this, LoginPage::class.java)
            // Inicia a atividade MainActivity
            startActivity(intent)
        }

        val tvDescription: TextView = findViewById(R.id.tv_description)
        // Busca o texto do description a partir do strings.xml
        val text = getString(R.string.tv_description_text)
        val spannable = SpannableString(text)

        // Find the start and end index of the word "plat"
        val start = text.indexOf("plat")
        val end = start + "plat".length

        // Set the color to #FF1F53
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#FF1F53")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvDescription.text = spannable
    }
}
