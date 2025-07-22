package com.example.memomap;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OneLineActivity extends AppCompatActivity {

    private TextView tvTime;
    private EditText etDailyNote;
    private TextView tvCharCount;
    private Button btnSave;
    private LinearLayout mediaContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_line);

        // Initialize views
        tvTime = findViewById(R.id.tvTime);
        etDailyNote = findViewById(R.id.etDailyNote);
        tvCharCount = findViewById(R.id.tvCharCount);
        btnSave = findViewById(R.id.btnSave);
        mediaContainer = findViewById(R.id.mediaContainer);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Set current time
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        tvTime.setText(currentTime);

        // Character counter
        etDailyNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(s.length() + "/56");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button click
        btnSave.setOnClickListener(v -> saveEntry());

        // Back button
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(OneLineActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize media items (photos/audio)
        setupMediaItems();
    }

    private void saveEntry() {
        String note = etDailyNote.getText().toString();
        String time = tvTime.getText().toString();

        // TODO: Save to database or SharedPreferences
        // Example:
        // DailyEntry entry = new DailyEntry(note, time, mediaPaths);
        // database.saveEntry(entry);

        finish(); // Return to previous activity
    }

    private void setupMediaItems() {
        // Add photo/image views
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(60),
                    dpToPx(60)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.ic_add_media);
            imageView.setOnClickListener(v -> openMediaPicker());
            mediaContainer.addView(imageView);
        }
    }

    private void openMediaPicker() {
        // TODO: Implement media picker intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* audio/*");
        startActivityForResult(intent, 1);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            // Handle selected media
        }
    }

    etDailyNote.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tvCharCounter.setText(s.length() + "/36");
        }

        @Override
        public void afterTextChanged(Editable s) {}
    });
}