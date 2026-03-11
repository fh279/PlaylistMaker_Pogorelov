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
import com.example.playlistMaker.SearchScreenClasses.ITunesSearchResponse
import com.example.playlistMaker.SearchScreenClasses.SearchApi
import com.example.playlistMaker.mediaLibraryClasses.Track
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class SearchActivity : AppCompatActivity() {
    // 1. Добавлены enum и поле состояния плейсхолдера
    private enum class PlaceholderState { NONE, NOTHING_FOUND, ERROR }
    private var currentPlaceholderState = PlaceholderState.NONE
    private var editTextValue = ""
    val iTunesBaseStringUrl: String = "https://itunes.apple.com"
    private lateinit var toolbar: MaterialToolbar
    private lateinit var clearButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var trackListListPlaceholderContainer: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderRefreshButton: Button
    lateinit var adapter: TrackListAdapter
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
        adapter = TrackListAdapter(listOf())
        trackListRecyclerView.adapter = adapter
        toolbar.setNavigationOnClickListener { finish() }
        val realTrackList = mutableListOf<Track>()
        trackListRecyclerView.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        // 2. Восстановление состояния после смены темы
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
            beforeTextChanged = { s, _, _, _ -> },
            onTextChanged = { s, _, _, _ ->
                editTextValue = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
            },
            afterTextChanged = { _ -> }
        )
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) performSearch(query)
                true
            }
            false
        }
        placeholderRefreshButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) performSearch(query)
        }
        // 3. clearButton использует updatePlaceHolderState вместо прямого .visibility = GONE
        clearButton.setOnClickListener {
            searchEditText.text?.clear()
            editTextValue = ""
            searchEditText.clearFocus()
            hideKeyboard(searchEditText as View)
            updatePlaceHolderState(isError = false, isEmpty = false)
        }
    }
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
    // 4. Сохраняем состояние плейсхолдера
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
    fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
    private fun updateUIWithResults(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            updatePlaceHolderState(isError = false, isEmpty = true)
        } else {
            adapter.updateTracks(tracks)
            updatePlaceHolderState(isError = false, isEmpty = false)
        }
    }
    // 5. Исправлен updatePlaceHolderState: контейнер становится VISIBLE + обновляется currentPlaceholderState
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
                Toast.makeText(this, "типа ошибка", Toast.LENGTH_SHORT).show()
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
        const val PLACEHOLDER_STATE_KEY: String = "PLACEHOLDER_STATE"  // 6. Новый ключ
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
                        trackListRecyclerView.adapter = TrackListAdapter(it.results)
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