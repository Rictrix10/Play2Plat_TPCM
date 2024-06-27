package com.example.play2plat_tpcm

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applyThemeFromPreferences()
        applyLocaleFromPreferences()
    }

    private fun applyThemeFromPreferences() {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val selectedTheme = sharedPreferences.getInt("selected_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(selectedTheme)
    }

    private fun applyLocaleFromPreferences() {
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val localeTag = sharedPreferences.getString("selected_locale", null)
        Log.d("Idioma Recebido", "$localeTag")

        if (localeTag == null || localeTag == "auto") {
            val systemLocale = Resources.getSystem().configuration.locale
            updateLocale(this, systemLocale)
        } else if (localeTag == "en") {
            updateLocale(this, Locale.ENGLISH)
        } else {
            val locale = Locale.forLanguageTag(localeTag)
            updateLocale(this, locale)
        }
    }

    private fun updateLocale(context: Context, locale: Locale) {
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        val newContext = context.createConfigurationContext(configuration)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        Locale.setDefault(locale)

        // Exemplo de uso para verificar se a mudança de localização foi aplicada corretamente
        val message = newContext.getString(R.string.account_deleted_success)
        Log.d("Texto", message)
    }

}

