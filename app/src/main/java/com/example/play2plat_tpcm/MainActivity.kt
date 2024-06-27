package com.example.play2plat_tpcm

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var isAdmin: IsAdmin
    private val navigationViewModel: FragmentNavigationViewModel by viewModels()
    private val SELECTED_TAB_ID_KEY = "selected_tab_id"

    override fun onCreate(savedInstanceState: Bundle?) {

        applyLocaleFromPreferences()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userTypeId = sharedPreferences.getInt("user_type_id", 2)
        isAdmin = IsAdmin(userTypeId == 1)

        // Restaurar o estado da tab selecionada após mudanças de configuração
        val selectedTabId = savedInstanceState?.getInt(SELECTED_TAB_ID_KEY)
            ?: sharedPreferences.getInt(SELECTED_TAB_ID_KEY, R.id.games_lay)
        updateTabSelection(selectedTabId)

        if (savedInstanceState == null) {
            // Adicionar o fragmento inicial se não houver estado salvo (primeira criação da activity)
            val initialFragment = Games_2_Fragment()
            navigationViewModel.addToStack(initialFragment)
            supportFragmentManager.beginTransaction()
                .replace(R.id.layout, initialFragment)
                .commit()
        }

        updateAdminIconVisibility()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salvar o estado da tab selecionada para lidar com mudanças de configuração
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val selectedTabId = sharedPreferences.getInt(SELECTED_TAB_ID_KEY, R.id.games_lay)
        outState.putInt(SELECTED_TAB_ID_KEY, selectedTabId)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.games_lay -> {
                if (isNetworkAvailable()) {
                    replaceFragment(Games_2_Fragment())
                }
                else{
                    redirectToNoConnectionFragment()
                }
                updateTabSelection(R.id.games_lay)
                saveSelectedTabId(R.id.games_lay)
            }
            R.id.favorites_lay -> {
                if (isNetworkAvailable()) {
                    replaceFragment(Favorites_Fragment())
                }
                else{
                    redirectToNoConnectionFragment()
                }
                updateTabSelection(R.id.favorites_lay)
                saveSelectedTabId(R.id.favorites_lay)
            }
            R.id.profile_lay -> {
                val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", 0)
                val profileFragment = Profile_Fragment.newInstance(userId)
                replaceFragment(profileFragment)
                updateTabSelection(R.id.profile_lay)
                saveSelectedTabId(R.id.profile_lay)
            }
            R.id.search_lay -> {
                if (isNetworkAvailable()) {
                    replaceFragment(Search_Fragment())
                }
                else{
                    redirectToNoConnectionFragment()
                }
                updateTabSelection(R.id.search_lay)
                saveSelectedTabId(R.id.search_lay)
            }
            R.id.new_game_lay -> {
                if (isNetworkAvailable()) {
                    replaceFragment(Add_New_Game_Fragment())
                }
                else{
                    redirectToNoConnectionFragment()
                }
                updateTabSelection(R.id.new_game_lay)
                saveSelectedTabId(R.id.new_game_lay)
            }
        }
    }

    private fun updateTabSelection(selectedTabId: Int) {
        val tabs = listOf(
            Triple(R.id.games_lay, R.id.games_icon, R.id.games_text),
            Triple(R.id.favorites_lay, R.id.favorites_icon, R.id.favorites_text),
            Triple(R.id.profile_lay, R.id.profile_icon, R.id.profile_text),
            Triple(R.id.search_lay, R.id.search_icon, R.id.search_text),
            Triple(R.id.new_game_lay, R.id.add_new_game_icon, null) // Null para o texto do new_game_lay
        )

        tabs.forEach { (layId, icon, text) ->
            if (layId == selectedTabId) {
                changeTabsIcon(icon, getSelectedIconId(layId))
                text?.let { changeTabsText(it, true) }
            } else {
                changeTabsIcon(icon, getDefaultIconId(icon))
                text?.let { changeTabsText(it, false) }
            }
        }
    }


    private fun saveSelectedTabId(tabId: Int) {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(SELECTED_TAB_ID_KEY, tabId)
        editor.apply()
    }

    private fun replaceFragment(fragment: Fragment) {
        navigationViewModel.addToStack(fragment)
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout, fragment)
            .addToBackStack(null) // Garante que o fragmento seja adicionado ao back stack
            .commit()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    private fun redirectToNoConnectionFragment() {
        val noConnectionFragment = NoConnectionFragment()
        navigationViewModel.addToStack(noConnectionFragment)
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout, noConnectionFragment)
            .addToBackStack(null)
            .commit()
    }


    private fun getSelectedIconId(layoutId: Int): Int {
        return when (layoutId) {
            R.id.games_lay -> R.drawable.icon_games_selected
            R.id.favorites_lay -> R.drawable.icon_favorites_selected
            R.id.profile_lay -> R.drawable.icon_profile_selected
            R.id.search_lay -> R.drawable.icon_search_selected
            R.id.new_game_lay -> R.drawable.icon_add_selected
            else -> R.drawable.icon_games_selected // Defina um padrão ou lide com outros casos se necessário
        }
    }

    private fun getDefaultIconId(iconId: Int): Int {
        return when (iconId) {
            R.id.games_icon -> R.drawable.icon_games
            R.id.favorites_icon -> R.drawable.icon_favorites
            R.id.profile_icon -> R.drawable.icon_profile
            R.id.search_icon -> R.drawable.icon_search
            R.id.add_new_game_icon -> R.drawable.icon_add
            else -> R.drawable.icon_games // Defina um padrão ou lide com outros casos se necessário
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

    private fun applyLocaleFromPreferences() {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val localeTag = sharedPreferences.getString("selected_locale", null)
        Log.d("Idioma Recebido", "$localeTag")

        if (localeTag == null || localeTag == "auto") {
            val systemLocale = resources.configuration.locale
            updateLocale(this, systemLocale)
        } else if (localeTag == "en") {
            updateLocale(this, Locale.ENGLISH)
        } else {
            val locale = Locale.forLanguageTag(localeTag)
            updateLocale(this, locale)
        }
    }

    private fun updateLocale(context: Context, locale: Locale) {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        val newContext = context.createConfigurationContext(configuration)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        Locale.setDefault(locale)

        // Exemplo de uso para verificar se a mudança foi aplicada corretamente
        val message = newContext.getString(R.string.account_deleted_success)
        Log.d("Texto", message)
    }
}

