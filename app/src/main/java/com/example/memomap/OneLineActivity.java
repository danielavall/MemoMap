package com.example.memomap;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.PopupWindow;


import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OneLineActivity extends AppCompatActivity {

    private EditText etDailyNote;
    private TextView tvCharCount;
    private ImageView ivMic; // Mikrofon button
    private ImageView ivAttach; // attach button
    private ImageView ivEmoji; // attach button
    private Button btnSave;
    private LinearLayout mediaContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_line);

        ivMic = findViewById(R.id.ivMic); // Inisialisasi dengan ID yang sesuai di XML

        ivMic.setOnClickListener(v -> {
            // Menampilkan Bottom Sheet ketika tombol mikrofon diklik
            BottomSheetRecordFragment bottomSheet = new BottomSheetRecordFragment();
            bottomSheet.show(getSupportFragmentManager(), "BottomSheetRecordFragment");
        });

        ivAttach = findViewById(R.id.ivAttach); // Temukan ImageView mikrofon

        ivAttach.setOnClickListener(v -> {
            // Menampilkan Bottom Sheet ketika tombol mikrofon diklik
            BottomSheetOptionsFragment bottomSheet = new BottomSheetOptionsFragment();
            bottomSheet.show(getSupportFragmentManager(), "BottomSheetOptionsFragment");
        });

        ivEmoji = findViewById(R.id.ivEmoji);

        ivEmoji.setOnClickListener(v -> {
            // Inflate layout pop-up emoji
            View popupView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_emoji, null);

            // Buat PopupWindow
            final PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true); // true: bisa ditutup otomatis saat klik di luar

            // Handle klik pada masing-masing emoji
            setEmojiClick(popupView, R.id.ivSad, R.drawable.ic_emote_sad_green, popupWindow, ivEmoji);
            setEmojiClick(popupView, R.id.ivCry, R.drawable.ic_emote_cry_blue, popupWindow, ivEmoji);
            setEmojiClick(popupView, R.id.ivNeutral, R.drawable.ic_emote_neutral_orange, popupWindow, ivEmoji);
            setEmojiClick(popupView, R.id.ivHappy, R.drawable.ic_emote_happy_purple, popupWindow, ivEmoji);
            setEmojiClick(popupView, R.id.ivSmile, R.drawable.ic_emote_smile_pink, popupWindow, ivEmoji);

            // Tampilkan pop-up di atas tombol
            popupWindow.showAsDropDown(ivEmoji, 0, -ivEmoji.getHeight() * 3); // Y offset agar naik ke atas
        });


        //Inisialisasi semua view yang digunakan
        etDailyNote = findViewById(R.id.etDailyNote);
        tvCharCount = findViewById(R.id.tvCharCount);
        btnSave = findViewById(R.id.btnSave);
        mediaContainer = findViewById(R.id.mediaContainer);

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

        // Initialize media items (photos/audio)
        setupMediaItems();

        // Close button
        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            Intent intent = new Intent(OneLineActivity.this, MainActivity.class);
            intent.putExtra("go_home", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

    }

    private void setEmojiClick(View popupView, int emojiId, int emojiDrawable, PopupWindow popupWindow, ImageView ivEmoji) {
        ImageView emojiView = popupView.findViewById(emojiId);
        emojiView.setOnClickListener(v -> {
            ivEmoji.setImageResource(emojiDrawable);
            popupWindow.dismiss();
        });
    }


    private void saveEntry() {
        String note = etDailyNote.getText().toString();

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
}