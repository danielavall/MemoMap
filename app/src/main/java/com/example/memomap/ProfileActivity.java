package com.example.memomap;

import static com.example.memomap.R.id.avatarImage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import android.util.Log;

public class ProfileActivity extends AppCompatActivity {

    private ImageView avatarImage;
    private Button btnEdit, btnSave, btnDiscard;
    private EditText etFirstName, etLastName, etEmail, etPhone, etDob;
    private RadioGroup genderGroup;

    LinearLayout editButtonGroup;

    private int selectedSkin = -1;
    private int selectedHair = -1;
    private int selectedFace = -1;
    private int selectedClothes = -1;
    private void setInputsEnabled(boolean enabled) {
        etFirstName.setEnabled(enabled);
        etLastName.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etDob.setEnabled(enabled);
        for (int i = 0; i < genderGroup.getChildCount(); i++) {
            genderGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    private void showEditButtons(boolean editing) {
        editButtonGroup.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(editing ? View.GONE : View.VISIBLE);
        btnSave.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnDiscard.setVisibility(editing ? View.VISIBLE : View.GONE);

        int bg = editing ? R.drawable.rounded_input_white : R.drawable.rounded_input_grey;

        etFirstName.setBackgroundResource(bg);
        etLastName.setBackgroundResource(bg);
        etEmail.setBackgroundResource(bg);
        etPhone.setBackgroundResource(bg);
        etDob.setBackgroundResource(bg);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editButtonGroup = findViewById(R.id.editButtonGroup);
        avatarImage = findViewById(R.id.avatarImage);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnDiscard = findViewById(R.id.btnDiscard);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        genderGroup = findViewById(R.id.genderGroup);

        setInputsEnabled(false);
        showEditButtons(false);

        Intent intent = getIntent();
        if (intent != null) {
            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            String avatarPath = prefs.getString("avatarPath", null);
            selectedSkin = intent.getIntExtra("skin", -1);
            selectedHair = intent.getIntExtra("hair", -1);
            selectedFace = intent.getIntExtra("face", -1);
            selectedClothes = intent.getIntExtra("clothes", -1);

            // Optional: Load avatar image into ImageView if needed
            if (avatarPath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
                if (bitmap != null) {
                    avatarImage.setImageBitmap(bitmap);
                }
            }
        }

        avatarImage.setOnClickListener(v -> {
            Intent avatarIntent = new Intent(ProfileActivity.this, AvatarActivity.class);
            avatarIntent.putExtra("skin", selectedSkin);
            avatarIntent.putExtra("hair", selectedHair);
            avatarIntent.putExtra("face", selectedFace);
            avatarIntent.putExtra("clothes", selectedClothes);
            startActivity(avatarIntent);
        });
        Log.d("AvatarActivity", "Received Skin=" + selectedSkin + ", Hair=" + selectedHair +
                ", Face=" + selectedFace + ", Clothes=" + selectedClothes);

        btnEdit.setOnClickListener(v -> {
            setInputsEnabled(true);
            showEditButtons(true);
        });

        btnDiscard.setOnClickListener(v -> {
            setInputsEnabled(false);
            showEditButtons(false);
        });

        btnSave.setOnClickListener(v -> {
            // Save to shared preferences or server here
            setInputsEnabled(false);
            showEditButtons(false);
        });

        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent Mainintent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(Mainintent);
            finish(); // Optional: finish ProfileActivity so it doesn’t stay in back stack
        });

        etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etDob.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

    }
}