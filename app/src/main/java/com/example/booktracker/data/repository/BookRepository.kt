package com.example.booktracker.data.repository

import androidx.lifecycle.LiveData
import com.example.booktracker.data.local.dao.BookDao
import com.example.booktracker.data.local.dao.CategoryDao
import com.example.booktracker.data.local.dao.ReadingHistoryDao
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.data.local.entity.Category
import com.example.booktracker.data.local.entity.ReadingHistory
import com.example.booktracker.data.remote.api.GoogleBooksApiService
import com.example.booktracker.data.remote.model.BookItem
import com.example.booktracker.data.remote.model.GoogleBooksResponse
import retrofit2.Response

class BookRepository(
    private val bookDao: BookDao,
    private val categoryDao: CategoryDao,
    private val historyDao: ReadingHistoryDao,
    private val googleBooksApi: GoogleBooksApiService
) {

    // ===== BOOKS =====
    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()

    suspend fun insertBook(book: Book): Long = bookDao.insert(book)

    suspend fun updateBook(book: Book) = bookDao.update(book)

    suspend fun deleteBook(book: Book) = bookDao.delete(book)

    suspend fun getBookById(id: Int): Book? = bookDao.getBookById(id)

    fun getBooksByStatus(status: String): LiveData<List<Book>> = bookDao.getBooksByStatus(status)

    fun getBooksByCategory(categoryId: Int): LiveData<List<Book>> = bookDao.getBooksByCategory(categoryId)

    fun getBooksByStatusAndCategory(status: String, categoryId: Int): LiveData<List<Book>> =
        bookDao.getBooksByStatusAndCategory(status, categoryId)

    suspend fun getReadBooksCount(): Int = bookDao.getReadBooksCount()

    suspend fun getTotalPagesRead(): Int = bookDao.getTotalPagesRead() ?: 0

    suspend fun getAverageRating(): Float = bookDao.getAverageRating() ?: 0f

    suspend fun getCurrentlyReadingCount(): Int = bookDao.getCurrentlyReadingCount()

    suspend fun getBookByIsbn(isbn: String): Book? = bookDao.getBookByIsbn(isbn)

    // ===== CATEGORIES =====
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category): Long = categoryDao.insert(category)

    suspend fun getCategoryById(id: Int): Category? = categoryDao.getCategoryById(id)

    suspend fun getBookCountInCategory(categoryId: Int): Int = categoryDao.getBookCountInCategory(categoryId)

    // ===== HISTORY =====
    suspend fun insertHistory(history: ReadingHistory) = historyDao.insert(history)

    fun getHistoryForBook(bookId: Int): LiveData<List<ReadingHistory>> = historyDao.getHistoryForBook(bookId)

    fun getRecentHistory(): LiveData<List<ReadingHistory>> = historyDao.getRecentHistory()

    // ===== API =====
    suspend fun searchBooks(query: String): Response<GoogleBooksResponse> {
        return googleBooksApi.searchBooks(query)
    }

    suspend fun getBookDetails(volumeId: String): Response<BookItem> {
        return googleBooksApi.getBookDetails(volumeId)
    }
}