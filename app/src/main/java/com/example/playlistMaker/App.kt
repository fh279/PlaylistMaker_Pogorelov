package com.example.playlistMaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistMaker.searchScreenClasses.SearchHistory

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(SearchHistory.PREFS_NAME, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val sharedPreferences = getSharedPreferences(SearchHistory.PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(KEY_DARK_THEME, darkThemeEnabled)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        private const val KEY_DARK_THEME = "key_dark_theme"
    }
}