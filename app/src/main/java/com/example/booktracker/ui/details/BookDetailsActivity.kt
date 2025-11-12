package com.example.booktracker.ui.details

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.booktracker.R
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.databinding.ActivityBookDetailsBinding

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    private var currentBook: Book? = null
    private var bookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getIntExtra("BOOK_ID", -1)
        if (bookId == -1) {
            finish()
            return
        }

        setupToolbar()
        setupStatusSpinner()
        setupListeners()
        observeViewModel()

        viewModel.loadBook(bookId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupStatusSpinner() {
        val statusList = listOf("Quero Ler", "Lendo", "Lido")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
    }

    private fun setupListeners() {
        binding.etCurrentPage.addTextChangedListener {
            updateProgress()
        }

        binding.btnSave.setOnClickListener {
            saveBook()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun observeViewModel() {
        viewModel.book.observe(this) { book ->
            if (book != null) {
                currentBook = book
                populateFields(book)
            }
        }
    }

    private fun populateFields(book: Book) {
        binding.apply {
            tvTitle.text = book.titulo
            tvAuthor.text = book.autor
            tvTotalPages.text = "/ ${book.numeroPaginas}"

            // Cover
            Glide.with(this@BookDetailsActivity)
                .load(book.urlCapa)
                .placeholder(R.drawable.ic_book_placeholder)
                .error(R.drawable.ic_book_placeholder)
                .into(ivCover)

            // Status
            val statusPosition = when (book.status) {
                "quero_ler" -> 0
                "lendo" -> 1
                "lido" -> 2
                else -> 0
            }
            spinnerStatus.setSelection(statusPosition)

            // Current Page
            etCurrentPage.setText(book.paginaAtual.toString())

            // Progress
            val progress = book.calcularProgresso().toInt()
            progressBar.progress = progress
            tvProgressPercent.text = "$progress%"

            // Rating
            ratingBar.rating = book.avaliacao?.toFloat() ?: 0f

            // Notes
            etNotes.setText(book.notasPessoais ?: "")
        }
    }

    private fun updateProgress() {
        val currentPage = binding.etCurrentPage.text.toString().toIntOrNull() ?: 0
        val totalPages = currentBook?.numeroPaginas ?: 1

        val progress = if (totalPages > 0) {
            ((currentPage.toFloat() / totalPages.toFloat()) * 100).toInt()
        } else 0

        binding.progressBar.progress = progress
        binding.tvProgressPercent.text = "$progress%"
    }

    private fun saveBook() {
        val book = currentBook ?: return

        val currentPage = binding.etCurrentPage.text.toString().toIntOrNull() ?: 0
        val statusPosition = binding.spinnerStatus.selectedItemPosition
        val status = when (statusPosition) {
            0 -> "quero_ler"
            1 -> "lendo"
            2 -> "lido"
            else -> "quero_ler"
        }
        val rating = binding.ratingBar.rating.toInt()
        val notes = binding.etNotes.text.toString()

        val updatedBook = book.copy(
            paginaAtual = currentPage,
            status = status,
            avaliacao = if (rating > 0) rating else null,
            notasPessoais = notes.ifBlank { null },
            dataInicio = if (status == "lendo" && book.dataInicio == null) System.currentTimeMillis() else book.dataInicio,
            dataTermino = if (status == "lido") System.currentTimeMillis() else null
        )

        viewModel.updateBook(updatedBook) {
            Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Livro")
            .setMessage("Tem certeza que deseja excluir este livro?")
            .setPositiveButton("Excluir") { _, _ ->
                deleteBook()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteBook() {
        val book = currentBook ?: return
        viewModel.deleteBook(book) {
            Toast.makeText(this, "Livro excluído!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}