package com.ua.bookcatalog.ui;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ua.bookcatalog.R;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;

    private final Fragment libraryFragment = new LibraryFragment();
    private final Fragment statisticsFragment = new StatisticsFragment();
    private final Fragment settingsFragment = new SettingsFragment();

    private Fragment activeFragment = libraryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNav = findViewById(R.id.bottom_navigation);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, settingsFragment, "3").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, statisticsFragment, "2").hide(statisticsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, libraryFragment, "1").commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_library) {
                fm.beginTransaction().hide(activeFragment).show(libraryFragment).commit();
                activeFragment = libraryFragment;
                toolbar.setTitle("Book Catalog");
                return true;
            } else if (id == R.id.nav_stats) {
                fm.beginTransaction().hide(activeFragment).show(statisticsFragment).commit();
                activeFragment = statisticsFragment;
                toolbar.setTitle("Statistics");
                return true;
            } else if (id == R.id.nav_settings) {
                fm.beginTransaction().hide(activeFragment).show(settingsFragment).commit();
                activeFragment = settingsFragment;
                toolbar.setTitle("Settings & Backup");
                return true;
            }
            return false;
        });
    }
}