// app/src/main/java/com/example/memomap/SwipeToDeleteCallback.java
package com.example.memomap;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final GoalAdapter mAdapter;
    private final Drawable deleteIcon;
    private final Drawable background;

    public SwipeToDeleteCallback(GoalAdapter adapter, int deleteIconResId) {
        super(0, ItemTouchHelper.LEFT);
        mAdapter = adapter;
        this.deleteIcon = ContextCompat.getDrawable(mAdapter.getItemViewContext(), deleteIconResId);
        this.background = ContextCompat.getDrawable(mAdapter.getItemViewContext(), R.drawable.rounded_input_red);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            mAdapter.deleteItem(position);
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // --- PENTING: Panggil super.onChildDraw() di awal ---
        // Ini akan menangani translasi itemView secara default.
        // Logika kustom Anda akan menggambar di bawahnya.
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        // Jika item dalam keadaan diam (IDLE), tidak perlu menggambar latar belakang kustom.
        // super.onChildDraw() di atas sudah cukup.
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            return; // Keluar dari metode ini
        }

        View itemView = viewHolder.itemView;
        RelativeLayout deleteButtonLayout = itemView.findViewById(R.id.delete_button_layout);

        if (dX < 0) { // Jika digeser ke kiri (dX negatif)
            // --- KODE UNTUK MENGGAMBAR BACKGROUND MERAH ---
            // background.setBounds harus dihitung berdasarkan posisi itemView yang sudah digeser oleh super.onChildDraw()
            // atau dihitung agar tetap "fixed" di kanan.
            // Karena mainContentLayout sekarang digeser oleh super, kita bisa menggambar background di area yang terbuka.

            // Hitung area yang terbuka di sebelah kanan
            int right = itemView.getRight();
            int left = itemView.getRight() + (int) dX; // dX negatif, jadi left akan lebih kecil dari right

            background.setBounds(left, itemView.getTop(), right, itemView.getBottom());
            background.draw(c);

            // --- KODE UNTUK MENGGAMBAR IKON TONG SAMPAH ---
            int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
            // Posisi ikon juga relatif terhadap area yang terbuka
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            deleteIcon.draw(c);

            // Pastikan deleteButtonLayout dari XML Anda tetap tidak terlihat (invisible),
            // karena kita menggambar sendiri background dan ikon trash.
            if (deleteButtonLayout != null) {
                deleteButtonLayout.setVisibility(View.INVISIBLE);
            }

        } else { // Jika tidak digeser atau digeser ke kanan (dX >= 0)
            // Pastikan deleteButtonLayout tersembunyi jika tidak ada swipe
            if (deleteButtonLayout != null) {
                deleteButtonLayout.setVisibility(View.INVISIBLE);
            }
        }
        // Tidak perlu memanggil super.onChildDraw() lagi di sini.
        // mainContentLayout.setTranslationX(0); juga tidak perlu di else, karena super.onChildDraw() menanganinya.
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // Hapus panggilan super.onChildDrawOver() jika tidak ada efek khusus yang ditambahkan ItemTouchHelper di atas item.
        // Hapus juga bringToFront() karena super.onChildDraw() di onChildDraw sudah menangani z-order.
        // super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        // viewHolder.itemView.findViewById(R.id.main_content_layout).bringToFront(); // Hapus ini
    }
}

//// app/src/main/java/com/example/memomap/SwipeToDeleteCallback.java
//package com.example.memomap;
//
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.view.View;
//import android.widget.RelativeLayout;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
//
//    private final GoalAdapter mAdapter;
//    private final Drawable deleteIcon;
//    private final Drawable background;
//
//    public SwipeToDeleteCallback(GoalAdapter adapter, int deleteIconResId) {
//        super(0, ItemTouchHelper.LEFT);
//        mAdapter = adapter;
//        this.deleteIcon = ContextCompat.getDrawable(mAdapter.getItemViewContext(), deleteIconResId);
//        this.background = ContextCompat.getDrawable(mAdapter.getItemViewContext(), R.drawable.rounded_input_red);
//    }
//
//    @Override
//    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//        return false;
//    }
//
//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//        int position = viewHolder.getAdapterPosition();
//        if (position != RecyclerView.NO_POSITION) {
//            mAdapter.deleteItem(position);
//        }
//    }
//
//    @Override
//    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        return makeMovementFlags(0, ItemTouchHelper.LEFT);
//    }
//
//    @Override
//    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        // --- PERBAIKAN PENTING DI SINI: KONDISI UNTUK IDLE STATE ---
//        // Jika item dalam keadaan diam (IDLE), biarkan ItemTouchHelper menangani penggambarannya secara default.
//        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            return; // Keluar dari metode ini setelah super call
//        }
//        // --- AKHIR PERBAIKAN ---
//
//        View itemView = viewHolder.itemView;
//        View mainContentLayout = itemView.findViewById(R.id.main_content_layout);
//        RelativeLayout deleteButtonLayout = itemView.findViewById(R.id.delete_button_layout);
//
//        // Terapkan translasi pada layout konten utama.
//        mainContentLayout.setTranslationX(dX);
//
//        if (dX < 0) { // Jika digeser ke kiri (dX negatif)
//            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
//            background.draw(c);
//
//            int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
//            int iconTop = itemView.getTop() + iconMargin;
//            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
//            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
//            int iconRight = itemView.getRight() - iconMargin;
//
//            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//            deleteIcon.draw(c);
//
//            if (deleteButtonLayout != null) {
//                deleteButtonLayout.setVisibility(View.INVISIBLE);
//            }
//
//        } else { // Jika tidak digeser atau digeser ke kanan
//            mainContentLayout.setTranslationX(0); // Pastikan konten utama di posisi semula
//            if (deleteButtonLayout != null) {
//                deleteButtonLayout.setVisibility(View.INVISIBLE);
//            }
//        }
//        // PENTING: Jangan panggil super.onChildDraw() di sini untuk state SWIPE/DRAG
//    }
//
//    @Override
//    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        viewHolder.itemView.findViewById(R.id.main_content_layout).bringToFront();
//    }
//}