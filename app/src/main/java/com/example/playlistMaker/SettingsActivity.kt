package com.example.playlistMaker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        enableEdgeToEdge()

        val toolbar = findViewById<MaterialToolbar>(R.id.settings_screen_toolbar)
        toolbar.setNavigationOnClickListener { finishAffinity() }
    }
}