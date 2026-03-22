package com.example.playlistMaker

import java.text.SimpleDateFormat
import java.util.Locale

fun convertMillisToMinutes(millis: Long?): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
}