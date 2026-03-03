package com.example.playlistMaker

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistMaker.mediaLibraryClasses.MockedTracks
import com.example.playlistMaker.mediaLibraryClasses.Track
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var editTextValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()

        Log.i("LIFECYCLE", "onCreate called, savedInstanceState = ${savedInstanceState != null}")

        val toolbar = findViewById<MaterialToolbar>(R.id.search_screen_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val clearButton = findViewById<ImageButton>(R.id.clearButton)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        val trackList = MockedTracks.listOfTracks
        val trackListRecyclerView = findViewById<RecyclerView>(R.id.track_list_recycler_view)

        trackListRecyclerView.layoutManager = LinearLayoutManager(
            /*context = */ this,
            /* orientation = */ RecyclerView.VERTICAL,
            /* reverseLayout = */ false
        )

        val trackListAdapter = TrackListAdapter(trackList)
        trackListRecyclerView.adapter = trackListAdapter

        // Restore editTextValue value from savedInstanceState(), if present
        if (savedInstanceState != null) {
            editTextValue = savedInstanceState.getString(CONTENT_KEY, "")
            Log.i("RESTORE", "Restored editTextValue from savedInstanceState: '$editTextValue'")
            // set restored editTextValue value in EditText
            searchEditText?.setText(editTextValue)
            searchEditText?.setSelection(editTextValue.length)
        }

        searchEditText?.addTextChangedListener(
            beforeTextChanged = { s, _, _, _ ->
                // stub
                Log.d("TEXT_CHANGE", "beforeTextChanged: '$s'")
            },
            onTextChanged = { s, _, _, _ ->
                editTextValue = s.toString()
                Log.d("TEXT_CHANGE", "onTextChanged -> editTextValue updated to: '$editTextValue'")
                clearButton.isVisible = !s.isNullOrEmpty()
            },
            afterTextChanged = { _ ->
                // stub
                Log.d("TEXT_CHANGE", "afterTextChanged, current editTextValue: '$editTextValue'")
            }
        )

        clearButton.setOnClickListener {
            searchEditText?.text?.clear()
            editTextValue = ""
            Log.i("CLEAR", "editTextValue cleared, now: '$editTextValue'")
            searchEditText?.clearFocus()
            hideKeyboard(searchEditText as View)
        }
    }

    // Hide keyboard after tapping out of edit text
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val currentFocus = currentFocus
            if (currentFocus is EditText) {
                val outRect = Rect()
                currentFocus.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    currentFocus.clearFocus()
                    hideKeyboard(currentFocus)
                }
            } else if (currentFocus != null) {
                hideKeyboard(currentFocus)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CONTENT_KEY, editTextValue)
        Log.i("SAVE", "onSaveInstanceState called, saving editTextValue: '$editTextValue'")
    }

    // Restore state after Activity is created
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Get editText value from the Bundle (nor from the class field)
        val restoredValue = savedInstanceState.getString(CONTENT_KEY, "")
        editTextValue = restoredValue
        Log.i("RESTORE", "onRestoreInstanceState called, restored editTextValue: '$restoredValue'")
    }

    companion object EditTextContent {
        const val CONTENT_KEY: String = "TEXT_FIELD_CONTENT"
    }
}

class TrackListAdapter(
    private val trackList: List<Track>
) : RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    /* resource = */ R.layout.track_list_item,
                    /* root = */ parent,
                    /* attachToRoot = */ false
                )
        return TrackListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(model = trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    class TrackListViewHolder(tracklistItemView: View) :
        RecyclerView.ViewHolder(tracklistItemView) {
        val albumCover = tracklistItemView.findViewById<ImageView>(R.id.album_cover)
        val songTitle = tracklistItemView.findViewById<TextView>(R.id.song_title)
        val artistAndTime = tracklistItemView.findViewById<TextView>(R.id.song_artist_and_time)
        val btnForward = tracklistItemView.findViewById<ImageView>(R.id.item_icon_forward)

        fun bind(model: Track) {
            songTitle.text = model.trackName
            "${model.artistName} • ${model.trackTime}".also { artistAndTime.text = it }
            Glide
                .with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(albumCover)
        }
    }
}

