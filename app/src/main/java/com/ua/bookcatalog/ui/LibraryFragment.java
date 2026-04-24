package com.ua.bookcatalog.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ua.bookcatalog.R;
import com.ua.bookcatalog.data.Book;
import com.ua.bookcatalog.data.BookDao;
import com.ua.bookcatalog.data.BookDatabase;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class LibraryFragment extends Fragment {

    private BookDatabase db;
    private BookDao bookDao;
    private BookAdapter adapter;
    private RecyclerView rvBooks;
    private LinearLayout layoutEmpty;
    private TextView tvBookCount, tvTotalPages;
    private TextInputEditText etSearch;

    private int currentSortMode = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        db = BookDatabase.getInstance(requireContext());
        bookDao = db.bookDao();

        rvBooks = view.findViewById(R.id.rvBooks);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvBookCount = view.findViewById(R.id.tvBookCount);
        tvTotalPages = view.findViewById(R.id.tvTotalPages);
        etSearch = view.findViewById(R.id.etSearch);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        ImageButton btnSort = view.findViewById(R.id.btnSort);

        setupRecyclerView();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditBookActivity.class);
            startActivity(intent);
        });

        btnSort.setOnClickListener(this::showSortMenu);

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
        updateStats();
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter();
        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        SlideInUpAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        animator.setRemoveDuration(300);
        rvBooks.setItemAnimator(animator);
        rvBooks.setAdapter(adapter);
        adapter.setOnBookClickListener(new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                Intent intent = new Intent(requireContext(), BookInfoActivity.class);
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

    private void showSortMenu(View view) {
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_title) {
                currentSortMode = 0;
                loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
                return true;
            } else if (id == R.id.sort_year) {
                currentSortMode = 1;
                loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
                return true;
            } else if (id == R.id.sort_rating) {
                currentSortMode = 2;
                loadBooks(etSearch.getText() != null ? etSearch.getText().toString() : "");
                return true;
            } else if (id == R.id.fill_test) {
                fillTestData();
                return true;
            } else if (id == R.id.clear_all) {
                bookDao.deleteAll();
                onResume();
                Toast.makeText(requireContext(), "All books deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
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
            Toast.makeText(requireContext(), "Test data filled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Database is not empty", Toast.LENGTH_SHORT).show();
        }
    }
}
