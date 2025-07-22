// app/src/main/java/com/example/memomap/SwipeToDeleteCallback.java
package com.example.memomap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
    private final ColorDrawable background;

    public SwipeToDeleteCallback(GoalAdapter adapter, int deleteIconResId) {
        super(0, ItemTouchHelper.LEFT); // Hanya mengaktifkan geser ke kiri
        mAdapter = adapter;
        // Gunakan context dari adapter untuk mendapatkan drawable
        this.deleteIcon = ContextCompat.getDrawable(mAdapter.getItemViewContext(), deleteIconResId);
        background = new ColorDrawable(Color.RED); // Latar belakang merah
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Tidak digunakan untuk fungsionalitas swipe-to-delete
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        // Menggunakan nilai -1 secara langsung sebagai pengganti RecyclerView.NoPosition
        if (position != -1) { // Perubahan di sini!
            // Panggil metode delete item di adapter
            mAdapter.deleteItem(position);
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // Hanya izinkan swipe ke kiri
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;

        RelativeLayout deleteButtonLayout = itemView.findViewById(R.id.delete_button_layout);
        View mainContentLayout = itemView.findViewById(R.id.main_content_layout);

        if (dX < 0) { // Jika digeser ke kiri
            // Pindahkan main content
            mainContentLayout.setTranslationX(dX);
            // Tampilkan delete button layout
            deleteButtonLayout.setVisibility(View.VISIBLE);

            // Gambar latar belakang merah
            background.setBounds(itemView.getRight() + (int) dX,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom());
            background.draw(c);

            // Gambar ikon hapus
            int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            deleteIcon.draw(c);
        } else {
            // Jika tidak digeser atau digeser ke kanan, pastikan main content di posisi semula dan tombol hapus disembunyikan
            mainContentLayout.setTranslationX(0);
            deleteButtonLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        // Pastikan main content selalu di atas
        viewHolder.itemView.findViewById(R.id.main_content_layout).bringToFront();
    }
}