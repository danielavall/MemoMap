// app/src/main/java/com/example/memomap/GoalAdapter.java
package com.example.memomap;

import android.content.Context; // Penting: import Context
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout; // Import RelativeLayout
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goalList; // Daftar goal yang sedang ditampilkan
    private OnGoalStatusChangeListener listener; // Listener untuk perubahan status (checkbox)
    private OnGoalDeleteListener deleteListener; // Listener baru untuk penghapusan
    private Context context; // Context diperlukan untuk Toast dan mendapatkan Drawable

    // Constructor yang menerima List<Goal> dan Context
    public GoalAdapter(List<Goal> goalList, Context context) {
        this.goalList = goalList;
        this.context = context;
    }

    // Metode untuk memperbarui data di adapter (saat filter berubah)
    public void updateList(List<Goal> newList) {
        this.goalList.clear();
        this.goalList.addAll(newList);
        notifyDataSetChanged(); // Memberi tahu RecyclerView untuk me-refresh
    }

    // Setter untuk listener status perubahan goal
    public void setOnGoalStatusChangeListener(OnGoalStatusChangeListener listener) {
        this.listener = listener;
    }

    // Setter untuk listener penghapusan goal
    public void setOnGoalDeleteListener(OnGoalDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    // Getter untuk Context, digunakan oleh SwipeToDeleteCallback
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

        holder.titleTextView.setText(currentGoal.getTitle());
        holder.labelTextView.setText(currentGoal.getLabel());

        // Hapus listener sebelumnya sebelum mengatur yang baru untuk menghindari masalah daur ulang
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(currentGoal.isCompleted());

        // Atur listener baru untuk checkbox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentGoal.setCompleted(isChecked);
            // Beri tahu Fragment bahwa status goal telah berubah
            if (listener != null) {
                listener.onGoalStatusChanged();
            }
        });

        // Logika untuk mengubah latar belakang label berdasarkan jenis goal
        String label = currentGoal.getLabel();
        if ("Task".equals(label)) {
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_task); // Pastikan drawable ini ada
        } else if ("Goal".equals(label)) {
            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_goal); // Pastikan drawable ini ada
        }
        // Tambahkan else jika ada kondisi label lain yang perlu background berbeda

        // Tangani klik pada tombol hapus (area merah) yang muncul setelah swipe
        // Ini memastikan bahwa jika user mengklik area merah setelah swipe, item akan dihapus
        holder.deleteButtonLayout.setOnClickListener(v -> {
            // Panggil metode deleteItem di adapter ini sendiri
            deleteItem(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    // Metode untuk menghapus item dari daftar goalList
    public void deleteItem(int position) {
        if (position >= 0 && position < goalList.size()) {
            Goal deletedGoal = goalList.get(position); // Dapatkan objek Goal yang akan dihapus
            goalList.remove(position); // Hapus dari daftar yang sedang ditampilkan adapter
            notifyItemRemoved(position); // Beri tahu RecyclerView bahwa item dihapus

            // Beri tahu Fragment bahwa item telah dihapus, kirim objek Goal yang dihapus
            if (deleteListener != null) {
                deleteListener.onGoalDeleted(deletedGoal); // Penting: kirim objek Goal
            }
            Toast.makeText(context, "Goal '" + deletedGoal.getTitle() + "' deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView labelTextView;
        CheckBox checkBox;
        RelativeLayout deleteButtonLayout; // Layout untuk tombol hapus

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text);
            labelTextView = itemView.findViewById(R.id.label_text);
            checkBox = itemView.findViewById(R.id.checkbox);
            deleteButtonLayout = itemView.findViewById(R.id.delete_button_layout); // Inisialisasi
        }
    }

    // Interface untuk komunikasi dari Adapter ke Fragment (perubahan status goal)
    public interface OnGoalStatusChangeListener {
        void onGoalStatusChanged();
    }

    // Interface baru untuk komunikasi dari Adapter ke Fragment (penghapusan goal)
    public interface OnGoalDeleteListener {
        void onGoalDeleted(Goal deletedGoal); // Mengirim objek Goal yang dihapus
    }
}


//// app/src/main/java/com/example/memomap/GoalAdapter.java
//package com.example.memomap;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
//
//    private List<Goal> goalList;
//    private OnGoalStatusChangeListener listener; // Listener untuk komunikasi ke Fragment
//
//    public GoalAdapter(List<Goal> goalList) {
//        this.goalList = goalList;
//    }
//
//    // Metode untuk memperbarui data di adapter
//    public void updateList(List<Goal> newList) {
//        this.goalList.clear();
//        this.goalList.addAll(newList);
//        notifyDataSetChanged(); // Memberi tahu RecyclerView untuk me-refresh
//    }
//
//    // Setter untuk listener
//    public void setOnGoalStatusChangeListener(OnGoalStatusChangeListener listener) {
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_goals, parent, false);
//        return new GoalViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
//        Goal currentGoal = goalList.get(position);
//
//        holder.titleTextView.setText(currentGoal.getTitle());
//        holder.labelTextView.setText(currentGoal.getLabel());
//
//        // Hapus listener sebelumnya sebelum mengatur yang baru untuk menghindari masalah daur ulang
//        holder.checkBox.setOnCheckedChangeListener(null);
//        holder.checkBox.setChecked(currentGoal.isCompleted());
//
//        // Atur listener baru
//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            currentGoal.setCompleted(isChecked);
//            // Beri tahu Fragment bahwa status goal telah berubah
//            if (listener != null) {
//                listener.onGoalStatusChanged();
//            }
//        });
//
//        // Logika untuk mengubah latar belakang label
//        String label = currentGoal.getLabel();
//        if ("Task".equals(label)) {
//            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_task);
//        } else if ("Goal".equals(label)) {
//            holder.labelTextView.setBackgroundResource(R.drawable.bg_label_goal);
//        }
//        // Tambahkan else jika ada kondisi label lain yang perlu background berbeda
//    }
//
//    @Override
//    public int getItemCount() {
//        return goalList.size();
//    }
//
//    public static class GoalViewHolder extends RecyclerView.ViewHolder {
//        TextView titleTextView;
//        TextView labelTextView;
//        CheckBox checkBox;
//
//        public GoalViewHolder(@NonNull View itemView) {
//            super(itemView);
//            titleTextView = itemView.findViewById(R.id.title_text);
//            labelTextView = itemView.findViewById(R.id.label_text);
//            checkBox = itemView.findViewById(R.id.checkbox);
//        }
//    }
//
//    // Interface untuk komunikasi dari Adapter ke Fragment
//    public interface OnGoalStatusChangeListener {
//        void onGoalStatusChanged();
//    }
//}