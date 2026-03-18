package com.example.playlistMaker
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistMaker.searchScreenClasses.ITunesSearchResponse
import com.example.playlistMaker.searchScreenClasses.SearchApi
import com.example.playlistMaker.searchScreenClasses.SearchHistory
import com.example.playlistMaker.mediaLibraryClasses.Track
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private enum class PlaceholderState { NONE, NOTHING_FOUND, ERROR }
    private var currentPlaceholderState = PlaceholderState.NONE
    private var editTextValue = ""
    private lateinit var toolbar: MaterialToolbar
    private lateinit var clearButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var trackListListPlaceholderContainer: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderRefreshButton: Button
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var historyRecycler: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: TrackListAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var adapter: TrackListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()

        toolbar = findViewById<MaterialToolbar>(R.id.search_screen_toolbar)
        clearButton = findViewById<ImageButton>(R.id.clearButton)
        searchEditText = findViewById<EditText>(R.id.searchEditText)
        trackListRecyclerView = findViewById<RecyclerView>(R.id.track_list_recycler_view)
        trackListListPlaceholderContainer = findViewById<LinearLayout>(R.id.placeholderContainer)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        placeholderRefreshButton = findViewById(R.id.placeholderRefreshButton)
        historyContainer = findViewById(R.id.search_history_layout)
        historyTitle = findViewById(R.id.tvHistoryTitle)
        historyRecycler = findViewById(R.id.history_recycler_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        searchHistory = SearchHistory(
            getSharedPreferences(SearchHistory.PREFS_NAME, MODE_PRIVATE)
        )

        adapter = TrackListAdapter(listOf()) { onTrackClicked(it) }
        trackListRecyclerView.adapter = adapter

        trackListRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        historyAdapter = TrackListAdapter(listOf()) {}
        historyRecycler.adapter = historyAdapter
        historyRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        toolbar.setNavigationOnClickListener { finish() }

        if (savedInstanceState != null) {
            editTextValue = savedInstanceState.getString(CONTENT_KEY, "")
            searchEditText.setText(editTextValue)
            searchEditText.setSelection(editTextValue.length)
            val stateName = savedInstanceState.getString(PLACEHOLDER_STATE_KEY, PlaceholderState.NONE.name)
            currentPlaceholderState = PlaceholderState.valueOf(stateName)
            when (currentPlaceholderState) {
                PlaceholderState.NOTHING_FOUND -> updatePlaceHolderState(isError = false, isEmpty = true)
                PlaceholderState.ERROR -> updatePlaceHolderState(isError = true, isEmpty = false)
                PlaceholderState.NONE -> Unit
            }
        }

        searchEditText.addTextChangedListener(
            beforeTextChanged = { s, _, _, _ -> /* Stub */ },
            onTextChanged = { s, _, _, _ ->
                editTextValue = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                if (searchEditText.hasFocus() && searchEditText.text.isEmpty()) {
                    showHistoryIfAvailable()
                } else {
                    hideHistory()
                }
            },
            afterTextChanged = { _ -> /* Stub */ }
        )
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                showHistoryIfAvailable()
            }
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) performSearch(query)
                true
            } else {
                false
            }
        }

        placeholderRefreshButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) performSearch(query)
        }

        clearButton.setOnClickListener {
            searchEditText.text?.clear()
            editTextValue = ""
            searchEditText.clearFocus()
            trackListRecyclerView.visibility = View.GONE
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            hideHistory()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val focused = currentFocus ?: return super.dispatchTouchEvent(ev)
            if (focused is EditText && !focused.isTouched(ev) && !historyContainer.isTouched(ev)) {
                focused.clearFocus()
                hideKeyboard(focused)
            } else if (focused !is EditText) {
                hideKeyboard(focused)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    private fun View.isTouched(ev: MotionEvent): Boolean {
        val rect = Rect()
        getGlobalVisibleRect(rect)
        return rect.contains(ev.rawX.toInt(), ev.rawY.toInt())
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CONTENT_KEY, editTextValue)
        outState.putString(PLACEHOLDER_STATE_KEY, currentPlaceholderState.name)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredValue = savedInstanceState.getString(CONTENT_KEY, "")
        editTextValue = restoredValue
    }

    private fun updateUIWithResults(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            updatePlaceHolderState(isError = false, isEmpty = true)
        } else {
            adapter.updateTracks(tracks)
            updatePlaceHolderState(isError = false, isEmpty = false)
        }
    }

    private fun updatePlaceHolderState(isError: Boolean, isEmpty: Boolean) {
        when {
            isError -> {
                currentPlaceholderState = PlaceholderState.ERROR
                trackListRecyclerView.visibility = View.GONE
                trackListListPlaceholderContainer.visibility = View.VISIBLE
                placeholderImage.setImageResource(R.drawable.network_troubles_icon)
                placeholderImage.visibility = View.VISIBLE
                placeholderText.setText(R.string.network_issues_text)
                placeholderText.visibility = View.VISIBLE
                placeholderRefreshButton.visibility = View.VISIBLE
            }
            isEmpty -> {
                currentPlaceholderState = PlaceholderState.NOTHING_FOUND
                trackListRecyclerView.visibility = View.GONE
                trackListListPlaceholderContainer.visibility = View.VISIBLE
                placeholderImage.setImageResource(R.drawable.nothing_found_icon)
                placeholderImage.visibility = View.VISIBLE
                placeholderText.setText(R.string.nothing_found_text)
                placeholderText.visibility = View.VISIBLE
                placeholderRefreshButton.visibility = View.GONE
            }
            else -> {
                currentPlaceholderState = PlaceholderState.NONE
                trackListListPlaceholderContainer.visibility = View.GONE
                trackListRecyclerView.visibility = View.VISIBLE
            }
        }
    }
    companion object EditTextContent {
        const val CONTENT_KEY: String = "TEXT_FIELD_CONTENT"
        const val PLACEHOLDER_STATE_KEY: String = "PLACEHOLDER_STATE"
    }

    private fun showHistoryIfAvailable() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty() && searchEditText.hasFocus() && searchEditText.text.isEmpty()) {
            historyAdapter.updateTracks(history)
            historyContainer.isVisible = true
            trackListRecyclerView.isVisible = false
            trackListListPlaceholderContainer.isVisible = false
            placeholderImage.isVisible = false
        } else {
            hideHistory()
        }
    }

    private fun hideHistory() {
        historyContainer.isVisible = false
    }

    private fun onTrackClicked(track: Track) {
        Toast.makeText(
            this,
            R.string.toast_track_added_text,
            Toast.LENGTH_SHORT
        ).show()

        searchHistory.addTrack(track)
        // showHistoryIfAvailable()
    }
    private fun performSearch(query: String) {
        val call = SearchApi.iTunesSearchApi.search(query)
        call.enqueue(object : Callback<ITunesSearchResponse> {
            override fun onResponse(
                call: Call<ITunesSearchResponse>,
                response: Response<ITunesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    searchResponse?.let {
                        updateUIWithResults(it.results)
                    }
                } else {
                    updatePlaceHolderState(isError = true, isEmpty = false)
                }
            }

            override fun onFailure(call: Call<ITunesSearchResponse>, t: Throwable) {
                updatePlaceHolderState(isError = true, isEmpty = false)
            }
        })
    }
}
