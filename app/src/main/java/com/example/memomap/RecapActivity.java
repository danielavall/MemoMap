package com.example.memomap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton; // Pastikan ini diimport jika Anda punya tombol back di Activity
import com.google.android.material.bottomnavigation.BottomNavigationView; // >>> PENTING: Pastikan import ini ada

public class RecapActivity extends AppCompatActivity {

    private ImageButton backButton; // Jika Anda punya tombol back di Activity
    private BottomNavigationView bottomNavigationView; // >>> PENTING: Deklarasikan BottomNavigationView Anda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap);

        backButton = findViewById(R.id.backButton); // Inisialisasi tombol back Activity
        // >>> PENTING: INISIALISASI BottomNavigationView ANDA SESUAI ID DI activity_recap.xml
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setel OnClickListener untuk tombol kembali Activity
        // Pastikan R.id.backButton ada di activity_recap.xml jika Anda menggunakan ini
        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }

        // Ini adalah cara menempatkan fragment di Activity.
        // Pastikan R.id.fragmentContainer ada di activity_recap.xml.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new RecapFragment())
                    .commit();
        }
    }

    // Metode untuk menampilkan tombol kembali di Activity (bukan di card)
    public void showBackButton() {
        if (backButton != null) {
            backButton.setVisibility(View.VISIBLE);
        }
    }

    // Metode untuk menyembunyikan tombol kembali di Activity (bukan di card)
    public void hideBackButton() {
        if (backButton != null) {
            backButton.setVisibility(View.GONE);
        }
    }

    // >>> METODE BARU UNTUK MENAMPILKAN BOTTOM NAVBAR <<<
    public void showBottomNav() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    // >>> METODE BARU UNTUK MENYEMBUNYIKAN BOTTOM NAVBAR <<<
    public void hideBottomNav() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }
}