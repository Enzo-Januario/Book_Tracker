package com.example.booktracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.booktracker.data.local.dao.BookDao
import com.example.booktracker.data.local.dao.CategoryDao
import com.example.booktracker.data.local.dao.ReadingHistoryDao
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.data.local.entity.Category
import com.example.booktracker.data.local.entity.ReadingHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Category::class, Book::class, ReadingHistory::class],
    version = 1,
    exportSchema = false
)
abstract class BookTrackerDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun bookDao(): BookDao
    abstract fun readingHistoryDao(): ReadingHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: BookTrackerDatabase? = null

        fun getDatabase(context: Context): BookTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookTrackerDatabase::class.java,
                    "book_tracker_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.categoryDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao) {
            // Categorias pré-cadastradas
            val categories = listOf(
                Category(nome = "Ficção", cor = "#E91E63"),
                Category(nome = "Não-ficção", cor = "#9C27B0"),
                Category(nome = "Técnico", cor = "#3F51B5"),
                Category(nome = "Biografia", cor = "#2196F3"),
                Category(nome = "Romance", cor = "#F44336"),
                Category(nome = "Fantasia", cor = "#9C27B0"),
                Category(nome = "Suspense", cor = "#607D8B"),
                Category(nome = "História", cor = "#795548"),
                Category(nome = "Autoajuda", cor = "#FF9800"),
                Category(nome = "Outros", cor = "#9E9E9E")
            )
            categoryDao.insertAll(categories)
        }
    }
}