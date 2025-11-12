package com.example.booktracker.data.remote.api

import com.example.booktracker.data.remote.model.BookItem
import com.example.booktracker.data.remote.model.GoogleBooksResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("langRestrict") langRestrict: String = "pt"
    ): Response<GoogleBooksResponse>

    @GET("volumes/{volumeId}")
    suspend fun getBookDetails(
        @Path("volumeId") volumeId: String
    ): Response<BookItem>
}