package com.example.play2plat_tpcm

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var isAdmin: IsAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userTypeId = sharedPreferences.getInt("user_type_id", 2)

        isAdmin = IsAdmin(userTypeId == 1)

        changeTabsText(R.id.games_text, true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.layout, Games_2_Fragment())
            .commit()

        updateAdminIconVisibility()
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.games_lay -> {
                replaceFragment(Games_2_Fragment())
                updateTabSelection(R.id.games_lay, R.id.games_icon, R.drawable.icon_games_selected, R.id.games_text)
            }
            R.id.favorites_lay -> {
                replaceFragment(Favorites_Fragment())
                updateTabSelection(R.id.favorites_lay, R.id.favorites_icon, R.drawable.icon_favorites_selected, R.id.favorites_text)
            }
            R.id.profile_lay -> {
                val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", 0)
                val profileFragment = Profile_Fragment.newInstance(userId)
                replaceFragment(profileFragment)
                updateTabSelection(R.id.profile_lay, R.id.profile_icon, R.drawable.icon_profile_selected, R.id.profile_text)
            }
            R.id.search_lay -> {
                replaceFragment(Search_Fragment())
                updateTabSelection(R.id.search_lay, R.id.search_icon, R.drawable.icon_search_selected, R.id.search_text)
            }
            R.id.new_game_lay -> {
                replaceFragment(Add_New_Game_Fragment())
                updateTabSelection(R.id.new_game_lay, R.id.add_new_game_icon, R.drawable.icon_add_selected, null)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val containerView = findViewById<View>(R.id.layout)
        if (containerView != null) {
            fragmentTransaction.replace(R.id.layout, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else {
            Log.e("MainActivity", "Container R.id.layout nÃ£o encontrado.")
        }
    }

    private fun updateTabSelection(layoutId: Int, iconId: Int, selectedDrawableId: Int, selectedTextId: Int?) {
        val tabs = listOf(
            Triple(R.id.games_lay, R.id.games_icon, R.id.games_text),
            Triple(R.id.favorites_lay, R.id.favorites_icon, R.id.favorites_text),
            Triple(R.id.profile_lay, R.id.profile_icon, R.id.profile_text),
            Triple(R.id.search_lay, R.id.search_icon, R.id.search_text),
            Triple(R.id.new_game_lay, R.id.add_new_game_icon, null) // Null para o texto do new_game_lay
        )

        tabs.forEach { (layId, icon, text) ->
            if (layId == layoutId) {
                changeTabsIcon(icon, selectedDrawableId)
                text?.let { changeTabsText(it, true) }
            } else {
                when (icon) {
                    R.id.games_icon -> changeTabsIcon(icon, R.drawable.icon_games)
                    R.id.favorites_icon -> changeTabsIcon(icon, R.drawable.icon_favorites)
                    R.id.profile_icon -> changeTabsIcon(icon, R.drawable.icon_profile)
                    R.id.search_icon -> changeTabsIcon(icon, R.drawable.icon_search)
                    R.id.add_new_game_icon -> changeTabsIcon(icon, R.drawable.icon_add)
                }
                text?.let { changeTabsText(it, false) }
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
        val unselectedColor = ContextCompat.getColor(this, R.color.white)

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
        adminLayout.visibility = if (isAdmin.isAdmin()) View.VISIBLE else View.GONE
    }
}