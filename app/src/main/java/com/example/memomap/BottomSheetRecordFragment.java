package com.example.memomap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetRecordFragment extends BottomSheetDialogFragment {

    private TextView tvRecordingTime;
    private boolean isRecording = false;
    private int seconds = 0;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                seconds++;
                tvRecordingTime.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk Bottom Sheet
        View rootView = inflater.inflate(R.layout.fragment_bottom_sheet_record, container, false);

        tvRecordingTime = rootView.findViewById(R.id.tvRecordingTime);

        // Start recording button
        rootView.findViewById(R.id.ivStartRecording).setOnClickListener(v -> {
            if (!isRecording) {
                // Logic untuk mulai rekaman
                isRecording = true;
                handler.post(updateTimeRunnable); // Mulai timer
            } else {
                // Logic untuk berhenti rekaman
                isRecording = false;
                handler.removeCallbacks(updateTimeRunnable); // Hentikan timer
            }
        });

        return rootView;
    }
}
