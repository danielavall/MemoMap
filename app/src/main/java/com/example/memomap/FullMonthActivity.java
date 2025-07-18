// FullMonthActivity.java
package com.example.memomap;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FullMonthActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView title;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_month);

        gridLayout = findViewById(R.id.gridFullMonth);
        title = findViewById(R.id.title);
        btnBack = findViewById(R.id.btn_back);

        // Ambil data dari Intent
        Intent intent = getIntent();
        String monthName = intent.getStringExtra("month"); // e.g., "JAN"
        int year = intent.getIntExtra("year", 2025);
        HashMap<Integer, Integer> coloredDates = (HashMap<Integer, Integer>)
                intent.getSerializableExtra("coloredDates");

        title.setText(String.valueOf(year));

        // Tampilkan tanggal-tanggal
        renderMonth(monthName, year, coloredDates);

        // Tombol back
        btnBack.setOnClickListener(v -> finish());
    }

    private void renderMonth(String monthName, int year, Map<Integer, Integer> coloredDates) {
        int month = getMonthIndex(monthName); // Convert "JAN" to 0

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Sunday = 1
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Hari-hari dalam seminggu
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};

        gridLayout.removeAllViews(); // tetap di sini

        for (String day : days) {
            TextView dayLabel = new TextView(this);
            dayLabel.setText(day);
            dayLabel.setTextSize(16);
            dayLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            dayLabel.setTextColor(getResources().getColor(R.color.grey));
            dayLabel.setGravity(android.view.Gravity.CENTER);

            GridLayout.LayoutParams labelParams = new GridLayout.LayoutParams();
            labelParams.width = 0;
            labelParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            labelParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            labelParams.setMargins(0, 32, 0, 32);
            dayLabel.setLayoutParams(labelParams);

            gridLayout.addView(dayLabel);
        }

        // Add empty views for days before the 1st of the month
        for (int i = 1; i < firstDayOfWeek; i++) {
            TextView emptyView = new TextView(this);
            GridLayout.LayoutParams emptyParams = new GridLayout.LayoutParams();
            emptyParams.width = 0;
            emptyParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            emptyParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyView.setLayoutParams(emptyParams);
            gridLayout.addView(emptyView);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayView = new TextView(this);
            dayView.setText(String.valueOf(day));
            dayView.setTextSize(16);
            dayView.setTextColor(getResources().getColor(R.color.black));
            dayView.setGravity(android.view.Gravity.CENTER);
            dayView.setPadding(0, 16, 0, 16);

            if (coloredDates != null && coloredDates.containsKey(day)) {
                int colorResId = coloredDates.get(day);
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(getResources().getColor(colorResId));
                bg.setCornerRadius(50); // bulat
                dayView.setBackground(bg);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // gunakan weight agar rata
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8); // jarak antar tanggal
            dayView.setLayoutParams(params);

            gridLayout.addView(dayView);
        }

    }

    private int getMonthIndex(String monthAbbr) {
        switch (monthAbbr.toUpperCase()) {
            case "JAN": return 0;
            case "FEB": return 1;
            case "MAR": return 2;
            case "APR": return 3;
            case "MAY": return 4;
            case "JUN": return 5;
            case "JUL": return 6;
            case "AUG": return 7;
            case "SEP": return 8;
            case "OCT": return 9;
            case "NOV": return 10;
            case "DEC": return 11;
            default: return 0;
        }
    }
}
