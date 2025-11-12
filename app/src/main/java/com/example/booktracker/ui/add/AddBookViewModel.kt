package com.example.booktracker.ui.add

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

class AddBookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository
    val allCategories: LiveData<List<Category>>

    init {
        val database = BookTrackerDatabase.getDatabase(application)
        repository = BookRepository(
            database.bookDao(),
            database.categoryDao(),
            database.readingHistoryDao(),
            RetrofitClient.googleBooksApi
        )
        allCategories = repository.allCategories
    }

    fun insertBook(book: Book, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar se já existe livro com mesmo ISBN
                book.isbn?.let { isbn ->
                    val existingBook = repository.getBookByIsbn(isbn)
                    if (existingBook != null) {
                        onError("Este livro já está na sua biblioteca!")
                        return@launch
                    }
                }

                repository.insertBook(book)
                onSuccess()
            } catch (e: Exception) {
                onError("Erro ao salvar: ${e.message}")
            }
        }
    }

    fun updateBook(book: Book, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateBook(book)
                onSuccess()
            } catch (e: Exception) {
                onError("Erro ao atualizar: ${e.message}")
            }
        }
    }
}