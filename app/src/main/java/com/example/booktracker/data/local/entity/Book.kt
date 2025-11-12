package com.example.booktracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoriaId"), Index(value = ["isbn"], unique = true)]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val autor: String,
    val numeroPaginas: Int,
    val isbn: String? = null,
    val urlCapa: String? = null,
    val categoriaId: Int,
    val status: String, // "quero_ler", "lendo", "lido"
    val paginaAtual: Int = 0,
    val dataInicio: Long? = null,
    val dataTermino: Long? = null,
    val avaliacao: Int? = null, // 1 a 5 estrelas
    val notasPessoais: String? = null,
    val apiId: String? = null // ID na Google Books API
) {
    fun calcularProgresso(): Float {
        return if (numeroPaginas > 0) {
            (paginaAtual.toFloat() / numeroPaginas.toFloat()) * 100
        } else 0f
    }
}