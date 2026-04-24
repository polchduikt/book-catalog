package com.ua.bookcatalog.ui;

import com.ua.bookcatalog.R;
import com.ua.bookcatalog.data.Book;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books = new ArrayList<>();
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
        void onFavoriteClick(Book book, int position);
    }

    public void setOnBookClickListener(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle, tvAuthor, tvYear, tvPages;
        private final Chip chipGenre;
        private final RatingBar ratingBar;
        private final ImageButton btnFavorite;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvYear = itemView.findViewById(R.id.tvBookYear);
            tvPages = itemView.findViewById(R.id.tvBookPages);
            chipGenre = itemView.findViewById(R.id.chipGenre);
            ratingBar = itemView.findViewById(R.id.ratingBarSmall);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onBookClick(books.get(getAdapterPosition()));
                }
            });

            btnFavorite.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(books.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }

        void bind(Book book) {
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvYear.setText(String.valueOf(book.getYear()));
            tvPages.setText(book.getPages() + " pages");
            chipGenre.setText(book.getGenre());
            ratingBar.setRating(book.getRating());

            if (book.isFavorite()) {
                btnFavorite.setColorFilter(itemView.getContext().getColor(R.color.hi_tech_secondary));
            } else {
                btnFavorite.setColorFilter(itemView.getContext().getColor(R.color.hi_tech_surface_variant));
            }
        }
    }
}
