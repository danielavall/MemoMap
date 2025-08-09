package com.example.memomap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private final List<CalendarModel> monthsData = new ArrayList<>();

    // Updated TextView variables to match new XML IDs
    private TextView textYearPrev2; // Corresponds to text_year_prev_2 (e.g., 2023)
    private TextView textYearPrev1; // Corresponds to text_year_prev_1 (e.g., 2024)
    private TextView textCurrentYear; // Corresponds to text_current_year (e.g., 2025)
    private TextView textYearNext1; // Corresponds to text_year_next_1 (e.g., 2026)
    private TextView textYearNext2; // Corresponds to text_year_next_2 (e.g., 2027)

    private ImageButton btnPrevYear; // Corresponds to btn_previous in XML
    private ImageButton btnNextYear; // Corresponds to btn_next in XML

    private int currentDisplayedYear;

    private Calendar todayCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_home);

        todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);

        currentDisplayedYear = todayCalendar.get(Calendar.YEAR);

        recyclerView = findViewById(R.id.recycler_view_item_calendar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Find the new UI elements using their correct IDs
        textYearPrev2 = findViewById(R.id.text_year_prev_2);
        textYearPrev1 = findViewById(R.id.text_year_prev_1);
        textCurrentYear = findViewById(R.id.text_current_year);
        textYearNext1 = findViewById(R.id.text_year_next_1);
        textYearNext2 = findViewById(R.id.text_year_next_2);

        btnPrevYear = findViewById(R.id.btn_previous);
        btnNextYear = findViewById(R.id.btn_next);

        // Load initial month data for the current year
        loadMonthsDataForYear(currentDisplayedYear);

        adapter = new CalendarAdapter(monthsData);
        recyclerView.setAdapter(adapter);

        // Set Click Listeners for year navigation buttons
        btnPrevYear.setOnClickListener(v -> navigateYear(-1));
        btnNextYear.setOnClickListener(v -> navigateYear(1));

        // Set Click Listeners for the year TextViews themselves
        // Use checkAndHandleYearClick to manage clickability based on future status
        textYearPrev2.setOnClickListener(v -> checkAndHandleYearClick(textYearPrev2));
        textYearPrev1.setOnClickListener(v -> checkAndHandleYearClick(textYearPrev1));
        textCurrentYear.setOnClickListener(v -> checkAndHandleYearClick(textCurrentYear)); // Current year might be clickable too
        textYearNext1.setOnClickListener(v -> checkAndHandleYearClick(textYearNext1));
        textYearNext2.setOnClickListener(v -> checkAndHandleYearClick(textYearNext2));


        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarHomeActivity.this, MainActivity.class);
            intent.putExtra("navigate_to", "home");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        updateYearUI(); // Update initial display
    }

    private void loadMonthsDataForYear(int year) {
        monthsData.clear();
        currentDisplayedYear = year;

        Calendar calendarForMonth = Calendar.getInstance();
        calendarForMonth.set(Calendar.YEAR, year);
        calendarForMonth.set(Calendar.MONTH, Calendar.JANUARY);
        calendarForMonth.set(Calendar.DAY_OF_MONTH, 1);
        calendarForMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarForMonth.set(Calendar.MINUTE, 0);
        calendarForMonth.set(Calendar.SECOND, 0);
        calendarForMonth.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMM", Locale.getDefault());

        for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
            calendarForMonth.set(Calendar.MONTH, month);

            String monthName = monthNameFormat.format(calendarForMonth.getTime()).toUpperCase();
            int daysInMonth = calendarForMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            int firstDayOfWeek = calendarForMonth.get(Calendar.DAY_OF_WEEK);

            Map<Integer, Integer> coloredDates = new HashMap<>();

            // --- IMPORTANT: YOUR JOURNAL DATA FETCHING LOGIC GOES HERE ---
            // This is where you would populate 'coloredDates' based on ACTUAL journal data
            // from your database for this specific 'month' and 'year'.
            //
            // Example dummy data (expand this significantly for your app):
            if (year == 2025) {
                if (month == Calendar.JANUARY) {
                    coloredDates.put(1, R.color.orange);
                    coloredDates.put(2, R.color.pink);
                    coloredDates.put(3, R.color.pink);
                    coloredDates.put(4, R.color.purple);
                    coloredDates.put(5, R.color.blue);
                    coloredDates.put(6, R.color.blue);
                    coloredDates.put(7, R.color.green);
                    coloredDates.put(8, R.color.purple);
                    coloredDates.put(9, R.color.blue);
                    coloredDates.put(10, R.color.pink);
                    coloredDates.put(11, R.color.green);
                    coloredDates.put(12, R.color.green);
                    coloredDates.put(13, R.color.blue);
                    coloredDates.put(14, R.color.orange);
                    coloredDates.put(15, R.color.green);
                    coloredDates.put(16, R.color.purple);
                    coloredDates.put(17, R.color.orange);
                    coloredDates.put(18, R.color.pink);
                    coloredDates.put(19, R.color.green);
                    coloredDates.put(20, R.color.blue);
                }
            } else if (year == 2024) {
                if (month == Calendar.DECEMBER) {
                    coloredDates.put(25, R.color.purple);
                }
            } else if (year == 2023) {
                if (month == Calendar.NOVEMBER) {
                    coloredDates.put(1, R.color.green);
                }
                if (month == Calendar.DECEMBER) {
                    coloredDates.put(10, R.color.blue);
                }
            }
            // --- END OF JOURNAL DATA LOGIC ---

            monthsData.add(new CalendarModel(monthName, year, firstDayOfWeek, daysInMonth, coloredDates));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateYearUI() {
        // Update the current year TextView
        textCurrentYear.setText(String.valueOf(currentDisplayedYear));
        applyYearTextStyle(textCurrentYear, currentDisplayedYear); // Apply style to current year

        // Update and style previous years
        int prevYear1 = currentDisplayedYear - 1;
        textYearPrev1.setText(String.valueOf(prevYear1));
        applyYearTextStyle(textYearPrev1, prevYear1);

        int prevYear2 = currentDisplayedYear - 2;
        textYearPrev2.setText(String.valueOf(prevYear2));
        applyYearTextStyle(textYearPrev2, prevYear2);

        // Update and style next years
        int nextYear1 = currentDisplayedYear + 1;
        textYearNext1.setText(String.valueOf(nextYear1));
        applyYearTextStyle(textYearNext1, nextYear1);

        int nextYear2 = currentDisplayedYear + 2;
        textYearNext2.setText(String.valueOf(nextYear2));
        applyYearTextStyle(textYearNext2, nextYear2);


        // Control Next/Previous Year buttons
        btnPrevYear.setEnabled(true);
        btnPrevYear.setAlpha(1.0f); // Always enabled unless you set a hard limit for past years

        // Disable btnNextYear if currentDisplayedYear is the actual current year or beyond
        if (currentDisplayedYear >= todayCalendar.get(Calendar.YEAR)) {
            btnNextYear.setEnabled(false);
            btnNextYear.setAlpha(0.5f);
        } else {
            btnNextYear.setEnabled(true);
            btnNextYear.setAlpha(1.0f);
        }
    }

    /**
     * Applies styling (color and clickability) to a TextView based on whether
     * the associated year is in the future relative to `todayCalendar`.
     * @param textView The TextView to style.
     * @param yearToCheck The year to check.
     */
    private void applyYearTextStyle(TextView textView, int yearToCheck) {
        if (yearToCheck > todayCalendar.get(Calendar.YEAR)) {
            textView.setTextColor(getResources().getColor(R.color.grey, null));
            textView.setClickable(false);
            textView.setEnabled(false);
        } else {
            textView.setTextColor(getResources().getColor(R.color.black, null));
            textView.setClickable(true);
            textView.setEnabled(true);
        }
    }


    /**
     * Navigates to the previous or next year.
     *
     * @param direction -1 for previous year, 1 for next year.
     */
    private void navigateYear(int direction) {
        int newYear = currentDisplayedYear + direction;

        // Prevent navigating to years in the future beyond today's year
        if (direction > 0 && newYear > todayCalendar.get(Calendar.YEAR)) {
            Toast.makeText(this, "Tidak ada entri untuk tahun " + newYear + " karena masih di masa depan.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadMonthsDataForYear(newYear);
        updateYearUI();
    }

    /**
     * Handles clicks on year TextViews, checking if they are enabled before proceeding.
     * @param clickedYearTextView The TextView that was clicked.
     */
    private void checkAndHandleYearClick(TextView clickedYearTextView) {
        if (clickedYearTextView.isEnabled()) {
            int targetYear = Integer.parseInt(clickedYearTextView.getText().toString());
            // No need for a Toast here as the `MapsYear` will handle the future check and Toast
            navigateYear(targetYear - currentDisplayedYear); // Calculate direction
        } else {
            Toast.makeText(this, "Tahun ini belum tersedia.", Toast.LENGTH_SHORT).show();
        }
    }
}