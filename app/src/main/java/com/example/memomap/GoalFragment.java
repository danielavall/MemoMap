// app/src/main/java/com/example/memomap/GoalFragment.java
package com.example.memomap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper; // Import ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator; // Import Iterator
import java.util.List;

public class GoalFragment extends Fragment implements AddGoalDialogFragment.AddGoalDialogListener,
        GoalAdapter.OnGoalStatusChangeListener, GoalAdapter.OnGoalDeleteListener { // Implement OnGoalDeleteListener

    private RecyclerView goalRecyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> allGoals; // Ini adalah sumber data utama kita
    private FloatingActionButton addGoalsButton;
    private TabLayout tabLayout;

    // Deklarasi TextView kosong
    private TextView emptyAllGoalsText;
    private TextView emptyTasksText;
    private TextView emptyGoalsText;
    private TextView emptyDoneText;

    private String currentFilter = "All"; // Filter yang sedang aktif

    public GoalFragment() {
        // Required empty public constructor
    }

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
        allGoals = new ArrayList<>();
        // Contoh data awal
        allGoals.add(new Goal("Belajar Android Jetpack Compose", "Task", false));
        allGoals.add(new Goal("Lari Pagi 30 Menit", "Task", true));
        allGoals.add(new Goal("Baca Buku 'Atomic Habits'", "Goal", false));
        allGoals.add(new Goal("Selesaikan Laporan Bulanan", "Goal", false));
        allGoals.add(new Goal("Telepon Orang Tua", "Task", false));
        allGoals.add(new Goal("Menyiram Tanaman", "Goal", true));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        goalRecyclerView = view.findViewById(R.id.goal_recycler_view);
        goalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi GoalAdapter dengan Context dari Fragment
        goalAdapter = new GoalAdapter(new ArrayList<>(), getContext()); // Penting: berikan getContext()
        goalRecyclerView.setAdapter(goalAdapter);
        goalAdapter.setOnGoalStatusChangeListener(this);
        goalAdapter.setOnGoalDeleteListener(this); // Set delete listener ke fragment ini

        // Inisialisasi SwipeToDeleteCallback dan attach ke RecyclerView
        // Pastikan R.drawable.ic_delete_white ada di folder drawable Anda
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(goalAdapter, R.drawable.icon_trash));
        itemTouchHelper.attachToRecyclerView(goalRecyclerView);

        addGoalsButton = view.findViewById(R.id.addGoalsButton);
        addGoalsButton.setOnClickListener(v -> {
            AddGoalDialogFragment addGoalDialog = new AddGoalDialogFragment();
            addGoalDialog.setAddGoalDialogListener(this);
            addGoalDialog.show(getParentFragmentManager(), "AddGoalDialogTag");
        });

        // Inisialisasi TextView kosong
        emptyAllGoalsText = view.findViewById(R.id.empty_all_goals_text);
        emptyTasksText = view.findViewById(R.id.empty_tasks_text);
        emptyGoalsText = view.findViewById(R.id.empty_goals_text);
        emptyDoneText = view.findViewById(R.id.empty_done_text);

        tabLayout = view.findViewById(R.id.tab_layout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabAppearance(tab, true);
                currentFilter = tab.getText().toString();
                // Gunakan post untuk memastikan RecyclerView sudah siap
                if (goalRecyclerView != null) {
                    goalRecyclerView.post(() -> {
                        filterGoals(currentFilter);
                        showEmptyMessage(); // Panggil setelah filter
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabAppearance(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tidak perlu melakukan apa-apa
            }
        });

        // Pilih tab pertama secara default dan terapkan filter awal
        tabLayout.post(() -> {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0);
            if (firstTab != null) {
                firstTab.select();
                updateTabAppearance(firstTab, true);
                currentFilter = firstTab.getText().toString();
                if (goalRecyclerView != null) {
                    goalRecyclerView.post(() -> {
                        filterGoals(currentFilter);
                        showEmptyMessage(); // Panggil setelah filter
                    });
                }
            }
        });

        return view;
    }

    private void updateTabAppearance(TabLayout.Tab tab, boolean isSelected) {
        View tabView = tab.view;
        if (tabView != null) {
            if (isSelected) {
                tabView.setBackgroundResource(R.drawable.rounded_active_blue); // Pastikan drawable ini ada
            } else {
                tabView.setBackgroundResource(R.drawable.rounded_transparent_background); // Pastikan drawable ini ada
            }
        }
    }

    // Metode untuk memfilter dan mengurutkan daftar goal
    private void filterGoals(String filter) {
        List<Goal> filteredList = new ArrayList<>();

        switch (filter) {
            case "All":
                filteredList.addAll(allGoals);
                // Urutkan: item yang belum selesai di atas, lalu berdasarkan timestamp penyelesaian (yang 0 akan di atas)
                Collections.sort(filteredList, (g1, g2) -> {
                    int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
                    if (completedComparison != 0) {
                        return completedComparison; // False (belum selesai) < True (sudah selesai)
                    }
                    // Jika status sama, urutkan berdasarkan timestamp (ascending)
                    return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
                });
                break;
            case "Tasks":
                for (Goal goal : allGoals) {
                    if ("Task".equals(goal.getLabel())) {
                        filteredList.add(goal);
                    }
                }
                Collections.sort(filteredList, (g1, g2) -> {
                    int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
                    if (completedComparison != 0) {
                        return completedComparison;
                    }
                    return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
                });
                break;
            case "Goals":
                for (Goal goal : allGoals) {
                    if ("Goal".equals(goal.getLabel())) {
                        filteredList.add(goal);
                    }
                }
                Collections.sort(filteredList, (g1, g2) -> {
                    int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
                    if (completedComparison != 0) {
                        return completedComparison;
                    }
                    return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
                });
                break;
            case "Done":
                for (Goal goal : allGoals) {
                    if (goal.isCompleted()) {
                        filteredList.add(goal);
                    }
                }
                // Urutkan item yang sudah selesai berdasarkan timestamp penyelesaian (descending, terbaru di atas)
                Collections.sort(filteredList, (g1, g2) -> Long.compare(g2.getLastCompletionTimestamp(), g1.getLastCompletionTimestamp()));
                break;
        }
        goalAdapter.updateList(filteredList); // Perbarui data di adapter
    }

    // Metode untuk menampilkan/menyembunyikan pesan kosong
    private void showEmptyMessage() {
        // Sembunyikan semua pesan kosong terlebih dahulu
        emptyAllGoalsText.setVisibility(View.GONE);
        emptyTasksText.setVisibility(View.GONE);
        emptyGoalsText.setVisibility(View.GONE);
        emptyDoneText.setVisibility(View.GONE);
        goalRecyclerView.setVisibility(View.VISIBLE); // Asumsikan RecyclerView terlihat secara default

        if (goalAdapter.getItemCount() == 0) { // Jika tidak ada item di daftar yang ditampilkan
            goalRecyclerView.setVisibility(View.GONE); // Sembunyikan RecyclerView
            switch (currentFilter) { // Tampilkan pesan yang sesuai dengan filter
                case "All":
                    emptyAllGoalsText.setVisibility(View.VISIBLE);
                    break;
                case "Tasks":
                    emptyTasksText.setVisibility(View.VISIBLE);
                    break;
                case "Goals":
                    emptyGoalsText.setVisibility(View.VISIBLE);
                    break;
                case "Done":
                    emptyDoneText.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onGoalAdded(String goalText, String type) {
        Toast.makeText(getContext(), "Successfully Create New " + type + " : " + goalText, Toast.LENGTH_LONG).show();
        // Ketika menambahkan goal baru, isCompleted adalah false, jadi timestamp akan 0
        Goal newGoal = new Goal(goalText, type, false);
        allGoals.add(0, newGoal); // Tambahkan di awal allGoals

        if (goalRecyclerView != null) {
            goalRecyclerView.post(() -> {
                filterGoals(currentFilter); // Filter ulang untuk memperbarui tampilan
                showEmptyMessage(); // Perbarui pesan kosong
            });
        }
    }

    @Override
    public void onGoalStatusChanged() {
        // Ketika status goal berubah (checkbox dicentang/tidak), perbarui timestamp
        // Goal object sudah diupdate di adapter, kita hanya perlu merefresh list di fragment
        if (goalRecyclerView != null) {
            goalRecyclerView.post(() -> {
                filterGoals(currentFilter); // Filter ulang untuk memperbarui urutan
                showEmptyMessage(); // Perbarui pesan kosong
            });
        }
    }

    @Override
    public void onGoalDeleted(Goal deletedGoal) { // Menerima objek Goal yang dihapus
        // Hapus goal yang sesuai dari sumber data utama (allGoals)
        // Gunakan Iterator untuk menghindari ConcurrentModificationException
        Iterator<Goal> iterator = allGoals.iterator();
        while (iterator.hasNext()) {
            Goal goal = iterator.next();
            // Asumsi Goal memiliki equals dan hashCode yang tepat, atau bandingkan berdasarkan ID/judul unik
            if (goal.equals(deletedGoal)) { // Jika objek Goal memiliki equals() yang memadai
                iterator.remove();
                break; // Hentikan setelah menemukan dan menghapus
            }
            // Jika tidak ada ID unik atau equals() tidak di-override,
            // Anda bisa membandingkan berdasarkan judul dan label:
            // if (goal.getTitle().equals(deletedGoal.getTitle()) && goal.getLabel().equals(deletedGoal.getLabel())) {
            //     iterator.remove();
            //     break;
            // }
        }

        // Setelah goal dihapus dari allGoals, filter ulang untuk memperbarui tampilan
        if (goalRecyclerView != null) {
            goalRecyclerView.post(() -> {
                filterGoals(currentFilter);
                showEmptyMessage();
            });
        }
    }
}



//// app/src/main/java/com/example/memomap/GoalFragment.java
//package com.example.memomap;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView; // Import TextView
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.tabs.TabLayout;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class GoalFragment extends Fragment implements AddGoalDialogFragment.AddGoalDialogListener, GoalAdapter.OnGoalStatusChangeListener {
//
//    private RecyclerView goalRecyclerView;
//    private GoalAdapter goalAdapter;
//    private List<Goal> allGoals;
//    private FloatingActionButton addGoalsButton;
//    private TabLayout tabLayout;
//
//    // Deklarasi TextView kosong
//    private TextView emptyAllGoalsText;
//    private TextView emptyTasksText;
//    private TextView emptyGoalsText;
//    private TextView emptyDoneText;
//
//    private String currentFilter = "All";
//
//    public GoalFragment() {
//        // Required empty public constructor
//    }
//
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private String mParam1;
//    private String mParam2;
//
//    public static GoalFragment newInstance(String param1, String param2) {
//        GoalFragment fragment = new GoalFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        allGoals = new ArrayList<>();
//        allGoals.add(new Goal("Belajar Android Jetpack Compose", "Task", false));
//        allGoals.add(new Goal("Lari Pagi 30 Menit", "Task", true));
//        allGoals.add(new Goal("Baca Buku 'Atomic Habits'", "Goal", false));
//        allGoals.add(new Goal("Selesaikan Laporan Bulanan", "Goal", false));
//        allGoals.add(new Goal("Telepon Orang Tua", "Task", false));
//        allGoals.add(new Goal("Menyiram Tanaman", "Goal", true));
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_goal, container, false);
//
//        goalRecyclerView = view.findViewById(R.id.goal_recycler_view);
//        goalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        goalAdapter = new GoalAdapter(new ArrayList<>());
//        goalRecyclerView.setAdapter(goalAdapter);
//        goalAdapter.setOnGoalStatusChangeListener(this);
//
//        addGoalsButton = view.findViewById(R.id.addGoalsButton);
//        addGoalsButton.setOnClickListener(v -> {
//            AddGoalDialogFragment addGoalDialog = new AddGoalDialogFragment();
//            addGoalDialog.setAddGoalDialogListener(this);
//            addGoalDialog.show(getParentFragmentManager(), "AddGoalDialogTag");
//        });
//
//        // Inisialisasi TextView kosong
//        emptyAllGoalsText = view.findViewById(R.id.empty_all_goals_text);
//        emptyTasksText = view.findViewById(R.id.empty_tasks_text);
//        emptyGoalsText = view.findViewById(R.id.empty_goals_text);
//        emptyDoneText = view.findViewById(R.id.empty_done_text);
//
//        tabLayout = view.findViewById(R.id.tab_layout);
//        // Hapus blok ini karena TabItem sudah ada di XML
//        // if (tabLayout.getTabCount() == 0) {
//        //     tabLayout.addTab(tabLayout.newTab().setText("All"));
//        //     tabLayout.addTab(tabLayout.newTab().setText("Tasks"));
//        //     tabLayout.addTab(tabLayout.newTab().setText("Goals"));
//        //     tabLayout.addTab(tabLayout.newTab().setText("Done"));
//        // }
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                updateTabAppearance(tab, true);
//                currentFilter = tab.getText().toString();
//                if (goalRecyclerView != null) {
//                    goalRecyclerView.post(() -> {
//                        filterGoals(currentFilter);
//                        showEmptyMessage(); // Panggil setelah filter
//                    });
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                updateTabAppearance(tab, false);
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                // Tidak perlu melakukan apa-apa
//            }
//        });
//
//        tabLayout.post(() -> {
//            TabLayout.Tab firstTab = tabLayout.getTabAt(0);
//            if (firstTab != null) {
//                firstTab.select();
//                updateTabAppearance(firstTab, true);
//                currentFilter = firstTab.getText().toString();
//                if (goalRecyclerView != null) {
//                    goalRecyclerView.post(() -> {
//                        filterGoals(currentFilter);
//                        showEmptyMessage(); // Panggil setelah filter
//                    });
//                }
//            }
//        });
//
//        return view;
//    }
//
//    private void updateTabAppearance(TabLayout.Tab tab, boolean isSelected) {
//        View tabView = tab.view;
//        if (tabView != null) {
//            if (isSelected) {
//                tabView.setBackgroundResource(R.drawable.rounded_active_blue); // Pastikan ini ada dan warnanya terlihat jelas
//            } else {
//                tabView.setBackgroundResource(R.drawable.rounded_transparent_background); // Pastikan ini ada
//            }
//            // Tambahkan ini jika ingin teks berubah warna juga
//            // TextView tabTextView = (TextView) ((ViewGroup) tabView).getChildAt(0);
//            // if (tabTextView != null) {
//            //     tabTextView.setTextColor(ContextCompat.getColor(getContext(), isSelected ? R.color.white : R.color.black));
//            // }
//        }
//    }
//
//
//    private void filterGoals(String filter) {
//        List<Goal> filteredList = new ArrayList<>();
//
//        switch (filter) {
//            case "All":
//                filteredList.addAll(allGoals);
//                Collections.sort(filteredList, (g1, g2) -> {
//                    int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
//                    if (completedComparison != 0) {
//                        return completedComparison;
//                    }
//                    return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
//                });
//                break;
//            case "Tasks":
//                for (Goal goal : allGoals) {
//                    if ("Task".equals(goal.getLabel())) {
//                        filteredList.add(goal);
//                    }
//                }
//                Collections.sort(filteredList, (g1, g2) -> {
//                    int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
//                    if (completedComparison != 0) {
//                        return completedComparison;
//                    }
//                    return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
//                });
//                break;
//            case "Goals":
//                for (Goal goal : allGoals) {
//                    if ("Goal".equals(goal.getLabel())) {
//                        filteredList.add(goal);
//                    }
//                }
//                Collections.sort(filteredList, (g1, g2) -> {
//                    int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
//                    if (completedComparison != 0) {
//                        return completedComparison;
//                    }
//                    return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
//                });
//                break;
//            case "Done":
//                for (Goal goal : allGoals) {
//                    if (goal.isCompleted()) {
//                        filteredList.add(goal);
//                    }
//                }
//                Collections.sort(filteredList, (g1, g2) -> Long.compare(g2.getLastCompletionTimestamp(), g1.getLastCompletionTimestamp()));
//                break;
//        }
//        goalAdapter.updateList(filteredList);
//    }
//
//    // Metode untuk menampilkan/menyembunyikan pesan kosong
//    private void showEmptyMessage() {
//        // Sembunyikan semua pesan kosong terlebih dahulu
//        emptyAllGoalsText.setVisibility(View.GONE);
//        emptyTasksText.setVisibility(View.GONE);
//        emptyGoalsText.setVisibility(View.GONE);
//        emptyDoneText.setVisibility(View.GONE);
//        goalRecyclerView.setVisibility(View.VISIBLE); // Asumsikan RecyclerView terlihat secara default
//
//        if (goalAdapter.getItemCount() == 0) {
//            goalRecyclerView.setVisibility(View.GONE); // Sembunyikan RecyclerView
//            switch (currentFilter) {
//                case "All":
//                    emptyAllGoalsText.setVisibility(View.VISIBLE);
//                    break;
//                case "Tasks":
//                    emptyTasksText.setVisibility(View.VISIBLE);
//                    break;
//                case "Goals":
//                    emptyGoalsText.setVisibility(View.VISIBLE);
//                    break;
//                case "Done":
//                    emptyDoneText.setVisibility(View.VISIBLE);
//                    break;
//            }
//        }
//    }
//
//
//    @Override
//    public void onGoalAdded(String goalText, String type) {
//        Toast.makeText(getContext(), "Successfully Create New " + type + " : " + goalText, Toast.LENGTH_LONG).show();
//        // Ketika menambahkan goal baru, isCompleted adalah false, jadi timestamp akan 0
//        Goal newGoal = new Goal(goalText, type, false);
//        allGoals.add(0, newGoal); // Tambahkan di awal agar muncul di atas jika belum selesai
//
//        if (goalRecyclerView != null) {
//            goalRecyclerView.post(() -> {
//                filterGoals(currentFilter);
//                showEmptyMessage(); // Panggil setelah filter
//            });
//        }
//    }
//
//    @Override
//    public void onGoalStatusChanged() {
//        if (goalRecyclerView != null) {
//            goalRecyclerView.post(() -> {
//                filterGoals(currentFilter);
//                showEmptyMessage(); // Panggil setelah filter
//            });
//        }
//    }
//}