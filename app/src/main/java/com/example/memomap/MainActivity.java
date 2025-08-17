package com.example.memomap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.ImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Import View

import com.example.memomap.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomappbar.BottomAppBar; // Import BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton; // Import FloatingActionButton

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    
    private BottomNavigationView bottomNavigationView;
    private BottomAppBar bottomAppBar; // Deklarasi BottomAppBar
    private FloatingActionButton fabJournal; // Deklarasi FloatingActionButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi semua elemen dari binding
        bottomNavigationView = binding.bottomNavigationView;
        bottomAppBar = binding.bottomAppBar; // Inisialisasi BottomAppBar
        fabJournal = binding.fabJournal; // Inisialisasi FloatingActionButton

        replaceFragment(new HomeFragment());
        // Mengatur background null untuk bottomNavigationView (ini sudah ada di kode Anda)
        binding.bottomNavigationView.setBackground(null);

        fabJournal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OneLineActivity.class);
            startActivity(intent);
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.goal) {
                replaceFragment(new GoalFragment());
            } else if (id == R.id.recap) {
                replaceFragment(new RecapFragment());
            } else if (id == R.id.suggest) {
                replaceFragment(new SuggestFragment());
            }
            return true;
        });


        boolean goHome = getIntent().getBooleanExtra("go_home", false);
        if (goHome) {
            replaceFragment(new HomeFragment());
        } else {
            replaceFragment(new HomeFragment()); // default
        }

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Metode untuk menyembunyikan seluruh Bottom Nav Area (BottomNavigationView, BottomAppBar, FAB)
    public void hideBottomNav() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
        if (bottomAppBar != null) { // Sembunyikan BottomAppBar
            bottomAppBar.setVisibility(View.GONE);
        }
        if (fabJournal != null) { // Sembunyikan FloatingActionButton
            fabJournal.setVisibility(View.GONE);
        }
    }

    // Metode untuk menampilkan seluruh Bottom Nav Area (BottomNavigationView, BottomAppBar, FAB)
    public void showBottomNav() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        if (bottomAppBar != null) { // Tampilkan BottomAppBar
            bottomAppBar.setVisibility(View.VISIBLE);
        }
        if (fabJournal != null) { // Tampilkan FloatingActionButton
            fabJournal.setVisibility(View.VISIBLE);
        }
    }


}