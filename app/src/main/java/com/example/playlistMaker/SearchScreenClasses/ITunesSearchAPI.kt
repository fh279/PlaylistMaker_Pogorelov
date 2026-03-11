package com.example.playlistMaker.SearchScreenClasses

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchApi {

    @GET("/search")
    fun search(
        @Query("term")
        text: String,
        @Query("entity")
        entity: String = "song"

    ): Call<ITunesSearchResponse>
}

object SearchApi{
    private const val BASE_URL = "https://itunes.apple.com/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val iTunesSearchApi: ITunesSearchApi by lazy {
        retrofit.create(ITunesSearchApi::class.java)
    }
}