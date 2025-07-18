// app/src/main/java/com/example/memomap/GoalAdapter.java
package com.example.memomap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goalList;

    public GoalAdapter(List<Goal> goalList) {
        this.goalList = goalList;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goals_layout, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal currentGoal = goalList.get(position);

        holder.titleTextView.setText(currentGoal.getTitle());
        holder.labelTextView.setText(currentGoal.getLabel());
        holder.checkBox.setChecked(currentGoal.isCompleted());

        // --- Logika untuk mengubah latar belakang label ---
        String label = currentGoal.getLabel();
        if ("Task".equals(label)) {
            // Jika label adalah "Task", gunakan latar belakang default (bg_label_task)
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_task);
        } else if ("Goal".equals(label)) {
            // Jika label adalah "Goal", gunakan latar belakang ungu (bg_label_goal)
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_goal);
        } else {
            // Opsional: Jika ada jenis label lain yang tidak Task atau Goal,
            // Anda bisa menetapkan latar belakang default atau yang lain di sini
            // Misalnya: holder.labelTextView.setBackgroundResource(R.drawable.bg_label_task);
        }
        // --- Akhir logika latar belakang label ---


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentGoal.setCompleted(isChecked);
            // Anda mungkin ingin memberi tahu adapter bahwa item telah berubah
            // notifyItemChanged(position); // Hati-hati dengan ini di dalam onBindViewHolder jika ada banyak perubahan
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView labelTextView;
        CheckBox checkBox;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text);
            labelTextView = itemView.findViewById(R.id.label_text);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}