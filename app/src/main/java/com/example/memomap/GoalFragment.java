package com.example.memomap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.button.MaterialButton; // Import ini jika kamu menggunakan MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButtonToggleGroup; // Import ini jika kamu menggunakan MaterialButtonToggleGroup

import java.util.ArrayList;
import java.util.Collections; // Untuk sorting
import java.util.Comparator; // Untuk sorting
import java.util.List;
// import java.util.stream.Collectors; // Tidak digunakan dalam implementasi ini

// Pastikan GoalFragment mengimplementasikan kedua interface
public class GoalFragment extends Fragment implements AddGoalDialogFragment.AddGoalDialogListener, GoalAdapter.OnGoalStatusChangeListener {

    private RecyclerView goalRecyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> allGoals; // Daftar lengkap semua goal
    private FloatingActionButton addGoalsButton;
    // Jika kamu kembali ke TabLayout (bukan MaterialButtonToggleGroup), gunakan ini:
    private TabLayout tabLayout;
    // Jika kamu menggunakan MaterialButtonToggleGroup (seperti diskusi sebelumnya untuk tampilan pil), gunakan ini:
    private MaterialButtonToggleGroup toggleButtonGroup;

    private String currentFilter = "All"; // Filter aktif saat ini, default "All"

    public GoalFragment() {
        // Required empty public constructor
    }

    // Metode newInstance, param1, param2 (disertakan untuk kelengkapan, tidak esensial untuk fitur ini)
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Inisialisasi data dummy Anda hanya sekali di onCreate
        allGoals = new ArrayList<>();
        allGoals.add(new Goal("Belajar Android Jetpack Compose", "Task", false));
        allGoals.add(new Goal("Lari Pagi 30 Menit", "Task", true)); // Contoh Task Selesai
        allGoals.add(new Goal("Baca Buku 'Atomic Habits'", "Goal", false));
        allGoals.add(new Goal("Selesaikan Laporan Bulanan", "Goal", false));
        allGoals.add(new Goal("Telepon Orang Tua", "Task", false));
        allGoals.add(new Goal("Menyiram Tanaman", "Goal", true)); // Contoh Goal Selesai
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        goalRecyclerView = view.findViewById(R.id.goal_recycler_view);
        goalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        goalAdapter = new GoalAdapter(new ArrayList<>());
        goalRecyclerView.setAdapter(goalAdapter);
        goalAdapter.setOnGoalStatusChangeListener(this); // Set listener untuk update status goal

        addGoalsButton = view.findViewById(R.id.addGoalsButton);
        addGoalsButton.setOnClickListener(v -> {
            AddGoalDialogFragment addGoalDialog = new AddGoalDialogFragment();
            addGoalDialog.setAddGoalDialogListener(this);
            addGoalDialog.show(getParentFragmentManager(), "AddGoalDialogTag");
        });

        // --- Perubahan untuk Tab Control ---
        // Kamu bisa menggunakan TabLayout (sesuai kode ini) ATAU MaterialButtonToggleGroup
        // Pastikan ID di sini sesuai dengan yang di fragment_goal.xml

