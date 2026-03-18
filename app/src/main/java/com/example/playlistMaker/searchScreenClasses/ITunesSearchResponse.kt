package com.example.playlistMaker.searchScreenClasses

import com.example.playlistMaker.mediaLibraryClasses.Track

data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<Track>
)