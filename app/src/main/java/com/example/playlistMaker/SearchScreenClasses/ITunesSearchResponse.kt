package com.example.playlistMaker.SearchScreenClasses

import com.example.playlistMaker.mediaLibraryClasses.Track

data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<Track>
)