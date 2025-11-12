package com.example.booktracker.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.booktracker.data.local.database.BookTrackerDatabase
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.data.local.entity.ReadingHistory
import com.example.booktracker.data.remote.api.RetrofitClient
import com.example.booktracker.data.repository.BookRepository
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    private val _book = MutableLiveData<Book?>()
    val book: LiveData<Book?> = _book

    private val _readingHistory = MutableLiveData<List<ReadingHistory>>()
    val readingHistory: LiveData<List<ReadingHistory>> = _readingHistory

    init {
        val database = BookTrackerDatabase.getDatabase(application)
        repository = BookRepository(
            database.bookDao(),
            database.categoryDao(),
            database.readingHistoryDao(),
            RetrofitClient.googleBooksApi
        )
    }

    fun loadBook(bookId: Int) {
        viewModelScope.launch {
            _book.value = repository.getBookById(bookId)
        }
    }

    fun loadHistory(bookId: Int) {
        viewModelScope.launch {
            repository.getHistoryForBook(bookId).observeForever { history ->
                _readingHistory.value = history
            }
        }
    }

    fun updateBook(book: Book, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateBook(book)

            // Adicionar ao histórico
            val history = ReadingHistory(
                livroId = book.id,
                dataRegistro = System.currentTimeMillis(),
                paginaAtual = book.paginaAtual,
                percentualConcluido = book.calcularProgresso()
            )
            repository.insertHistory(history)

            onSuccess()
        }
    }

    fun deleteBook(book: Book, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteBook(book)
            onSuccess()
        }
    }
}