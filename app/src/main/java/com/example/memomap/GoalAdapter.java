// app/src/main/java/com/example/memomap/GoalAdapter.java
package com.example.memomap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goalList;
    private OnGoalStatusChangeListener listener;
    private OnGoalDeleteListener deleteListener;
    private final Context context;

    public GoalAdapter(List<Goal> goalList, Context context) {
        this.goalList = goalList;
        this.context = context;
        setHasStableIds(true); // Penting untuk RecyclerView yang lebih stabil
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Goal> newList) {
        this.goalList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void setOnGoalStatusChangeListener(OnGoalStatusChangeListener listener) {
        this.listener = listener;
    }

    public void setOnGoalDeleteListener(OnGoalDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public Context getItemViewContext() {
        return context;
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
        Log.d("DEBUG_APP", "onBindViewHolder: Binding position " + position + ", Goal: " + currentGoal.toString());

        // --- PERBAIKAN PENTING UNTUK RESET TRANSLASI DI AWAL onBindViewHolder ---
        // Ini memastikan ViewHolder yang didaur ulang kembali ke posisi awal
        // dan visibilitas default sebelum data baru di-bind.
        holder.itemView.setTranslationX(0); // Reset translasi seluruh item
        holder.mainContentLayout.setTranslationX(0); // Reset translasi main_content_layout
        holder.deleteButtonLayout.setVisibility(View.INVISIBLE); // Pastikan hidden by default
        // --- AKHIR PERBAIKAN RESET ---

        holder.titleTextView.setText(currentGoal.getTitle());
        holder.labelTextView.setText(currentGoal.getLabel());

        // Reset checkbox listener dulu sebelum mengatur checked state
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(currentGoal.isCompleted());

        // Atur listener baru untuk checkbox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentGoal.setCompleted(isChecked);
            if (listener != null) {
                listener.onGoalStatusChanged();
            }
        });

        // Ganti background label berdasarkan jenis goal
        String label = currentGoal.getLabel();
        if ("Task".equals(label)) {
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_task);
        } else if ("Goal".equals(label)) {
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_goal);
        }

        // Tangani klik pada tombol delete (area merah)
        holder.deleteButtonLayout.setOnClickListener(v -> {
            deleteItem(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    // --- PENTING: Override getItemId untuk Stable IDs ---
    @Override
    public long getItemId(int position) {
        // Menggunakan ID unik dari objek Goal Anda (Goal.java harus memiliki getId())
        return goalList.get(position).getId();
    }
    // --- AKHIR getItemId ---

    public void deleteItem(int position) {
        if (position >= 0 && position < goalList.size()) {
            Goal deletedGoal = goalList.get(position);
            goalList.remove(position);
            notifyItemRemoved(position); // Memberi tahu RecyclerView untuk menghapus item

            // Beri tahu Fragment bahwa item telah dihapus
            if (deleteListener != null) {
                deleteListener.onGoalDeleted(deletedGoal);
            }
            Toast.makeText(context, "Goal '" + deletedGoal.getTitle() + "' deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView labelTextView;
        CheckBox checkBox;
        RelativeLayout deleteButtonLayout;
        LinearLayout mainContentLayout; // Deklarasi

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text);
            labelTextView = itemView.findViewById(R.id.label_text);
            checkBox = itemView.findViewById(R.id.checkbox);
            deleteButtonLayout = itemView.findViewById(R.id.delete_button_layout);
            // --- PERBAIKAN PENTING DI SINI: GUNAKAN ID YANG BENAR ---
            mainContentLayout = itemView.findViewById(R.id.main_content_layout); // <-- INI YANG BENAR
            // --- AKHIR PERBAIKAN ---
        }
    }

    public interface OnGoalStatusChangeListener {
        void onGoalStatusChanged();
    }

    public interface OnGoalDeleteListener {
        void onGoalDeleted(Goal deletedGoal);
    }
}