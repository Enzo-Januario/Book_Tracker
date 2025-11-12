package com.example.booktracker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booktracker.R
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.data.local.entity.Category
import com.example.booktracker.databinding.FragmentHomeBinding
import com.example.booktracker.ui.adapter.BookAdapter
import com.example.booktracker.ui.details.BookDetailsActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    private var allBooks: List<Book> = emptyList()
    private var categories: List<Category> = emptyList()
    private var selectedStatus: String = "all"
    private var selectedCategoryId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChipGroup()
        setupCategorySpinner()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book ->
            // Navegar para detalhes
            val intent = Intent(requireContext(), BookDetailsActivity::class.java)
            intent.putExtra("BOOK_ID", book.id)
            startActivity(intent)
        }

        binding.recyclerBooks.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupChipGroup() {
        binding.chipGroupStatus.setOnCheckedChangeListener { _, checkedId ->
            selectedStatus = when (checkedId) {
                R.id.chip_quero_ler -> "quero_ler"
                R.id.chip_lendo -> "lendo"
                R.id.chip_lido -> "lido"
                else -> "all"
            }
            applyFilters()
        }
    }

    private fun setupCategorySpinner() {
        viewModel.allCategories.observe(viewLifecycleOwner) { categoryList ->
            categories = categoryList

            val categoryNames = mutableListOf("Todas as categorias")
            categoryNames.addAll(categoryList.map { it.nome })

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerCategory.adapter = adapter
            binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCategoryId = if (position == 0) null else categories[position - 1].id
                    applyFilters()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Não fazer nada
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.allBooks.observe(viewLifecycleOwner) { books ->
            allBooks = books
            applyFilters()
        }
    }

    private fun applyFilters() {
        var filteredBooks = allBooks

        // Filtrar por status
        if (selectedStatus != "all") {
            filteredBooks = filteredBooks.filter { it.status == selectedStatus }
        }

        // Filtrar por categoria
        selectedCategoryId?.let { categoryId ->
            filteredBooks = filteredBooks.filter { it.categoriaId == categoryId }
        }

        // Atualizar RecyclerView
        updateBookList(filteredBooks)
    }

    private fun updateBookList(books: List<Book>) {
        if (books.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.recyclerBooks.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.recyclerBooks.visibility = View.VISIBLE
            bookAdapter.submitList(books)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarregar dados quando voltar para a tela
        viewModel.allBooks.value?.let {
            allBooks = it
            applyFilters()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}