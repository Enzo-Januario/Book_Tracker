package com.example.booktracker.ui.add

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.booktracker.R
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.data.local.entity.Category
import com.example.booktracker.databinding.ActivityAddBookBinding

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding
    private val viewModel: AddBookViewModel by viewModels()

    private var categories: List<Category> = emptyList()
    private var isEditMode = false
    private var bookId: Int = -1

    // Dados da API
    private var fromApi = false
    private var apiId: String? = null
    private var coverUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar se veio da API
        fromApi = intent.getBooleanExtra("FROM_API", false)

        if (fromApi) {
            loadApiData()
        }

        setupToolbar()
        setupCategorySpinner()
        setupStatusSpinner()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (isEditMode) "Editar Livro" else "Adicionar Livro"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadApiData() {
        apiId = intent.getStringExtra("API_ID")
        val title = intent.getStringExtra("TITLE")
        val author = intent.getStringExtra("AUTHOR")
        val pages = intent.getIntExtra("PAGES", 0)
        val isbn = intent.getStringExtra("ISBN")
        coverUrl = intent.getStringExtra("COVER_URL")

        binding.apply {
            etTitle.setText(title)
            etAuthor.setText(author)
            etPages.setText(pages.toString())
            etIsbn.setText(isbn)

            // Preview da capa
            if (coverUrl != null) {
                ivCoverPreview.visibility = View.VISIBLE
                Glide.with(this@AddBookActivity)
                    .load(coverUrl)
                    .into(ivCoverPreview)
            }
        }
    }

    private fun setupCategorySpinner() {
        viewModel.allCategories.observe(this) { categoryList ->
            categories = categoryList

            val categoryNames = categoryList.map { it.nome }
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }
    }

    private fun setupStatusSpinner() {
        val statusList = listOf("Quero Ler", "Lendo", "Lido")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveBook()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveBook() {
        val title = binding.etTitle.text.toString().trim()
        val author = binding.etAuthor.text.toString().trim()
        val pagesStr = binding.etPages.text.toString().trim()
        val isbn = binding.etIsbn.text.toString().trim()

        // Validações
        if (title.isEmpty()) {
            binding.tilTitle.error = "Título é obrigatório"
            return
        }
        if (author.isEmpty()) {
            binding.tilAuthor.error = "Autor é obrigatório"
            return
        }
        if (pagesStr.isEmpty()) {
            binding.tilPages.error = "Número de páginas é obrigatório"
            return
        }

        val pages = pagesStr.toIntOrNull() ?: 0
        if (pages <= 0) {
            binding.tilPages.error = "Número de páginas inválido"
            return
        }

        val categoryPosition = binding.spinnerCategory.selectedItemPosition
        if (categoryPosition < 0 || categories.isEmpty()) {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = categories[categoryPosition].id
        val statusPosition = binding.spinnerStatus.selectedItemPosition
        val status = when (statusPosition) {
            0 -> "quero_ler"
            1 -> "lendo"
            2 -> "lido"
            else -> "quero_ler"
        }

        val book = Book(
            titulo = title,
            autor = author,
            numeroPaginas = pages,
            isbn = isbn.ifBlank { null },
            urlCapa = coverUrl,
            categoriaId = categoryId,
            status = status,
            apiId = apiId
        )

        viewModel.insertBook(
            book = book,
            onSuccess = {
                Toast.makeText(this, "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )
    }
}