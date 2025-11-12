package com.example.booktracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktracker.R
import com.example.booktracker.data.remote.model.BookItem
import com.example.booktracker.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onBookClick: (BookItem) -> Unit
) : ListAdapter<BookItem, SearchResultAdapter.SearchViewHolder>(SearchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookItem: BookItem) {
            binding.apply {
                tvTitle.text = bookItem.volumeInfo.title ?: "Título desconhecido"
                tvAuthor.text = bookItem.volumeInfo.authors?.joinToString(", ") ?: "Autor desconhecido"

                val pages = bookItem.volumeInfo.pageCount
                tvPages.text = if (pages != null) "$pages páginas" else "Páginas desconhecidas"

                // Cover
                val thumbnailUrl = bookItem.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
                Glide.with(itemView.context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(ivCover)

                root.setOnClickListener {
                    onBookClick(bookItem)
                }
            }
        }
    }

    class SearchDiffCallback : DiffUtil.ItemCallback<BookItem>() {
        override fun areItemsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
            return oldItem == newItem
        }
    }
}