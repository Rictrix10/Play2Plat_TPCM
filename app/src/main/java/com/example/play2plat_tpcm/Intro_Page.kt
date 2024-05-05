package com.example.play2plat_tpcm

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button


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

        // Define um ouvinte de clique para o botão "Get Started"
        btnGetStarted.setOnClickListener {
            // Cria uma intenção para iniciar a atividade MainActivity
            val intent = Intent(this, MainActivity::class.java)

            // Inicia a atividade MainActivity
            startActivity(intent)
        }
    }
}
