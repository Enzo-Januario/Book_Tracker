package com.example.booktracker.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.booktracker.data.local.entity.ReadingHistory

@Dao
interface ReadingHistoryDao {

    @Insert
    suspend fun insert(history: ReadingHistory)

    @Query("SELECT * FROM reading_history WHERE livroId = :bookId ORDER BY dataRegistro DESC")
    fun getHistoryForBook(bookId: Int): LiveData<List<ReadingHistory>>

    @Query("DELETE FROM reading_history WHERE livroId = :bookId")
    suspend fun deleteHistoryForBook(bookId: Int)

    @Query("SELECT * FROM reading_history ORDER BY dataRegistro DESC LIMIT 10")
    fun getRecentHistory(): LiveData<List<ReadingHistory>>
}