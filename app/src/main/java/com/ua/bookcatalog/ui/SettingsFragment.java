package com.ua.bookcatalog.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ua.bookcatalog.R;
import com.ua.bookcatalog.data.Book;
import com.ua.bookcatalog.data.BookDao;
import com.ua.bookcatalog.data.BookDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;

public class SettingsFragment extends Fragment {

    private BookDao bookDao;
    private Gson gson;

    private ActivityResultLauncher<Intent> exportLauncher;
    private ActivityResultLauncher<Intent> importLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        bookDao = BookDatabase.getInstance(requireContext()).bookDao();
        gson = new Gson();

        MaterialButton btnExport = view.findViewById(R.id.btnExport);
        MaterialButton btnImport = view.findViewById(R.id.btnImport);

        setupLaunchers();

        btnExport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "bookcatalog_backup.json");
            exportLauncher.launch(intent);
        });

        btnImport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            importLauncher.launch(intent);
        });

        return view;
    }

    private void setupLaunchers() {
        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            exportDataToUri(uri);
                        }
                    }
                }
        );

        importLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            importDataFromUri(uri);
                        }
                    }
                }
        );
    }

    private void exportDataToUri(Uri uri) {
        try {
            List<Book> books = bookDao.getAllBooksByTitle();
            String jsonString = gson.toJson(books);

            OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(jsonString.getBytes());
                outputStream.close();
                Toast.makeText(requireContext(), "Export successful", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void importDataFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();

                Type listType = new TypeToken<List<Book>>(){}.getType();
                List<Book> importedBooks = gson.fromJson(stringBuilder.toString(), listType);

                if (importedBooks != null && !importedBooks.isEmpty()) {
                    for (Book book : importedBooks) {
                        book.setId(0);
                        bookDao.insert(book);
                    }
                    Toast.makeText(requireContext(), "Import successful: " + importedBooks.size() + " books added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "File is empty or invalid", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Import failed", Toast.LENGTH_SHORT).show();
        }
    }
}
