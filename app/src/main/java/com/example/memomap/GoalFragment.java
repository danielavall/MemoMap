package com.example.memomap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

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

import android.animation.ObjectAnimator; // Untuk animasi ProgressBar
import android.animation.ValueAnimator; // <--- IMPOR BARU UNTUK ANIMASI TEKS

public class GoalFragment extends Fragment implements AddGoalDialogFragment.AddGoalDialogListener,
        GoalAdapter.OnGoalStatusChangeListener, GoalAdapter.OnGoalDeleteListener {

    private RecyclerView goalRecyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> allGoals;
    private FloatingActionButton addGoalsButton;
    private TabLayout tabLayout;

    private TextView emptyAllGoalsText;
    private TextView emptyTasksText;
    private TextView emptyGoalsText;
    private TextView emptyDoneText;

    private TextView progressPercentageText;
    private ProgressBar goalsProgressBar;
    private ImageView iconKapalStart;
    private ImageView iconPulauEnd;

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

        progressPercentageText = view.findViewById(R.id.progress_percentage_text);
        goalsProgressBar = view.findViewById(R.id.goals_progress_bar);
        iconKapalStart = view.findViewById(R.id.icon_kapal_start);
        iconPulauEnd = view.findViewById(R.id.icon_pulau_end);

        goalAdapter = new GoalAdapter(new ArrayList<>(), getContext());
        goalRecyclerView.setAdapter(goalAdapter);
        goalAdapter.setOnGoalStatusChangeListener(this);
        goalAdapter.setOnGoalDeleteListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(goalAdapter, R.drawable.icon_trash));
        itemTouchHelper.attachToRecyclerView(goalRecyclerView);

        addGoalsButton = view.findViewById(R.id.addGoalsButton);
        addGoalsButton.setOnClickListener(v -> {
            AddGoalDialogFragment addGoalDialog = new AddGoalDialogFragment();
            Bundle args = new Bundle();
            String preSelectLabel = "Task";

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
                        updateProgressUI();
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
                        updateProgressUI();
                    });
                }
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void updateProgressUI() {
        int totalGoals = 0;
        int completedGoals = 0;

        for (Goal goal : allGoals) {
            totalGoals++;
            if (goal.isCompleted()) {
                completedGoals++;
            }
        }

        final int progressPercentage;
        if (totalGoals > 0) {
            progressPercentage = (completedGoals * 100) / totalGoals;
        } else {
            progressPercentage = 0;
        }

        // --- PERBAIKAN: Animasi Progress Bar ---
        ObjectAnimator animator = ObjectAnimator.ofInt(goalsProgressBar, "progress", goalsProgressBar.getProgress(), progressPercentage);
        animator.setDuration(750); // Durasi animasi (milidetik)
        animator.start();
        // --- AKHIR PERBAIKAN ANIMASI PROGRESS BAR ---

        // --- PERBAIKAN BARU: Animasi Teks Persentase ---
        // Dapatkan nilai persentase yang saat ini ditampilkan di TextView
        int currentDisplayedPercentage = 0;
        String currentText = progressPercentageText.getText().toString();
        if (currentText.endsWith("%") && currentText.length() > 1) {
            try {
                currentDisplayedPercentage = Integer.parseInt(currentText.substring(0, currentText.length() - 1));
            } catch (NumberFormatException e) {
                // Jika parsing gagal (misalnya, teks default "0%", atau error), set ke 0
                currentDisplayedPercentage = 0;
            }
        }

        ValueAnimator textAnimator = ValueAnimator.ofInt(currentDisplayedPercentage, progressPercentage);
        textAnimator.setDuration(750); // Cocokkan durasi dengan progress bar dan kapal
        textAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            progressPercentageText.setText(animatedValue + "%");
        });
        textAnimator.start();
        // --- AKHIR PERBAIKAN ANIMASI TEKS PERSENTASE ---


        goalsProgressBar.post(() -> {
            int progressBarWidth = goalsProgressBar.getWidth();
            int shipIconWidth = iconKapalStart.getWidth();

            if (progressBarWidth > 0 && shipIconWidth > 0) {
                float translationRange = progressBarWidth - shipIconWidth;
                float targetTranslationX = (progressPercentage / 100f) * translationRange;

                final float translationXForShip = Math.max(0f, Math.min(targetTranslationX, translationRange));

                // --- PERBAIKAN: Animasi Pergerakan Kapal ---
                iconKapalStart.animate()
                        .translationX(translationXForShip)
                        .setDuration(750) // Durasi animasi (milidetik)
                        .start();
                // --- AKHIR PERBAIKAN ANIMASI PERGERAKAN KAPAL ---
            }
        });
    }

    private void updateTabAppearance(TabLayout.Tab tab, boolean isSelected) {
        View tabView = tab.view;
        if (tabView != null) {
            if (isSelected) {
                tabView.setBackgroundResource(R.drawable.rounded_active_blue);
            } else {
                tabView.setBackgroundResource(R.drawable.rounded_transparent_background);
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
        updateProgressUI();
    }


    @Override
    public void onGoalStatusChanged() {
        if (goalRecyclerView != null) {
            goalRecyclerView.post(() -> {
                filterGoals(currentFilter);
                showEmptyMessage();
                updateProgressUI();
            });
        }
    }

    @Override
    public void onGoalDeleted(Goal deletedGoal) {
        Iterator<Goal> iterator = allGoals.iterator();
        while (iterator.hasNext()) {
            Goal goal = iterator.next();
            if (goal.equals(deletedGoal)) {
                iterator.remove();
                break;
            }
        }

        if (goalRecyclerView != null) {
            goalRecyclerView.post(() -> {
                filterGoals(currentFilter);
                showEmptyMessage();
                updateProgressUI();
            });
        }
    }
}


//// app/src/main/java/com/example/memomap/GoalFragment.java
//package com.example.memomap;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.tabs.TabLayout;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//
//import android.animation.ObjectAnimator; // Pastikan ini diimpor jika belum
//
//public class GoalFragment extends Fragment implements AddGoalDialogFragment.AddGoalDialogListener,
//        GoalAdapter.OnGoalStatusChangeListener, GoalAdapter.OnGoalDeleteListener {
//
//    private RecyclerView goalRecyclerView;
//    private GoalAdapter goalAdapter;
//    private List<Goal> allGoals;
//    private FloatingActionButton addGoalsButton;
//    private TabLayout tabLayout;
//
//    private TextView emptyAllGoalsText;
//    private TextView emptyTasksText;
//    private TextView emptyGoalsText;
//    private TextView emptyDoneText;
//
//    private TextView progressPercentageText;
//    private ProgressBar goalsProgressBar;
//    private ImageView iconKapalStart;
//    private ImageView iconPulauEnd;
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
//        progressPercentageText = view.findViewById(R.id.progress_percentage_text);
//        goalsProgressBar = view.findViewById(R.id.goals_progress_bar);
//        iconKapalStart = view.findViewById(R.id.icon_kapal_start);
//        iconPulauEnd = view.findViewById(R.id.icon_pulau_end);
//
//        goalAdapter = new GoalAdapter(new ArrayList<>(), getContext());
//        goalRecyclerView.setAdapter(goalAdapter);
//        goalAdapter.setOnGoalStatusChangeListener(this);
//        goalAdapter.setOnGoalDeleteListener(this);
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(goalAdapter, R.drawable.icon_trash));
//        itemTouchHelper.attachToRecyclerView(goalRecyclerView);
//
//        addGoalsButton = view.findViewById(R.id.addGoalsButton);
//        addGoalsButton.setOnClickListener(v -> {
//            AddGoalDialogFragment addGoalDialog = new AddGoalDialogFragment();
//            Bundle args = new Bundle();
//            String preSelectLabel = "Task";
//
//            if ("Goals".equals(currentFilter)) {
//                preSelectLabel = "Goal";
//            } else if ("Tasks".equals(currentFilter)) {
//                preSelectLabel = "Task";
//            } else {
//                preSelectLabel = "Task";
//            }
//
//            args.putString("PRE_SELECT_LABEL", preSelectLabel);
//            addGoalDialog.setArguments(args);
//            addGoalDialog.setAddGoalDialogListener(this);
//            addGoalDialog.show(getParentFragmentManager(), "AddGoalDialogTag");
//        });
//
//        emptyAllGoalsText = view.findViewById(R.id.empty_all_goals_text);
//        emptyTasksText = view.findViewById(R.id.empty_tasks_text);
//        emptyGoalsText = view.findViewById(R.id.empty_goals_text);
//        emptyDoneText = view.findViewById(R.id.empty_done_text);
//
//        tabLayout = view.findViewById(R.id.tab_layout);
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                updateTabAppearance(tab, true);
//                currentFilter = tab.getText().toString();
//                if (goalRecyclerView != null) {
//                    goalRecyclerView.post(() -> {
//                        filterGoals(currentFilter);
//                        showEmptyMessage();
//                        updateProgressUI();
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
//                // Not needed
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
//                        showEmptyMessage();
//                        updateProgressUI();
//                    });
//                }
//            }
//        });
//
//        return view;
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void updateProgressUI() {
//        int totalGoals = 0;
//        int completedGoals = 0;
//
//        for (Goal goal : allGoals) {
//            totalGoals++;
//            if (goal.isCompleted()) {
//                completedGoals++;
//            }
//        }
//
//        final int progressPercentage;
//        if (totalGoals > 0) {
//            progressPercentage = (completedGoals * 100) / totalGoals;
//        } else {
//            progressPercentage = 0;
//        }
//
//        progressPercentageText.setText(progressPercentage + "%");
//
//        // --- PERBAIKAN: Selalu gunakan ObjectAnimator untuk animasi progress bar ---
//        // Ini akan memberikan animasi yang lebih konsisten di semua API level
//        ObjectAnimator animator = ObjectAnimator.ofInt(goalsProgressBar, "progress", goalsProgressBar.getProgress(), progressPercentage);
//        animator.setDuration(500); // Durasi animasi dalam milidetik. Coba tingkatkan ke 800 atau 1000 jika masih patah-patah
//        animator.start();
//        // --- AKHIR PERBAIKAN ANIMASI PROGRESS BAR ---
//
//
//        goalsProgressBar.post(() -> {
//            int progressBarWidth = goalsProgressBar.getWidth();
//            int shipIconWidth = iconKapalStart.getWidth();
//
//            if (progressBarWidth > 0 && shipIconWidth > 0) {
//                float translationRange = progressBarWidth - shipIconWidth;
//                float targetTranslationX = (progressPercentage / 100f) * translationRange;
//
//                final float translationXForShip = Math.max(0f, Math.min(targetTranslationX, translationRange));
//
//                // --- PERBAIKAN: Animasi Pergerakan Kapal ---
//                iconKapalStart.animate()
//                        .translationX(translationXForShip)
//                        .setDuration(500) // Durasi animasi (milidetik). Cocokkan dengan durasi progress bar.
//                        .start();
//                // --- AKHIR PERBAIKAN ANIMASI PERGERAKAN KAPAL ---
//            }
//        });
//    }
//
//    private void updateTabAppearance(TabLayout.Tab tab, boolean isSelected) {
//        View tabView = tab.view;
//        if (tabView != null) {
//            if (isSelected) {
//                tabView.setBackgroundResource(R.drawable.rounded_active_blue);
//            } else {
//                tabView.setBackgroundResource(R.drawable.rounded_transparent_background);
//            }
//        }
//    }
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
//    private void showEmptyMessage() {
//        emptyAllGoalsText.setVisibility(View.GONE);
//        emptyTasksText.setVisibility(View.GONE);
//        emptyGoalsText.setVisibility(View.GONE);
//        emptyDoneText.setVisibility(View.GONE);
//        goalRecyclerView.setVisibility(View.VISIBLE);
//
//        if (goalAdapter.getItemCount() == 0) {
//            goalRecyclerView.setVisibility(View.GONE);
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
//    private List<Goal> filterGoalsToList(String filter) {
//        List<Goal> filteredList = new ArrayList<>();
//
//        for (Goal goal : allGoals) {
//            boolean match = false;
//            switch (filter) {
//                case "All":
//                    match = true;
//                    break;
//                case "Tasks":
//                    match = "Task".equals(goal.getLabel());
//                    break;
//                case "Goals":
//                    match = "Goal".equals(goal.getLabel());
//                    break;
//                case "Done":
//                    match = goal.isCompleted();
//                    break;
//                default:
//                    match = false;
//            }
//            if (match) filteredList.add(goal);
//        }
//
//        if ("Done".equals(filter)) {
//            filteredList.sort((g1, g2) -> Long.compare(g2.getLastCompletionTimestamp(), g1.getLastCompletionTimestamp()));
//        } else {
//            filteredList.sort((g1, g2) -> {
//                int completedComparison = Boolean.compare(g1.isCompleted(), g2.isCompleted());
//                if (completedComparison != 0) return completedComparison;
//                return Long.compare(g1.getLastCompletionTimestamp(), g2.getLastCompletionTimestamp());
//            });
//        }
//
//        return filteredList;
//    }
//
//
//    @Override
//    public void onGoalAdded(String goalText, String type) {
//        Toast.makeText(getContext(), "Successfully Create New " + type + " : " + goalText, Toast.LENGTH_LONG).show();
//        Goal newGoal = new Goal(goalText, type, false);
//        allGoals.add(0, newGoal);
//
//        boolean isMatch = false;
//        switch (currentFilter) {
//            case "All":
//                isMatch = true;
//                break;
//            case "Tasks":
//                isMatch = "Task".equals(type);
//                break;
//            case "Goals":
//                isMatch = "Goal".equals(type);
//                break;
//            case "Done":
//                isMatch = false;
//                break;
//        }
//
//        if (isMatch) {
//            goalAdapter.updateList(filterGoalsToList(currentFilter));
//        }
//
//        showEmptyMessage();
//        updateProgressUI();
//    }
//
//
//    @Override
//    public void onGoalStatusChanged() {
//        if (goalRecyclerView != null) {
//            goalRecyclerView.post(() -> {
//                filterGoals(currentFilter);
//                showEmptyMessage();
//                updateProgressUI();
//            });
//        }
//    }
//
//    @Override
//    public void onGoalDeleted(Goal deletedGoal) {
//        Iterator<Goal> iterator = allGoals.iterator();
//        while (iterator.hasNext()) {
//            Goal goal = iterator.next();
//            if (goal.equals(deletedGoal)) {
//                iterator.remove();
//                break;
//            }
//        }
//
//        if (goalRecyclerView != null) {
//            goalRecyclerView.post(() -> {
//                filterGoals(currentFilter);
//                showEmptyMessage();
//                updateProgressUI();
//            });
//        }
//    }
//}