package com.example.memomap;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingRecap extends AppCompatActivity {

    private static final int REQUEST_CALENDAR = 100;

    private ImageView boat;
    private RelativeLayout layoutLoading;
    private ScrollView layoutRecap;

    private Button yearButton, monthButton, weekButton, createButton;

    private int selectedYear = -1;
    private int selectedMonth = -1;
    private int selectedWeek = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recap);

        boat = findViewById(R.id.boat);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutRecap = findViewById(R.id.layoutRecap);

        yearButton = findViewById(R.id.yearButton);
        monthButton = findViewById(R.id.monthButton);
        weekButton = findViewById(R.id.weekButton);
        createButton = findViewById(R.id.createButton);

        layoutLoading.setVisibility(View.VISIBLE);
        layoutRecap.setVisibility(View.GONE);
        boat.setVisibility(View.GONE);

        setupCalendarNavigation();

        createButton.setOnClickListener(v -> {
            if (selectedYear == -1) return;

            layoutRecap.setVisibility(View.GONE);
            layoutLoading.setVisibility(View.VISIBLE);

            // Sembunyikan panah dan teks
            findViewById(R.id.arrowHint).setVisibility(View.GONE);
            findViewById(R.id.textHint).setVisibility(View.GONE);

            // Tampilkan dan animasikan boat
            boat.setVisibility(View.VISIBLE);
            animateBoat();

            boat.postDelayed(() -> {
                layoutLoading.setVisibility(View.GONE);
                layoutRecap.setVisibility(View.VISIBLE);
                boat.setVisibility(View.GONE);

                yearButton.setText(String.valueOf(selectedYear));
                monthButton.setText(getMonthName(selectedMonth));
                weekButton.setText("Week " + selectedWeek);
            }, 3000);
        });
    }

    private void setupCalendarNavigation() {
        View.OnClickListener openCalendar = v -> {
            Intent intent = new Intent(LoadingRecap.this, CalendarRecap.class);
            startActivityForResult(intent, REQUEST_CALENDAR);
        };

        yearButton.setOnClickListener(openCalendar);
        monthButton.setOnClickListener(openCalendar);
        weekButton.setOnClickListener(openCalendar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CALENDAR && resultCode == RESULT_OK) {
            selectedYear = data.getIntExtra("year", -1);
            selectedMonth = data.getIntExtra("month", -1);
            selectedWeek = data.getIntExtra("week", -1);
        }
    }

    private void animateBoat() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(boat, "translationX", -30f, 30f);
        animator.setDuration(2000);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    private String getMonthName(int monthIndex) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return (monthIndex >= 0 && monthIndex < months.length) ? months[monthIndex] : "Month";
    }
}
