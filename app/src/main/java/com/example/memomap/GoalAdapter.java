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
    private OnGoalStatusChangeListener listener; // Listener untuk komunikasi ke Fragment

    public GoalAdapter(List<Goal> goalList) {
        this.goalList = goalList;
    }

    // Metode untuk memperbarui data di adapter
    public void updateList(List<Goal> newList) {
        this.goalList.clear();
        this.goalList.addAll(newList);
        notifyDataSetChanged(); // Memberi tahu RecyclerView untuk me-refresh
    }

    // Setter untuk listener
    public void setOnGoalStatusChangeListener(OnGoalStatusChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_goals, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal currentGoal = goalList.get(position);

        holder.titleTextView.setText(currentGoal.getTitle());
        holder.labelTextView.setText(currentGoal.getLabel());

        // Hapus listener sebelumnya sebelum mengatur yang baru untuk menghindari masalah daur ulang
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(currentGoal.isCompleted());

        // Atur listener baru
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentGoal.setCompleted(isChecked);
            // Beri tahu Fragment bahwa status goal telah berubah
            if (listener != null) {
                listener.onGoalStatusChanged();
            }
        });

        // Logika untuk mengubah latar belakang label
        String label = currentGoal.getLabel();
        if ("Task".equals(label)) {
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_task);
        } else if ("Goal".equals(label)) {
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_goal);
        }
        // Tambahkan else jika ada kondisi label lain yang perlu background berbeda
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

    // Interface untuk komunikasi dari Adapter ke Fragment
    public interface OnGoalStatusChangeListener {
        void onGoalStatusChanged();
    }
}