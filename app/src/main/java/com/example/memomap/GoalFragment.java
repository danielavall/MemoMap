// app/src/main/java/com/example/memomap/GoalFragment.java
package com.example.memomap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class GoalFragment extends Fragment implements AddGoalDialogFragment.AddGoalDialogListener,
        GoalAdapter.OnGoalStatusChangeListener, GoalAdapter.OnGoalDeleteListener {

    private RecyclerView goalRecyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> allGoals; // Ini adalah sumber data utama kita
    private FloatingActionButton addGoalsButton;
    private TabLayout tabLayout;

    private TextView emptyAllGoalsText;
    private TextView emptyTasksText;
    private TextView emptyGoalsText;
    private TextView emptyDoneText;

    private String currentFilter = "All";

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
        goalAdapter = new GoalAdapter(new ArrayList<>(), getContext());
        goalRecyclerView.setAdapter(goalAdapter);
        goalAdapter.setOnGoalStatusChangeListener(this);
        goalAdapter.setOnGoalDeleteListener(this); // Set delete listener ke fragment ini

        // Inisialisasi SwipeToDeleteCallback dan attach ke RecyclerView
        // Pastikan R.drawable.ic_delete_white ada di folder drawable Anda
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(goalAdapter, R.drawable.icon_trash));
        itemTouchHelper.attachToRecyclerView(goalRecyclerView);

        addGoalsButton = view.findViewById(R.id.addGoalsButton);
        addGoalsButton.setOnClickListener(v -> {
            Log.d("DEBUG_APP", "GoalFragment: currentFilter saat klik tombol tambah: " + currentFilter); // Tambahkan ini
            AddGoalDialogFragment addGoalDialog = new AddGoalDialogFragment();
            Bundle args = new Bundle();
            String preSelectLabel = "Task"; // Default

            if ("Goals".equals(currentFilter)) {
                preSelectLabel = "Goal";
            } else if ("Tasks".equals(currentFilter)) {
                preSelectLabel = "Task";
            } else {
                preSelectLabel = "Task";
            }

            args.putString("PRE_SELECT_LABEL", preSelectLabel);
            addGoalDialog.setArguments(args);
            addGoalDialog.setAddGoalDialogListener(this);
            addGoalDialog.show(getParentFragmentManager(), "AddGoalDialogTag");
        });

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
                if (goalRecyclerView != null) {
                    goalRecyclerView.post(() -> {
                        filterGoals(currentFilter);
                        showEmptyMessage();
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabAppearance(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });

        tabLayout.post(() -> {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0);
            if (firstTab != null) {
                firstTab.select();
                updateTabAppearance(firstTab, true);
                currentFilter = firstTab.getText().toString();
                if (goalRecyclerView != null) {
                    goalRecyclerView.post(() -> {
                        filterGoals(currentFilter);
                        showEmptyMessage();
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

    private void filterGoals(String filter) {
        List<Goal> filteredList = new ArrayList<>();

        switch (filter) {
            case "All":
                filteredList.addAll(allGoals);
                Collections.sort(filteredList, (g1, g2) -> {
                    int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
                    if (completedComparison != 0) {
                        return completedComparison;
                    }
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
                Collections.sort(filteredList, (g1, g2) -> Long.compare(g2.getLastCompletionTimestamp(), g1.getLastCompletionTimestamp()));
                break;
        }
        goalAdapter.updateList(filteredList);
    }

    private void showEmptyMessage() {
        emptyAllGoalsText.setVisibility(View.GONE);
        emptyTasksText.setVisibility(View.GONE);
        emptyGoalsText.setVisibility(View.GONE);
        emptyDoneText.setVisibility(View.GONE);
        goalRecyclerView.setVisibility(View.VISIBLE);

        if (goalAdapter.getItemCount() == 0) {
            goalRecyclerView.setVisibility(View.GONE);
            switch (currentFilter) {
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

    private List<Goal> filterGoalsToList(String filter) {
        List<Goal> filteredList = new ArrayList<>();

        for (Goal goal : allGoals) {
            boolean match = false;
            switch (filter) {
                case "All":
                    match = true;
                    break;
                case "Tasks":
                    match = "Task".equals(goal.getLabel());
                    break;
                case "Goals":
                    match = "Goal".equals(goal.getLabel());
                    break;
                case "Done":
                    match = goal.isCompleted();
                    break;
                default:
                    match = false;
            }
            if (match) filteredList.add(goal);
        }

        // Sorting sesuai filter
        if ("Done".equals(filter)) {
            filteredList.sort((g1, g2) -> Long.compare(g2.getLastCompletionTimestamp(), g1.getLastCompletionTimestamp()));
        } else {
            filteredList.sort((g1, g2) -> {
                int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
                if (completedComparison != 0) return completedComparison;
                return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
            });
        }

        return filteredList;
    }


    @Override
    public void onGoalAdded(String goalText, String type) {
        Toast.makeText(getContext(), "Successfully Create New " + type + " : " + goalText, Toast.LENGTH_LONG).show();
        Goal newGoal = new Goal(goalText, type, false);
        allGoals.add(0, newGoal);

        // Cek apakah goal ini cocok dengan filter sekarang
        boolean isMatch = false;
        switch (currentFilter) {
            case "All":
                isMatch = true;
                break;
            case "Tasks":
                isMatch = "Task".equals(type);
                break;
            case "Goals":
                isMatch = "Goal".equals(type);
                break;
            case "Done":
                isMatch = false;
                break;
        }

        if (isMatch) {
            goalAdapter.updateList(filterGoalsToList(currentFilter));
        }

        showEmptyMessage();
    }


    @Override
    public void onGoalStatusChanged() {
        if (goalRecyclerView != null) {
            goalRecyclerView.post(() -> {
                filterGoals(currentFilter);
                showEmptyMessage();
            });
        }
    }

    @Override
    public void onGoalDeleted(Goal deletedGoal) {
        // Hapus goal yang sesuai dari sumber data utama (allGoals)
        Iterator<Goal> iterator = allGoals.iterator();
        while (iterator.hasNext()) {
            Goal goal = iterator.next();
            // Penting: Pastikan objek Goal memiliki metode equals() dan hashCode() yang di-override
            // dengan benar, atau bandingkan berdasarkan properti unik seperti judul.
            if (goal.equals(deletedGoal)) {
                iterator.remove();
                break;
            }
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



