package com.ua.bookcatalog.ui;

import com.ua.bookcatalog.R;
import com.ua.bookcatalog.data.Book;
import com.ua.bookcatalog.data.BookDao;
import com.ua.bookcatalog.data.BookDatabase;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditBookActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etAuthor, etYear, etGenre, etPages;
    private RatingBar ratingBar;
    private Button btnSave;

    private BookDatabase db;
    private BookDao bookDao;

    private int bookId = -1;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        db = BookDatabase.getInstance(this);
        bookDao = db.bookDao();

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etYear = findViewById(R.id.etYear);
        etGenre = findViewById(R.id.etGenre);
        etPages = findViewById(R.id.etPages);
        ratingBar = findViewById(R.id.ratingBar);
        btnSave = findViewById(R.id.btnSave);

        if (getIntent().hasExtra("book_id")) {
            bookId = getIntent().getIntExtra("book_id", -1);
            if (bookId != -1) {
                toolbar.setTitle("Edit Book");
                loadBookData(bookId);
            }
        }

        btnSave.setOnClickListener(v -> saveBook());
    }

    private void loadBookData(int id) {
        Book book = bookDao.getBookById(id);
        if (book != null) {
            etTitle.setText(book.getTitle());
            etAuthor.setText(book.getAuthor());
            etYear.setText(String.valueOf(book.getYear()));
            etGenre.setText(book.getGenre());
            etPages.setText(String.valueOf(book.getPages()));
            ratingBar.setRating(book.getRating());
            isFavorite = book.isFavorite();
        }
    }

    private void saveBook() {
        String title = getTextFrom(etTitle);
        String author = getTextFrom(etAuthor);
        String yearStr = getTextFrom(etYear);
        String genre = getTextFrom(etGenre);
        String pagesStr = getTextFrom(etPages);
        float rating = ratingBar.getRating();

        if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty()
                || genre.isEmpty() || pagesStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int year;
        int pages;
        try {
            year = Integer.parseInt(yearStr);
            pages = Integer.parseInt(pagesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Year and number of pages must be numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        Book book = new Book(title, author, year, genre, pages);
        book.setRating(rating);
        book.setFavorite(isFavorite);

        if (bookId == -1) {
            bookDao.insert(book);
            Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show();
        } else {
            book.setId(bookId);
            bookDao.update(book);
            Toast.makeText(this, "Book updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private String getTextFrom(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
