package com.example.booktracker.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.booktracker.data.local.entity.Book

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book): Long

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM books ORDER BY titulo ASC")
    fun getAllBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Int): Book?

    @Query("SELECT * FROM books WHERE status = :status ORDER BY titulo ASC")
    fun getBooksByStatus(status: String): LiveData<List<Book>>

    @Query("SELECT * FROM books WHERE categoriaId = :categoryId ORDER BY titulo ASC")
    fun getBooksByCategory(categoryId: Int): LiveData<List<Book>>

    @Query("SELECT * FROM books WHERE status = :status AND categoriaId = :categoryId ORDER BY titulo ASC")
    fun getBooksByStatusAndCategory(status: String, categoryId: Int): LiveData<List<Book>>

    @Query("SELECT COUNT(*) FROM books WHERE status = 'lido'")
    suspend fun getReadBooksCount(): Int

    @Query("SELECT SUM(numeroPaginas) FROM books WHERE status = 'lido'")
    suspend fun getTotalPagesRead(): Int?

    @Query("SELECT AVG(avaliacao) FROM books WHERE avaliacao IS NOT NULL")
    suspend fun getAverageRating(): Float?

    @Query("SELECT COUNT(*) FROM books WHERE status = 'lendo'")
    suspend fun getCurrentlyReadingCount(): Int

    @Query("SELECT * FROM books WHERE isbn = :isbn LIMIT 1")
    suspend fun getBookByIsbn(isbn: String): Book?
}