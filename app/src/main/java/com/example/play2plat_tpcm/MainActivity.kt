package com.example.play2plat_tpcm

import Profile_Fragment
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var isAdmin: IsAdmin // Declaração da variável para controlar o status de administração

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userTypeId = sharedPreferences.getInt("user_type_id", 2)

        // Verificar o tipo de utilizador para definir o status de administrador
        isAdmin = IsAdmin(userTypeId == 1)

        // Iniciando o fragmento padrão
        supportFragmentManager.beginTransaction()
            .add(R.id.layout, Games_Fragment()).commit()

        // Verifica se o usuário é administrador e atualiza a visibilidade do ícone de administração
        updateAdminIconVisibility()
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.games_lay -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout, Games_Fragment())
                    .commit()
                changeTabsIcon(R.id.games_icon, R.drawable.icon_games_selected)
                changeTabsIcon(R.id.favorites_icon, R.drawable.icon_favorites)
                changeTabsIcon(R.id.profile_icon, R.drawable.icon_profile)
                changeTabsIcon(R.id.search_icon, R.drawable.icon_search)
                changeTabsIcon(R.id.add_new_game_icon, R.drawable.icon_add)
                changeTabsText(R.id.games_text, true)
                changeTabsText(R.id.favorites_text, false)
                changeTabsText(R.id.profile_text, false)
                changeTabsText(R.id.search_text, false)
            }

            R.id.favorites_lay -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout, Favorites_Fragment())
                    .commit()
                changeTabsIcon(R.id.games_icon, R.drawable.icon_games)
                changeTabsIcon(R.id.favorites_icon, R.drawable.icon_favorites_selected)
                changeTabsIcon(R.id.profile_icon, R.drawable.icon_profile)
                changeTabsIcon(R.id.search_icon, R.drawable.icon_search)
                changeTabsIcon(R.id.add_new_game_icon, R.drawable.icon_add)
                changeTabsText(R.id.games_text, false)
                changeTabsText(R.id.favorites_text, true)
                changeTabsText(R.id.profile_text, false)
                changeTabsText(R.id.search_text, false)
            }

            R.id.profile_lay -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout, Profile_Fragment())
                    .commit()
                changeTabsIcon(R.id.games_icon, R.drawable.icon_games)
                changeTabsIcon(R.id.favorites_icon, R.drawable.icon_favorites)
                changeTabsIcon(R.id.profile_icon, R.drawable.icon_profile_selected)
                changeTabsIcon(R.id.search_icon, R.drawable.icon_search)
                changeTabsIcon(R.id.add_new_game_icon, R.drawable.icon_add)
                changeTabsText(R.id.games_text, false)
                changeTabsText(R.id.favorites_text, false)
                changeTabsText(R.id.profile_text, true)
                changeTabsText(R.id.search_text, false)
            }

            R.id.search_lay -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout, Search_Fragment())
                    .commit()
                changeTabsIcon(R.id.games_icon, R.drawable.icon_games)
                changeTabsIcon(R.id.favorites_icon, R.drawable.icon_favorites)
                changeTabsIcon(R.id.profile_icon, R.drawable.icon_profile)
                changeTabsIcon(R.id.search_icon, R.drawable.icon_search_selected)
                changeTabsIcon(R.id.add_new_game_icon, R.drawable.icon_add)
                changeTabsText(R.id.games_text, false)
                changeTabsText(R.id.favorites_text, false)
                changeTabsText(R.id.profile_text, false)
                changeTabsText(R.id.search_text, true)
            }

            R.id.new_game_lay -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layout, Add_New_Game_Fragment())
                    .commit()
                changeTabsIcon(R.id.games_icon, R.drawable.icon_games)
                changeTabsIcon(R.id.favorites_icon, R.drawable.icon_favorites)
                changeTabsIcon(R.id.profile_icon, R.drawable.icon_profile)
                changeTabsIcon(R.id.search_icon, R.drawable.icon_search)
                changeTabsIcon(R.id.add_new_game_icon, R.drawable.icon_add_selected)
                changeTabsText(R.id.games_text, false)
                changeTabsText(R.id.favorites_text, false)
                changeTabsText(R.id.profile_text, false)
                changeTabsText(R.id.search_text, false)
            }
        }
    }


    private fun changeTabsIcon(iconId: Int, drawableId: Int) {
        findViewById<ImageView>(iconId)?.setImageResource(drawableId)
    }

    private fun changeTabsText(textViewId: Int, isSelected: Boolean) {
        val textView = findViewById<TextView>(textViewId)
        val selectedColorStart = Color.parseColor("#FF1F53")
        val selectedColorEnd = Color.parseColor("#1F91E9")
        val unselectedColor = Color.WHITE

        if (isSelected) {
            val text = textView.text.toString()
            textView.text = text
            val textPaint = textView.paint
            val width = textPaint.measureText(text)
            val textShader = LinearGradient(
                0f, 0f, width, textView.textSize,
                intArrayOf(selectedColorStart, selectedColorEnd),
                null, Shader.TileMode.CLAMP
            )
            textPaint.shader = textShader
        } else {
            textView.text = textView.text.toString()
            textView.setTextColor(unselectedColor)
            textView.paint.shader = null
        }
    }


    private fun updateAdminIconVisibility() {
        val adminLayout = findViewById<View>(R.id.new_game_lay)
        if (isAdmin.isAdmin()) {
            adminLayout.visibility = View.VISIBLE
        } else {
            adminLayout.visibility = View.GONE
        }
    }
}