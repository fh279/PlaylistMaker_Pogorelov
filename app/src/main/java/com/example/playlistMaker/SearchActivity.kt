package com.example.playlistMaker

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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

        // Restore editTextValue value from savedInstanceState(), if present
        if (savedInstanceState != null) {
            editTextValue = savedInstanceState.getString(contentKey, "")
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
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
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

    // Save state before killing Activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(contentKey, editTextValue)
        Log.i("SAVE", "onSaveInstanceState called, saving editTextValue: '$editTextValue'")
    }

    // Restore state after Activity is created
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Get editText value from the Bundle (nor from the class field)
        val restoredValue = savedInstanceState.getString(contentKey, "")
        editTextValue = restoredValue
        Log.i("RESTORE", "onRestoreInstanceState called, restored editTextValue: '$restoredValue'")
    }

    companion object editTextContent {
        const val contentKey: String = "TEXT_FIELD_CONTENT"
    }
}
