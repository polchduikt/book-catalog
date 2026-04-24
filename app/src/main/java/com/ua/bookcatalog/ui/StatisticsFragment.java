package com.ua.bookcatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ua.bookcatalog.R;
import com.ua.bookcatalog.data.BookDao;
import com.ua.bookcatalog.data.BookDatabase;

import java.util.Locale;

public class StatisticsFragment extends Fragment {

    private BookDao bookDao;

    private TextView tvTotalBooks, tvFavorites, tvAvgRating, tvTopGenre, tvMaxPages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks);
        tvFavorites = view.findViewById(R.id.tvFavorites);
        tvAvgRating = view.findViewById(R.id.tvAvgRating);
        tvTopGenre = view.findViewById(R.id.tvTopGenre);
        tvMaxPages = view.findViewById(R.id.tvMaxPages);
        bookDao = BookDatabase.getInstance(requireContext()).bookDao();
        loadStatistics();
        return view;
    }

    private void loadStatistics() {
        int total = bookDao.getBookCount();
        tvTotalBooks.setText(String.valueOf(total));
        int favorites = bookDao.getFavoriteCount();
        tvFavorites.setText(String.valueOf(favorites));
        float avgRating = bookDao.getAverageRating();
        tvAvgRating.setText(String.format(Locale.getDefault(), "%.1f", avgRating));
        String topGenre = bookDao.getTopGenre();
        tvTopGenre.setText(topGenre != null && !topGenre.isEmpty() ? topGenre : "N/A");
        int maxPages = bookDao.getMaxPages();
        tvMaxPages.setText(String.valueOf(maxPages));
    }
}
