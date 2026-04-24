package com.ua.bookcatalog;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Book book = getIntent().getParcelableExtra("book");

        if (book != null) {
            TextView tvTitle = findViewById(R.id.tvTitle);
            TextView tvAuthor = findViewById(R.id.tvAuthor);
            TextView tvYear = findViewById(R.id.tvYear);
            TextView tvGenre = findViewById(R.id.tvGenre);
            TextView tvPages = findViewById(R.id.tvPages);

            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvYear.setText(String.valueOf(book.getYear()));
            tvGenre.setText(book.getGenre());
            tvPages.setText(String.valueOf(book.getPages()));
        }

        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> finish());
    }
}
