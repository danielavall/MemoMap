// app/src/main/java/com/example/memomap/GoalFragment.java
package com.example.memomap; // Pastikan package ini sesuai dengan package proyekmu

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Import kelas Goal dan GoalAdapter yang sudah kamu buat
import com.example.memomap.Goal;
import com.example.memomap.GoalAdapter;

import java.util.ArrayList;
import java.util.List;

public class GoalFragment extends Fragment {

    // Variabel untuk RecyclerView dan Adapter
    private RecyclerView goalRecyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> goalList; // Daftar untuk menyimpan objek Goal

    public GoalFragment() {
        // Required empty public constructor
    }

    // Kamu bisa biarkan metode newInstance dan ARG_PARAM1/ARG_PARAM2 jika kamu butuh
    // untuk melewati argumen ke fragment ini di masa depan.
    // Untuk tujuan RecyclerView, mereka tidak langsung digunakan.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public static GoalFragment newInstance(String param1, String param2) {
        GoalFragment fragment = new GoalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment (pastikan ini merujuk ke fragmen_goal.xml)
        View view = inflater.inflate(R.layout.fragment_goal, container, false); // Pastikan nama layoutnya benar: fragmen_goal

        // --- Inisialisasi RecyclerView ---
        goalRecyclerView = view.findViewById(R.id.goal_recycler_view);

        // Atur LayoutManager untuk RecyclerView
        // LinearLayoutManager akan menampilkan item dalam daftar vertikal
        goalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- Buat Data Dummy untuk Contoh ---
        goalList = new ArrayList<>();
        goalList.add(new Goal("Belajar Android Jetpack Compose", "Task", false)); // Contoh Task
        goalList.add(new Goal("Lari Pagi 30 Menit", "Task", true)); // Contoh Task
        goalList.add(new Goal("Baca Buku 'Atomic Habits'", "Goal", false)); // Contoh Goal
        goalList.add(new Goal("Selesaikan Laporan Bulanan", "Goal", false)); // Contoh Goal
        goalList.add(new Goal("Telepon Orang Tua", "Task", false)); // Contoh Task
        goalList.add(new Goal("Menyiram Tanaman", "Goal", true)); // Contoh Goal
        // Kamu bisa tambahkan lebih banyak objek Goal di sini
        // Data ini nantinya bisa kamu ambil dari database lokal (Room), API, atau sumber lain

        // --- Inisialisasi dan Atur Adapter ke RecyclerView ---
        goalAdapter = new GoalAdapter(goalList);
        goalRecyclerView.setAdapter(goalAdapter);

        // Jika kamu ingin menambahkan fungsionalitas untuk FloatingActionButton:
        // FloatingActionButton addGoalsButton = view.findViewById(R.id.addGoalsButton);
        // addGoalsButton.setOnClickListener(v -> {
        //     // Tulis kode di sini untuk menangani klik tombol tambah tujuan
        //     // Contoh: Buka dialog atau fragment baru untuk menambahkan tujuan
        //     Toast.makeText(getContext(), "Tombol Tambah Tujuan Diklik!", Toast.LENGTH_SHORT).show();
        // });

        return view;
    }
}