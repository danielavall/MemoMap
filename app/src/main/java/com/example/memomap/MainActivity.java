package com.example.memomap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.memomap.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null); // Bisa tetap ada, meski visibilitas dikontrol dari BottomAppBar

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
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Metode untuk menampilkan BottomAppBar dan FAB
    public void showBottomNav() {
        if (binding != null) {
            if (binding.bottomAppBar != null) {
                binding.bottomAppBar.setVisibility(View.VISIBLE);
            }
            // >>> TAMBAHKAN KODE INI UNTUK MENAMPILKAN FAB <<<
            if (binding.fabJournal != null) { // fab_journal adalah ID baru untuk FAB Anda
                binding.fabJournal.setVisibility(View.VISIBLE);
            }
        }
    }

    // Metode untuk menyembunyikan BottomAppBar dan FAB
    public void hideBottomNav() {
        if (binding != null) {
            if (binding.bottomAppBar != null) {
                binding.bottomAppBar.setVisibility(View.GONE);
            }
            // >>> TAMBAHKAN KODE INI UNTUK MENYEMBUNYIKAN FAB <<<
            if (binding.fabJournal != null) { // fab_journal adalah ID baru untuk FAB Anda
                binding.fabJournal.setVisibility(View.GONE);
            }
        }
    }
}