package com.ua.bookcatalog.ui;

import com.ua.bookcatalog.R;
import com.ua.bookcatalog.data.Book;
import com.ua.bookcatalog.data.BookDao;
import com.ua.bookcatalog.data.BookDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookDatabase db;
    private BookDao bookDao;
    private BookAdapter adapter;

    private RecyclerView rvBooks;
    private LinearLayout layoutEmpty;
    private TextView tvBookCount, tvTotalPages;
    private TextInputEditText etSearch;

    private int currentSortMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = BookDatabase.getInstance(this);
        bookDao = db.bookDao();

        rvBooks = findViewById(R.id.rvBooks);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvBookCount = findViewById(R.id.tvBookCount);
        tvTotalPages = findViewById(R.id.tvTotalPages);
        etSearch = findViewById(R.id.etSearch);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        setupRecyclerView();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditBookActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
        updateStats();
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter();
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.setAdapter(adapter);

        adapter.setOnBookClickListener(new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                Intent intent = new Intent(MainActivity.this, BookInfoActivity.class);
                intent.putExtra("book_id", book.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Book book, int position) {
                book.setFavorite(!book.isFavorite());
                bookDao.update(book);
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void loadBooks(String query) {
        List<Book> books;
        if (query.trim().isEmpty()) {
            if (currentSortMode == 0) {
                books = bookDao.getAllBooksByTitle();
            } else if (currentSortMode == 1) {
                books = bookDao.getAllBooksByYear();
            } else {
                books = bookDao.getAllBooksByRating();
            }
        } else {
            books = bookDao.searchBooks(query.trim());
        }

        adapter.setBooks(books);

        if (books.isEmpty() && query.trim().isEmpty()) {
            rvBooks.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvBooks.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void updateStats() {
        int count = bookDao.getBookCount();
        int pages = bookDao.getTotalPages();
        tvBookCount.setText(String.valueOf(count));
        tvTotalPages.setText(String.valueOf(pages));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_sort_title) {
            currentSortMode = 0;
            loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
            return true;
        } else if (id == R.id.menu_sort_year) {
            currentSortMode = 1;
            loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
            return true;
        } else if (id == R.id.menu_sort_rating) {
            currentSortMode = 2;
            loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
            return true;
        } else if (id == R.id.menu_fill_test) {
            fillTestData();
            return true;
        } else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        } else if (id == R.id.menu_clear_all) {
            bookDao.deleteAll();
            onResume();
            Toast.makeText(this, "All books deleted", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillTestData() {
        if (bookDao.getBookCount() == 0) {
            bookDao.insert(new Book("The Great Gatsby", "F. Scott Fitzgerald", 1925, "Novel", 180));
            bookDao.insert(new Book("1984", "George Orwell", 1949, "Dystopian", 328));
            bookDao.insert(new Book("To Kill a Mockingbird", "Harper Lee", 1960, "Southern Gothic", 281));
            Book lotr = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954, "Fantasy", 1178);
            lotr.setRating(5f);
            lotr.setFavorite(true);
            bookDao.insert(lotr);
            Book hobbit = new Book("The Hobbit", "J.R.R. Tolkien", 1937, "Fantasy", 310);
            hobbit.setRating(4.5f);
            bookDao.insert(hobbit);
            onResume();
            Toast.makeText(this, "Test data filled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Database is not empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("BookCatalog v2.0\n\nA modern library management app.")
                .setPositiveButton("OK", null)
                .show();
    }
}