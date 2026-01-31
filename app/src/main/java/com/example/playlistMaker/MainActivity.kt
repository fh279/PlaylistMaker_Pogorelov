package com.example.playlistMaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
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


        // Way 1. Implementation of anonymous object
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.search_button_toast_text,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        searchButton.setOnClickListener(imageClickListener)


        // Way 2. Implementation of lambda expression
        mediaButton.setOnClickListener {
            Toast.makeText(
                this,
                R.string.media_button_toast_text,
                Toast.LENGTH_SHORT
            ).show()
        }

        settingsButton.setOnClickListener {
            Toast.makeText(
                this,
                R.string.settings_button_toast_text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
