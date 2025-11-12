package com.example.booktracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading_history",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["livroId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("livroId")]
)
data class ReadingHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val livroId: Int,
    val dataRegistro: Long,
    val paginaAtual: Int,
    val percentualConcluido: Float
)