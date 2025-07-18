package com.example.memomap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.memomap.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Cek apakah ada navigasi dari intent
        String navigateTo = getIntent().getStringExtra("navigate_to");

        if ("home".equals(navigateTo)) {
            replaceFragment(new HomeFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.home); // sesuaikan dengan ID item di nav
        } else {
            replaceFragment(new HomeFragment()); // default saat pertama buka
        }

        binding.bottomNavigationView.setBackground(null);
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
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment); // pastikan ID ini sesuai dengan layout kamu
        transaction.commit();
    }
}
