package com.example.playlistMaker.mediaLibraryClasses

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,

    @SerializedName("trackTimeMillis")
    val trackTime: Long?,

    @SerializedName("artworkUrl100")
    val url: String?,

    @SerializedName("collectionName")
    val collectionName: String? = null,

    @SerializedName("releaseDate")
    val releaseDate: String? = null,

    @SerializedName("primaryGenreName")
    val primaryGenreName: String? = null,

    @SerializedName("country")
    val country: String? = null
) : Serializable {
    fun getCoverArtwork(): String? {
        return url?.takeIf { '/' in it }?.replaceAfterLast('/', LARGE_COVER_RESOLUTION)
    }

    companion object {
        private const val LARGE_COVER_RESOLUTION: String = "512x512bb.jpg"
    }
}
