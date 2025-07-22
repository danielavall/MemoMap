package com.example.memomap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FullMonthActivity extends AppCompatActivity {

    private TextView titleYearTextView;
    private TextView textMonthPrev2;
    private TextView textMonthPrev1;
    private TextView textMonthCurrent;
    private TextView textMonthNext1;
    private TextView textMonthNext2;
    private ImageButton btnPreviousMonth;
    private ImageButton btnNextMonth;
    private GridLayout gridFullMonth;

    private Calendar currentDisplayCalendar; // Tracks the month currently shown in this activity
    private Calendar todayCalendar;          // Reference for today's date, to determine future months

    private Map<Integer, Integer> coloredDatesData; // Data for colored days in the current month

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_month);

        // Initialize todayCalendar to the desired 'current' date (Jan 21, 2025)
        // This will dictate what counts as 'future' for this activity.
        todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.YEAR, 2025);
        todayCalendar.set(Calendar.MONTH, Calendar.JANUARY); // January is 0
        todayCalendar.set(Calendar.DAY_OF_MONTH, 21); // Set to 21st day for example (Jan 21, 2025)
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);

        // Get data from the Intent (passed from CalendarAdapter)
        Intent intent = getIntent();
        String initialMonthName = intent.getStringExtra("month"); // e.g., "JAN"
        int initialYear = intent.getIntExtra("year", todayCalendar.get(Calendar.YEAR)); // Default to current year

        // Initialize currentDisplayCalendar based on received intent data
        currentDisplayCalendar = Calendar.getInstance();
        currentDisplayCalendar.set(Calendar.YEAR, initialYear);
        currentDisplayCalendar.set(Calendar.MONTH, getMonthNumber(initialMonthName));
        currentDisplayCalendar.set(Calendar.DAY_OF_MONTH, 1); // Always set to 1st day for month view
        currentDisplayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentDisplayCalendar.set(Calendar.MINUTE, 0);
        currentDisplayCalendar.set(Calendar.SECOND, 0);
        currentDisplayCalendar.set(Calendar.MILLISECOND, 0);


        // Find views
        titleYearTextView = findViewById(R.id.title);
        textMonthPrev2 = findViewById(R.id.text_month_prev_2);
        textMonthPrev1 = findViewById(R.id.text_month_prev_1);
        textMonthCurrent = findViewById(R.id.text_month_current);
        textMonthNext1 = findViewById(R.id.text_month_next_1);
        textMonthNext2 = findViewById(R.id.text_month_next_2);
        btnPreviousMonth = findViewById(R.id.btn_previous);
        btnNextMonth = findViewById(R.id.btn_next);
        gridFullMonth = findViewById(R.id.gridFullMonth);

        // Set listeners for month navigation
        btnPreviousMonth.setOnClickListener(v -> navigateMonth(-1));
        btnNextMonth.setOnClickListener(v -> navigateMonth(1));
        textMonthPrev2.setOnClickListener(v -> checkAndHandleMonthClick(textMonthPrev2));
        textMonthPrev1.setOnClickListener(v -> checkAndHandleMonthClick(textMonthPrev1));
        textMonthNext1.setOnClickListener(v -> checkAndHandleMonthClick(textMonthNext1));
        textMonthNext2.setOnClickListener(v -> checkAndHandleMonthClick(textMonthNext2));

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Initial loading of colored dates and UI update
        loadColoredDatesForMonth(currentDisplayCalendar.get(Calendar.MONTH), currentDisplayCalendar.get(Calendar.YEAR));
        updateFullMonthUI();
    }

    /**
     * Updates the entire FullMonthActivity UI: year title, month selector, and day grid.
     */
    private void updateFullMonthUI() {
        titleYearTextView.setText(String.valueOf(currentDisplayCalendar.get(Calendar.YEAR)));
        updateMonthSelectorUI();
        populateDayGrid();
    }

    /**
     * Updates the month TextViews in the selector and manages their styling/clickability.
     */
    private void updateMonthSelectorUI() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

        String currentMonthName = monthFormat.format(currentDisplayCalendar.getTime()).toUpperCase();
        textMonthCurrent.setText(currentMonthName);
        textMonthCurrent.setTextColor(ContextCompat.getColor(this, R.color.black));
        textMonthCurrent.setClickable(true);
        textMonthCurrent.setEnabled(true);

        Calendar prev1MonthCal = (Calendar) currentDisplayCalendar.clone();
        prev1MonthCal.add(Calendar.MONTH, -1);
        textMonthPrev1.setText(monthFormat.format(prev1MonthCal.getTime()).toUpperCase());
        applyMonthStyle(textMonthPrev1, prev1MonthCal);

        Calendar prev2MonthCal = (Calendar) currentDisplayCalendar.clone();
        prev2MonthCal.add(Calendar.MONTH, -2);
        textMonthPrev2.setText(monthFormat.format(prev2MonthCal.getTime()).toUpperCase());
        applyMonthStyle(textMonthPrev2, prev2MonthCal);

        Calendar next1MonthCal = (Calendar) currentDisplayCalendar.clone();
        next1MonthCal.add(Calendar.MONTH, 1);
        textMonthNext1.setText(monthFormat.format(next1MonthCal.getTime()).toUpperCase());
        applyMonthStyle(textMonthNext1, next1MonthCal);

        Calendar next2MonthCal = (Calendar) currentDisplayCalendar.clone();
        next2MonthCal.add(Calendar.MONTH, 2);
        textMonthNext2.setText(monthFormat.format(next2MonthCal.getTime()).toUpperCase());
        applyMonthStyle(textMonthNext2, next2MonthCal);

        btnPreviousMonth.setEnabled(true);
        btnPreviousMonth.setAlpha(1.0f);

        // Explicitly check for next button state after all month styles are applied
        Calendar nextMonthToCheckForButton = (Calendar) currentDisplayCalendar.clone();
        nextMonthToCheckForButton.add(Calendar.MONTH, 1);

        if (isFutureMonth(nextMonthToCheckForButton)) {
            btnNextMonth.setEnabled(false);
            btnNextMonth.setAlpha(0.5f);
        } else {
            btnNextMonth.setEnabled(true);
            btnNextMonth.setAlpha(1.0f);
        }
    }

    /**
     * Populates the GridLayout with day numbers for the current month.
     * Also applies colored backgrounds based on `coloredDatesData`.
     */
    private void populateDayGrid() {
        gridFullMonth.removeAllViews(); // Clear previous days

        int daysInMonth = currentDisplayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = currentDisplayCalendar.get(Calendar.DAY_OF_WEEK);

        int startOffset = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7;

        int totalCells = startOffset + daysInMonth;
        int rowCount = (totalCells + 6) / 7;
        gridFullMonth.setRowCount(rowCount); // Ensure GridLayout has the correct number of rows dynamically

        int dayCounter = 1;
        for (int i = 0; i < totalCells; i++) {
            TextView dayTextView = new TextView(this);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // Let GridLayout distribute width based on weight
            params.height = GridLayout.LayoutParams.WRAP_CONTENT; // Let height wrap its content

            params.columnSpec = GridLayout.spec(i % 7, 1f); // Weight 1f
            params.rowSpec = GridLayout.spec(i / 7); // Assign row

            params.setMargins(dpToPx(this, 4), dpToPx(this, 4), dpToPx(this, 4), dpToPx(this, 4));

            dayTextView.setLayoutParams(params);
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            dayTextView.setTextColor(Color.BLACK);

            dayTextView.setMinimumHeight(dpToPx(this, 40));
            dayTextView.setPadding(0, dpToPx(this, 5), 0, dpToPx(this, 5));

            if (i < startOffset) {
                dayTextView.setText(""); // No text for empty cells
                dayTextView.setBackgroundResource(0); // No background for empty cells
            } else {
                dayTextView.setText(String.valueOf(dayCounter));

                if (coloredDatesData != null && coloredDatesData.containsKey(dayCounter)) {
                    int colorResId = coloredDatesData.get(dayCounter);
                    dayTextView.setBackgroundResource(R.drawable.bg_circle_generic);
                    dayTextView.getBackground().setTint(ContextCompat.getColor(this, colorResId));
                } else {
                    dayTextView.setBackgroundResource(0);
                }

                final int clickedDay = dayCounter;
                dayTextView.setOnClickListener(v -> {
                    Toast.makeText(FullMonthActivity.this, "Clicked Day: " + clickedDay + " " +
                            new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentDisplayCalendar.getTime()), Toast.LENGTH_SHORT).show();
                    // TODO: Implement navigation to a DailyJournalActivity or show day details
                });

                dayCounter++;
            }
            gridFullMonth.addView(dayTextView);
        }
    }


    /**
     * Applies styling (color and clickability) to a month TextView based on whether
     * the associated month is in the future relative to `todayCalendar`.
     */
    private void applyMonthStyle(TextView textView, Calendar monthCalendar) {
        if (isFutureMonth(monthCalendar)) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.grey));
            textView.setClickable(false);
            textView.setEnabled(false);
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.black));
            textView.setClickable(true);
            textView.setEnabled(true);
        }
    }

    /**
     * Checks if a given month is in the future relative to `todayCalendar`.
     */
    private boolean isFutureMonth(Calendar monthToCheck) {
        Calendar todayMonthStart = (Calendar) todayCalendar.clone();
        todayMonthStart.set(Calendar.DAY_OF_MONTH, 1);
        todayMonthStart.set(Calendar.HOUR_OF_DAY, 0);
        todayMonthStart.set(Calendar.MINUTE, 0);
        todayMonthStart.set(Calendar.SECOND, 0);
        todayMonthStart.set(Calendar.MILLISECOND, 0);

        Calendar checkMonthStart = (Calendar) monthToCheck.clone();
        checkMonthStart.set(Calendar.DAY_OF_MONTH, 1);
        checkMonthStart.set(Calendar.HOUR_OF_DAY, 0);
        checkMonthStart.set(Calendar.MINUTE, 0);
        checkMonthStart.set(Calendar.SECOND, 0);
        checkMonthStart.set(Calendar.MILLISECOND, 0);

        return checkMonthStart.after(todayMonthStart);
    }

    /**
     * Handles clicks on month TextViews, checking if they are enabled before proceeding.
     */
    private void checkAndHandleMonthClick(TextView clickedMonthTextView) {
        if (clickedMonthTextView.isEnabled()) {
            navigateMonth(getTargetCalendarForTextView(clickedMonthTextView));
        } else {
            SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            Calendar futureMonthCal = getTargetCalendarForTextView(clickedMonthTextView);
            String futureMonthName = monthYearFormat.format(futureMonthCal.getTime());
            Toast.makeText(this, "Belum tersedia untuk " + futureMonthName + ".", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Calculates the target Calendar object based on a clicked month TextView.
     */
    private Calendar getTargetCalendarForTextView(TextView textView) {
        String monthAbbreviation = textView.getText().toString();
        int targetMonthInt;
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(parseFormat.parse(monthAbbreviation));
            targetMonthInt = tempCal.get(Calendar.MONTH);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return (Calendar) currentDisplayCalendar.clone();
        }

        Calendar targetCalendar = (Calendar) currentDisplayCalendar.clone();
        targetCalendar.set(Calendar.MONTH, targetMonthInt);

        int currentDisplayedMonthInt = currentDisplayCalendar.get(Calendar.MONTH);

        if (targetMonthInt == Calendar.DECEMBER && currentDisplayedMonthInt == Calendar.JANUARY) {
            targetCalendar.add(Calendar.YEAR, -1);
        } else if (targetMonthInt == Calendar.JANUARY && currentDisplayedMonthInt == Calendar.DECEMBER) {
            targetCalendar.add(Calendar.YEAR, 1);
        }
        return targetCalendar;
    }

    /**
     * Navigates the calendar to the previous or next month.
     * @param direction -1 for previous month, 1 for next month.
     */
    private void navigateMonth(int direction) {
        Calendar tempTargetCalendar = (Calendar) currentDisplayCalendar.clone();
        tempTargetCalendar.add(Calendar.MONTH, direction);

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String targetMonthAndYear = monthYearFormat.format(tempTargetCalendar.getTime());

        if (isFutureMonth(tempTargetCalendar)) {
            Toast.makeText(this, "Belum ada entri untuk " + targetMonthAndYear + " karena masih di masa depan.", Toast.LENGTH_SHORT).show();
        } else {
            currentDisplayCalendar = tempTargetCalendar;
            loadColoredDatesForMonth(currentDisplayCalendar.get(Calendar.MONTH), currentDisplayCalendar.get(Calendar.YEAR));
            updateFullMonthUI();
        }
    }

    /**
     * Navigates to a specific month represented by a Calendar object.
     * This is used when a specific month TextView is clicked.
     * @param targetCalendar The Calendar object representing the month to navigate to.
     */
    private void navigateMonth(Calendar targetCalendar) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String targetMonthAndYear = monthYearFormat.format(targetCalendar.getTime());

        if (isFutureMonth(targetCalendar)) {
            Toast.makeText(this, "Belum tersedia untuk " + targetMonthAndYear + " karena masih di masa depan.", Toast.LENGTH_SHORT).show();
        } else {
            currentDisplayCalendar = targetCalendar;
            loadColoredDatesForMonth(currentDisplayCalendar.get(Calendar.MONTH), currentDisplayCalendar.get(Calendar.YEAR));
            updateFullMonthUI();
        }
    }

    /**
     * Loads colored dates for a specific month and year.
     * IMPORTANT: REPLACE THIS WITH YOUR ACTUAL DATA FETCHING LOGIC FROM YOUR DATABASE/SOURCE.
     *
     * @param month The Calendar month constant (e.g., Calendar.JANUARY).
     * @param year The year.
     */
    private void loadColoredDatesForMonth(int month, int year) {
        coloredDatesData = new HashMap<>(); // Re-initialize the map to clear previous data

        // --- YOUR ACTUAL DATA FETCHING LOGIC GOES HERE ---
        // You would query your database or other data source for journal entries
        // for the specified 'month' and 'year'.
        // Then, populate the 'coloredDatesData' map with day -> color_resource_id.

        // Example dummy data for demonstration (REPLACE THIS WITH REAL DATA!):
        if (year == 2025) {
            if (month == Calendar.JANUARY) {
                coloredDatesData.put(1, R.color.orange);
                coloredDatesData.put(2, R.color.pink);
                coloredDatesData.put(3, R.color.pink);
                coloredDatesData.put(4, R.color.purple);
                coloredDatesData.put(5, R.color.blue);
                coloredDatesData.put(6, R.color.blue);
                coloredDatesData.put(7, R.color.green);
                coloredDatesData.put(8, R.color.purple);
                coloredDatesData.put(9, R.color.blue);
                coloredDatesData.put(10, R.color.pink);
                coloredDatesData.put(11, R.color.green);
                coloredDatesData.put(12, R.color.green);
                coloredDatesData.put(13, R.color.blue);
                coloredDatesData.put(14, R.color.orange);
                coloredDatesData.put(15, R.color.green);
                coloredDatesData.put(16, R.color.purple);
                coloredDatesData.put(17, R.color.orange);
                coloredDatesData.put(18, R.color.pink);
                coloredDatesData.put(19, R.color.green);
                coloredDatesData.put(20, R.color.blue);
            }
            // All other months in 2025 and other years will not have colored dates
            // because there are no 'else if' blocks for them.
            // This fulfills the requirement of only January 2025 having colors.
        }
        // --- END OF DUMMY DATA ---
    }


    // Helper method to convert month abbreviation string to Calendar.MONTH integer
    private int getMonthNumber(String monthNameAbbr) {
        switch (monthNameAbbr) {
            case "JAN": return Calendar.JANUARY;
            case "FEB": return Calendar.FEBRUARY;
            case "MAR": return Calendar.MARCH;
            case "APR": return Calendar.APRIL;
            case "MAY": return Calendar.MAY;
            case "JUN": return Calendar.JUNE;
            case "JUL": return Calendar.JULY;
            case "AUG": return Calendar.AUGUST;
            case "SEP": return Calendar.SEPTEMBER;
            case "OCT": return Calendar.OCTOBER;
            case "NOV": return Calendar.NOVEMBER;
            case "DEC": return Calendar.DECEMBER;
            default: return -1; // Should not happen
        }
    }

    // Helper method to convert dp to pixels
    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
}