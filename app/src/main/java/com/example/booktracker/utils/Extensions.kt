package com.example.booktracker.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension Functions para o projeto Book Tracker
 */

/**
 * Converte timestamp (Long) para String no formato dd/MM/yyyy
 * Uso: book.dataInicio?.toDateString()
 */
fun Long.toDateString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Converte código de status (Int) para texto legível
 * 0 → "Quero Ler"
 * 1 → "Lendo"
 * 2 → "Lido"
 */
fun Int.toReadingStatus(): String {
    return when (this) {
        0 -> "Quero Ler"
        1 -> "Lendo"
        2 -> "Lido"
        else -> "Desconhecido"
    }
}

/**
 * Converte String de status para formato legível
 * "quero_ler" → "Quero Ler"
 * "lendo" → "Lendo"
 * "lido" → "Lido"
 */
fun String.toReadableStatus(): String {
    return when (this) {
        "quero_ler" -> "Quero Ler"
        "lendo" -> "Lendo"
        "lido" -> "Lido"
        else -> "Desconhecido"
    }
}