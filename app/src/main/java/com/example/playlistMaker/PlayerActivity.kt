package com.example.playlistMaker

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistMaker.mediaLibraryClasses.Track
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.player_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.player_screen_toolbar)
        val albumCoverContainer: CardView = findViewById(R.id.player_album_cover)
        val albumCover: ImageView = findViewById(R.id.player_album_cover_image)

        val trackTitleText: TextView = findViewById(R.id.player_screen_track_title)
        val artistNameText: TextView = findViewById(R.id.player_screen_artist_name)

        val trackTimeValueTextView: TextView = findViewById(R.id.player_screen_time_value)

        val albumLabelTextView: TextView = findViewById(R.id.player_screen_album_label_text)
        val albumValueTextView: TextView = findViewById(R.id.player_screen_album_value_text)

        val yearLabelTextView: TextView = findViewById(R.id.player_screen_year_label_text)
        val yearTitleTextView: TextView = findViewById(R.id.player_screen_year_value_text)

        val genreLabelView: TextView = findViewById(R.id.player_screen_genre_value_text)
        val countryLabelView: TextView = findViewById(R.id.player_screen_country_value_text)


        val playbackProgressView: TextView = findViewById(R.id.player_screen_playBack_process_text)
        val playButton: ImageButton = findViewById(R.id.player_screen_play_button)

        toolbar.setNavigationOnClickListener { finish() }

        val track = intent.getSerializableExtra(EXTRA_TRACK) as? Track

        if (track != null) {
            trackTitleText.text = track.trackName.orEmpty()
            artistNameText.text = track.artistName.orEmpty()

            val durationText = convertMillisToMinutes(track.trackTime)

            durationText.let {
                trackTimeValueTextView.text = it
                playbackProgressView.text = it
            }

            if (track.collectionName.isNullOrEmpty()) {
                albumLabelTextView.visibility = View.GONE
                albumValueTextView.visibility = View.GONE
            } else {
                albumValueTextView.run {
                    text = track.collectionName
                    movementMethod = ScrollingMovementMethod()
                }
            }

            val year = track.releaseDate
                ?.takeIf { it.length >= 4 }
                ?.substring(0, 4)
            if (year.isNullOrEmpty()) {
                yearLabelTextView.visibility = View.GONE
            } else {
                yearLabelTextView.run {
                    visibility = View.VISIBLE
                }
                yearTitleTextView.run {
                    visibility = View.VISIBLE
                    text = year
                }
            }


            genreLabelView.text = track.primaryGenreName.orEmpty()
            countryLabelView.text = track.country.orEmpty()

            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(albumCover)
        }

        playButton.setOnClickListener { /*stub*/ }
    }

    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"
    }
}