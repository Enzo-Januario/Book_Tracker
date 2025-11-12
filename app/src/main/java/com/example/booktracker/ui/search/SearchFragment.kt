package com.example.booktracker.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booktracker.databinding.FragmentSearchBinding
import com.example.booktracker.ui.adapter.SearchResultAdapter
import com.example.booktracker.ui.add.AddBookActivity

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchResultAdapter { bookItem ->
            // Navegar para adicionar livro com dados da API
            val intent = Intent(requireContext(), AddBookActivity::class.java)
            intent.putExtra("FROM_API", true)
            intent.putExtra("API_ID", bookItem.id)
            intent.putExtra("TITLE", bookItem.volumeInfo.title)
            intent.putExtra("AUTHOR", bookItem.volumeInfo.authors?.joinToString(", "))
            intent.putExtra("PAGES", bookItem.volumeInfo.pageCount ?: 0)
            intent.putExtra("ISBN", bookItem.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier)
            intent.putExtra("COVER_URL", bookItem.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"))
            startActivity(intent)
        }

        binding.recyclerSearchResults.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.tilSearch.setEndIconOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchBooks(query)
            hideKeyboard()
        } else {
            Toast.makeText(requireContext(), "Digite algo para buscar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (results.isNotEmpty()) {
                binding.recyclerSearchResults.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                searchAdapter.submitList(results)
            } else {
                binding.recyclerSearchResults.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.emptyState.visibility = View.VISIBLE
                binding.tvMessage.text = error
                binding.recyclerSearchResults.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}