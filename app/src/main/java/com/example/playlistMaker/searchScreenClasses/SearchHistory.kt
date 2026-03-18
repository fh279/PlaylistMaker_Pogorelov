package com.example.playlistMaker.searchScreenClasses

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistMaker.mediaLibraryClasses.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistory(
    private val sharedPreferences: SharedPreferences
) {
    private val gson = Gson()

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson<ArrayList<Track>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addTrack(track: Track) {
        val current = getHistory().toMutableList()

        val existingIndex = current.indexOfFirst { it.trackId == track.trackId }
        if (existingIndex != -1) { current.removeAt(existingIndex) }

        current.add(0, track)

        if (current.size > MAX_HISTORY_SIZE) {
            current.subList(MAX_HISTORY_SIZE, current.size).clear()
        }

        saveHistory(current)
    }

    fun clear() =
        sharedPreferences.edit {
            remove(KEY_HISTORY)
        }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit {
            putString(KEY_HISTORY, json)
        }
    }

    companion object {
        const val PREFS_NAME = "playlist_maker_prefs"
        private const val KEY_HISTORY = "key_search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}