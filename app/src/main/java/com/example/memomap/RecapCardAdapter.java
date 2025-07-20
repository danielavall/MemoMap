package com.example.memomap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// import android.widget.ImageButton; // ImageButton tidak lagi dibutuhkan di sini
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecapCardAdapter extends RecyclerView.Adapter<RecapCardAdapter.RecapCardViewHolder> {

    private Context context;
    private int[] imageResources;
    // private Runnable backButtonCallback; // Hapus ini, callback tidak lagi di adapter

    // MODIFIKASI KONSTRUKTOR: Hapus parameter backButtonCallback
    public RecapCardAdapter(Context context, int[] imageResources) {
        this.context = context;
        this.imageResources = imageResources;
        // this.backButtonCallback = backButtonCallback; // Hapus inisialisasi ini
    }

    @NonNull
    @Override
    public RecapCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Pastikan R.layout.recap_card tidak memiliki ImageButton untuk tombol kembali
        View view = LayoutInflater.from(context).inflate(R.layout.recap_card, parent, false);
        return new RecapCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecapCardViewHolder holder, int position) {
        holder.imageViewCard.setImageResource(imageResources[position]);

        // HAPUS SEMUA KODE BERIKUT INI, KARENA TOMBOL BACK TIDAK LAGI DI KARTU
        // holder.backButtonOnCard.setOnClickListener(v -> {
        //     if (backButtonCallback != null) {
        //         backButtonCallback.run();
        //     }
        // });
        // holder.backButtonOnCard.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return imageResources.length;
    }

    public static class RecapCardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCard;
        // HAPUS DEKLARASI ImageButton backButtonOnCard;
        // ImageButton backButtonOnCard;

        public RecapCardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
        }
    }
}