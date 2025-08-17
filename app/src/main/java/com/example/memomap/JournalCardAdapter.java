package com.example.memomap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JournalCardAdapter extends RecyclerView.Adapter<JournalCardAdapter.ViewHolder> {
    private final List<JournalCardModel> journalList;
    private OnItemClickListener listener;

    public JournalCardAdapter(List<JournalCardModel> journalList) {
        this.journalList = journalList;
    }


    @NonNull
    @Override
    public JournalCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_journal_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalCardAdapter.ViewHolder holder, int position) {
        JournalCardModel item = journalList.get(position);

        // Set data seperti sebelumnya
        holder.day.setText(item.getDay());
        holder.date.setText(item.getDate());
        holder.month.setText(item.getMonth());
        holder.description.setText(item.getDescription());

        Context context = holder.itemView.getContext();
        Resources res = context.getResources();

        String photoText = res.getQuantityString(
                R.plurals.photo_count_plural,
                item.getPhotoCount(),
                item.getPhotoCount()
        );
        String videoText = res.getQuantityString(
                R.plurals.video_count_plural,
                item.getVideoCount(),
                item.getVideoCount()
        );

        holder.photoCount.setText(photoText);
        holder.videoCount.setText(videoText);

        holder.cardViewContainer.setCardBackgroundColor(context.getColor(item.getBackgroundColor()));

        // ✅ Tambahkan listener klik
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView day, date, month, description, photoCount, videoCount;
        CardView cardViewContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            description = itemView.findViewById(R.id.description);
            photoCount = itemView.findViewById(R.id.photo_count);
            videoCount = itemView.findViewById(R.id.video_count);
            cardViewContainer = (CardView) itemView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(JournalCardModel item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
