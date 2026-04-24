package com.ua.bookcatalog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etAuthor, etYear, etGenre, etPages;
    private Button btnShowInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etYear = findViewById(R.id.etYear);
        etGenre = findViewById(R.id.etGenre);
        etPages = findViewById(R.id.etPages);
        btnShowInfo = findViewById(R.id.btnShowInfo);

        btnShowInfo.setOnClickListener(v -> showBookInfo());
    }

    private void showBookInfo() {
        String title = getTextFrom(etTitle);
        String author = getTextFrom(etAuthor);
        String yearStr = getTextFrom(etYear);
        String genre = getTextFrom(etGenre);
        String pagesStr = getTextFrom(etPages);

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

        Intent intent = new Intent(MainActivity.this, BookInfoActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    private String getTextFrom(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_clear) {
            clearForm();
            return true;
        } else if (id == R.id.menu_fill_test) {
            fillTestData();
            return true;
        } else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearForm() {
        etTitle.setText("");
        etAuthor.setText("");
        etYear.setText("");
        etGenre.setText("");
        etPages.setText("");
        Toast.makeText(this, "Form cleared", Toast.LENGTH_SHORT).show();
    }

    private void fillTestData() {
        etTitle.setText("The Great Gatsby");
        etAuthor.setText("F. Scott Fitzgerald");
        etYear.setText("1925");
        etGenre.setText("Novel");
        etPages.setText("180");
        Toast.makeText(this, "Test data filled", Toast.LENGTH_SHORT).show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("BookCatalog v1.0\n\nAn app for viewing book information.")
                .setPositiveButton("OK", null)
                .show();
    }
}