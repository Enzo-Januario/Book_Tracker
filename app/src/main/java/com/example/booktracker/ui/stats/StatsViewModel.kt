package com.example.booktracker.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.booktracker.data.local.database.BookTrackerDatabase
import com.example.booktracker.data.remote.api.RetrofitClient
import com.example.booktracker.data.repository.BookRepository
import kotlinx.coroutines.launch

data class BookStats(
    val totalBooksRead: Int = 0,
    val totalPagesRead: Int = 0,
    val currentlyReading: Int = 0,
    val averageRating: Float = 0f
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    private val _stats = MutableLiveData<BookStats>()
    val stats: LiveData<BookStats> = _stats

    init {
        val database = BookTrackerDatabase.getDatabase(application)
        repository = BookRepository(
            database.bookDao(),
            database.categoryDao(),
            database.readingHistoryDao(),
            RetrofitClient.googleBooksApi
        )
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            val readCount = repository.getReadBooksCount()
            val pagesRead = repository.getTotalPagesRead()
            val reading = repository.getCurrentlyReadingCount()
            val avgRating = repository.getAverageRating()

            _stats.value = BookStats(
                totalBooksRead = readCount,
                totalPagesRead = pagesRead,
                currentlyReading = reading,
                averageRating = avgRating
            )
        }
    }
}