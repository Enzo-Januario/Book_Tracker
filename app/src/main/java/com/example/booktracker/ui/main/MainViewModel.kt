package com.example.booktracker.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.booktracker.data.local.database.BookTrackerDatabase
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.data.local.entity.Category
import com.example.booktracker.data.remote.api.RetrofitClient
import com.example.booktracker.data.repository.BookRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository
    val allBooks: LiveData<List<Book>>
    val allCategories: LiveData<List<Category>>

    init {
        val database = BookTrackerDatabase.getDatabase(application)
        repository = BookRepository(
            database.bookDao(),
            database.categoryDao(),
            database.readingHistoryDao(),
            RetrofitClient.googleBooksApi
        )
        allBooks = repository.allBooks
        allCategories = repository.allCategories
    }

    fun deleteBook(book: Book) = viewModelScope.launch {
        repository.deleteBook(book)
    }

    fun updateBook(book: Book) = viewModelScope.launch {
        repository.updateBook(book)
    }
}