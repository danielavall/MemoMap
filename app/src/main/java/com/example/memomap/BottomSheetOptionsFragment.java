package com.example.memomap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetOptionsFragment extends BottomSheetDialogFragment {

    private ImageView ivGallery;
    private ImageView ivAudio;
    private ImageView ivCamera;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the Bottom Sheet
        View rootView = inflater.inflate(R.layout.fragment_bottom_sheet_options, container, false);

        ivGallery = rootView.findViewById(R.id.ivGallery);
        ivAudio = rootView.findViewById(R.id.ivAudio);
        ivCamera = rootView.findViewById(R.id.ivCamera);

        // Set OnClickListener for each button
        ivGallery.setOnClickListener(v -> {
            // Logika untuk Galeri
            dismiss();  // Dismiss the Bottom Sheet after selecting option
        });

        ivAudio.setOnClickListener(v -> {
            // Logika untuk Audio
            dismiss();  // Dismiss the Bottom Sheet after selecting option
        });

        ivCamera.setOnClickListener(v -> {
            // Logika untuk Kamera
            dismiss();  // Dismiss the Bottom Sheet after selecting option
        });

        return rootView;
    }
}
