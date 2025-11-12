package com.example.booktracker.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.booktracker.data.local.database.BookTrackerDatabase
import com.example.booktracker.data.remote.api.RetrofitClient
import com.example.booktracker.data.remote.model.BookItem
import com.example.booktracker.data.repository.BookRepository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    private val _searchResults = MutableLiveData<List<BookItem>>()
    val searchResults: LiveData<List<BookItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        val database = BookTrackerDatabase.getDatabase(application)
        repository = BookRepository(
            database.bookDao(),
            database.categoryDao(),
            database.readingHistoryDao(),
            RetrofitClient.googleBooksApi
        )
    }

    fun searchBooks(query: String) {
        if (query.isBlank()) {
            _errorMessage.value = "Digite algo para buscar"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.searchBooks(query)
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    _searchResults.value = books
                    if (books.isEmpty()) {
                        _errorMessage.value = "Nenhum livro encontrado"
                    }
                } else {
                    _errorMessage.value = "Erro ao buscar: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro de conexão: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getBookDetails(volumeId: String, onSuccess: (BookItem) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getBookDetails(volumeId)
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Erro ao carregar detalhes")
                }
            } catch (e: Exception) {
                onError("Erro: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
        _errorMessage.value = null
    }
}