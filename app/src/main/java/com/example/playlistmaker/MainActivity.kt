package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Надо для того что бы воткнуть кастомный тулбар с AppBarLayout.
        // вообще еще есть 2 способ замутить тулбар - мегакастомный.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Показывает стрелку «назад»
        supportActionBar?.setDisplayShowHomeEnabled(true) // Включение обработки клика. пока не надо, наверное.
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}