        // Jika menggunakan TabLayout:
        tabLayout = view.findViewById(R.id.tab_layout);
        // Tambahkan tab secara programatis jika belum di XML
        if (tabLayout.getTabCount() == 0) { // Hanya tambahkan jika belum ada
            tabLayout.addTab(tabLayout.newTab().setText("All"));
            tabLayout.addTab(tabLayout.newTab().setText("Tasks"));
            tabLayout.addTab(tabLayout.newTab().setText("Goals"));
            tabLayout.addTab(tabLayout.newTab().setText("Done"));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabAppearance(tab, true); // Hanya jika menggunakan custom background di Java
                currentFilter = tab.getText().toString();
                filterGoals(currentFilter); // Panggil langsung, tidak perlu post()
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabAppearance(tab, false); // Hanya jika menggunakan custom background di Java
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tidak perlu melakukan apa-apa jika tab yang sama diklik lagi
            }
        });

        // Pilih tab "All" secara default dan filter data
        // Menggunakan post() di sini adalah cara yang baik untuk memastikan tabLayout sudah diukur
        tabLayout.post(() -> {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0); // Dapatkan tab "All" (indeks 0)
            if (firstTab != null) {
                firstTab.select(); // Pilih tab "All" secara programatis
                updateTabAppearance(firstTab, true); // Hanya jika menggunakan custom background di Java
                currentFilter = firstTab.getText().toString();
                filterGoals(currentFilter); // Panggil langsung
            }
        });
        // --- Akhir Perubahan untuk Tab Control ---

        return view;
    }

    // Metode updateTabAppearance() kamu (jika masih diperlukan untuk custom background)
    // Jika kamu menggunakan MaterialButtonToggleGroup dengan styling di XML, metode ini mungkin tidak perlu.
    private void updateTabAppearance(TabLayout.Tab tab, boolean isSelected) {
        View tabView = tab.view;
        if (tabView != null) {
            if (isSelected) {
                tabView.setBackgroundResource(R.drawable.rounded_active_blue); // Pastikan ini ada
            } else {
                tabView.setBackgroundResource(R.drawable.rounded_transparent_background); // Pastikan ini ada
            }
            // Kamu juga mungkin perlu mengatur warna teks secara manual di sini
            // ((TextView) tabView.findViewById(android.R.id.text1)).setTextColor(ContextCompat.getColor(getContext(), isSelected ? R.color.blue : R.color.black_light));
        }
    }


    // Memfilter daftar goals berdasarkan filter yang dipilih
    private void filterGoals(String filter) {
        List<Goal> filteredList = new ArrayList<>();

        switch (filter) {
            case "All":
                filteredList.addAll(allGoals);
                // Urutkan: yang belum selesai di atas, yang sudah selesai di bawah
                Collections.sort(filteredList, Comparator.comparing(Goal::isCompleted));
                break;
            case "Tasks":
                // Tampilkan SEMUA Task (selesai atau belum), lalu urutkan yang belum selesai di atas
                for (Goal goal : allGoals) {
                    if ("Task".equals(goal.getLabel())) { // <-- HAPUS && !goal.isCompleted()
                        filteredList.add(goal);
                    }
                }
                Collections.sort(filteredList, Comparator.comparing(Goal::isCompleted)); // <-- Tambahkan pengurutan
                break;
            case "Goals":
                // Tampilkan SEMUA Goal (selesai atau belum), lalu urutkan yang belum selesai di atas
                for (Goal goal : allGoals) {
                    if ("Goal".equals(goal.getLabel())) { // <-- HAPUS && !goal.isCompleted()
                        filteredList.add(goal);
                    }
                }
                Collections.sort(filteredList, Comparator.comparing(Goal::isCompleted)); // <-- Tambahkan pengurutan
                break;
            case "Done":
                // Tampilkan semua Task dan Goal yang sudah selesai
                for (Goal goal : allGoals) {
                    if (goal.isCompleted()) {
                        filteredList.add(goal);
                    }
                }
                // (Opsional) Anda bisa menambahkan pengurutan di sini untuk tab "Done"
                // Misalnya, yang terakhir diselesaikan muncul di bagian atas di tab "Done"
                // Collections.sort(filteredList, (g1, g2) -> Boolean.compare(g2.isCompleted(), g1.isCompleted()));
                break;
        }
        goalAdapter.updateList(filteredList); // Perbarui data di adapter
    }


    // Implementasi dari AddGoalDialogListener (saat goal baru ditambahkan)
    @Override
    public void onGoalAdded(String goalText, String type) {
        Toast.makeText(getContext(), "Successfully Create New " + type + " : " + goalText, Toast.LENGTH_LONG).show();
        Goal newGoal = new Goal(goalText, type, false);

        // --- PERUBAHAN DI SINI: Tambahkan ke indeks 0 (awal daftar) ---
        allGoals.add(0, newGoal); // Tambahkan goal baru ke daftar lengkap di posisi paling atas

        filterGoals(currentFilter); // Panggil langsung filterGoals untuk memperbarui RecyclerView
        // Opsional: Gulir ke bagian atas setelah menambahkan jika tab yang aktif adalah "All" atau "Tasks/Goals" (dan item baru muncul di atas)
        if ("All".equals(currentFilter) || "Tasks".equals(currentFilter) || "Goals".equals(currentFilter)) {
            goalRecyclerView.scrollToPosition(0);
        }
    }

    // Implementasi dari GoalAdapter.OnGoalStatusChangeListener (saat status checkbox berubah)
    @Override
    public void onGoalStatusChanged() {
        // Ketika status checkbox berubah, kita perlu memfilter ulang daftar
        // untuk memastikan item berada di tab yang benar (misal: pindah dari "Tasks" ke "Done")
        filterGoals(currentFilter); // Panggil langsung filterGoals
    }
}