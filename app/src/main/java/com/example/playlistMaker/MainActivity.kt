package com.example.playlistMaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        val searchButton = findViewById<Button>(R.id.main_screen_search_button)
        val mediaButton = findViewById<Button>(R.id.main_screen_media_button)
        val settingsButton = findViewById<Button>(R.id.main_screen_settings_button)

        searchButton.setOnClickListener { startSearch() }
        mediaButton.setOnClickListener { startMediaLibrary() }
        settingsButton.setOnClickListener { startSettings() }
    }

    private fun startSearch() {
        val searchIntent = Intent(this, SearchActivity::class.java)
        startActivity(searchIntent)
    }

    private fun startMediaLibrary() {
        val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
        startActivity(mediaLibraryIntent)
    }

    private fun startSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }
}
