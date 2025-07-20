package com.example.memomap;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class RecapCardTransform implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        float absPos = Math.abs(position);

        // Geser horizontal (default untuk swipe kanan-kiri)
        page.setTranslationX(-position * page.getWidth());

        // Efek menghilang secara halus + memperkecil tinggi
        page.setAlpha(1f - absPos);
        page.setScaleY(1f - 0.15f * absPos);
    }
}
