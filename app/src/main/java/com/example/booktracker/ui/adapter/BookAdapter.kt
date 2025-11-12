package com.example.booktracker.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booktracker.R
import com.example.booktracker.data.local.entity.Book
import com.example.booktracker.databinding.ItemBookBinding

class BookAdapter(
    private val onBookClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(
        private val binding: ItemBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.apply {
                tvTitle.text = book.titulo
                tvAuthor.text = book.autor

                // Status
                chipStatus.text = when (book.status) {
                    "quero_ler" -> "Quero Ler"
                    "lendo" -> "Lendo"
                    "lido" -> "Lido"
                    else -> "Desconhecido"
                }

                // Status Color
                val statusColor = when (book.status) {
                    "quero_ler" -> Color.parseColor("#FF9800")
                    "lendo" -> Color.parseColor("#2196F3")
                    "lido" -> Color.parseColor("#4CAF50")
                    else -> Color.GRAY
                }
                chipStatus.setChipBackgroundColorResource(android.R.color.transparent)
                chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(statusColor)

                // Progress
                val progress = book.calcularProgresso().toInt()
                progressReading.progress = progress
                tvProgress.text = "$progress%"

                // Show progress only if reading
                if (book.status == "lendo") {
                    progressReading.visibility = View.VISIBLE
                    tvProgress.visibility = View.VISIBLE
                } else {
                    progressReading.visibility = View.GONE
                    tvProgress.visibility = View.GONE
                }

                // Rating
                if (book.avaliacao != null && book.avaliacao > 0) {
                    ratingBar.visibility = View.VISIBLE
                    ratingBar.rating = book.avaliacao.toFloat()
                } else {
                    ratingBar.visibility = View.GONE
                }

                // Cover Image
                Glide.with(itemView.context)
                    .load(book.urlCapa)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(ivBookCover)

                // Click
                root.setOnClickListener {
                    onBookClick(book)
                }
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}