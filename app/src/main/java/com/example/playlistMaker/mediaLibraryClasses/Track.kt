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

    val artworkUrl100: String?,

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
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    }
}
