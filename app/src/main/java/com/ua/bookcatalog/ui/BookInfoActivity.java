package com.ua.bookcatalog.ui;

import com.ua.bookcatalog.R;
import com.ua.bookcatalog.data.Book;
import com.ua.bookcatalog.data.BookDao;
import com.ua.bookcatalog.data.BookDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class BookInfoActivity extends AppCompatActivity {

    private BookDatabase db;
    private BookDao bookDao;
    private int bookId = -1;
    private Book currentBook;

    private TextView tvTitle, tvAuthor, tvYear, tvGenre, tvPages;
    private RatingBar ratingBar;
    private ImageView ivFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        MaterialToolbar toolbar = findViewById(R.id.toolbarInfo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = BookDatabase.getInstance(this);
        bookDao = db.bookDao();

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvYear = findViewById(R.id.tvYear);
        tvGenre = findViewById(R.id.tvGenre);
        tvPages = findViewById(R.id.tvPages);
        ratingBar = findViewById(R.id.ratingBarInfo);
        ivFavorite = findViewById(R.id.ivFavorite);

        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);

        bookId = getIntent().getIntExtra("book_id", -1);

        btnEdit.setOnClickListener(v -> {
            if (bookId != -1) {
                Intent intent = new Intent(BookInfoActivity.this, AddEditBookActivity.class);
                intent.putExtra("book_id", bookId);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookId != -1) {
            loadBookDetails();
        } else {
            Toast.makeText(this, "Error loading book", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadBookDetails() {
        currentBook = bookDao.getBookById(bookId);
        if (currentBook != null) {
            tvTitle.setText(currentBook.getTitle());
            tvAuthor.setText(currentBook.getAuthor());
            tvYear.setText(String.valueOf(currentBook.getYear()));
            tvGenre.setText(currentBook.getGenre());
            tvPages.setText(String.valueOf(currentBook.getPages()));
            ratingBar.setRating(currentBook.getRating());

            ivFavorite.setImageResource(
                    currentBook.isFavorite()
                            ? android.R.drawable.btn_star_big_on
                            : android.R.drawable.btn_star_big_off
            );
        } else {
            finish();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete '" + currentBook.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    bookDao.delete(currentBook);
                    Toast.makeText(this, "Book deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
