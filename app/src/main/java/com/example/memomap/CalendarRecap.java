package com.example.memomap;

import android.app.Activity;
import android.content.Intent;
// import android.graphics.Color; // Tidak digunakan, bisa dihapus jika tidak ada keperluan lain
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class CalendarRecap extends AppCompatActivity {

    private TextView yearRange, tvMonthYear, tvWeekMonth;
    private GridLayout yearGrid, monthGrid, weekGrid;
    private Button finishButton;
    private ImageButton btnYearLeft, btnYearRight, btnMonthLeft, btnMonthRight, btnWeekLeft, btnWeekRight;

    private int selectedYear;
    private int selectedMonth;
    private int weekStart;
    private int weekEnd;

    private int yearStart;
    private int yearEnd;

    private String selectedCalendarType = "";

    private final int MAX_YEAR = 2025;
    private final int MAX_MONTH_FOR_MAX_YEAR = Calendar.AUGUST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_recap);

        // Year
        yearRange = findViewById(R.id.yearRange);
        btnYearLeft = findViewById(R.id.btnYearLeft);
        btnYearRight = findViewById(R.id.btnYearRight);
        yearGrid = findViewById(R.id.yearGrid);

        // Month
        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnMonthLeft = findViewById(R.id.btnMonthLeft);
        btnMonthRight = findViewById(R.id.btnMonthRight);
        monthGrid = findViewById(R.id.monthGrid);

        // Week
        tvWeekMonth = findViewById(R.id.tvWeekMonth);
        btnWeekLeft = findViewById(R.id.btnWeekLeft);
        btnWeekRight = findViewById(R.id.btnWeekRight);
        weekGrid = findViewById(R.id.weekGrid);

        finishButton = findViewById(R.id.finishButton);

        GradientDrawable finishButtonShape = new GradientDrawable();
        finishButtonShape.setShape(GradientDrawable.RECTANGLE);
        finishButtonShape.setCornerRadius(100f);
        finishButtonShape.setColor(ContextCompat.getColor(this, R.color.blue));
        finishButton.setBackground(finishButtonShape);
        finishButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        Intent intent = getIntent();
        selectedCalendarType = intent.getStringExtra("type");
        if (selectedCalendarType == null) {
            selectedCalendarType = "";
        }

        Calendar currentCal = Calendar.getInstance();
        int defaultYear = currentCal.get(Calendar.YEAR);
        int defaultMonth = currentCal.get(Calendar.MONTH);

        selectedYear = intent.getIntExtra("currentYear", defaultYear);
        selectedMonth = intent.getIntExtra("currentMonth", defaultMonth);
        weekStart = intent.getIntExtra("currentWeekStart", 0);
        weekEnd = intent.getIntExtra("currentWeekEnd", 0);

        if (selectedYear > MAX_YEAR) {
            selectedYear = MAX_YEAR;
            selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
            weekStart = 0;
            weekEnd = 0;
        } else if (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR) {
            selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
            weekStart = 0;
            weekEnd = 0;
        }

        if (selectedYear >= (MAX_YEAR - 14) && selectedYear <= MAX_YEAR) {
            yearStart = MAX_YEAR - 14;
            yearEnd = MAX_YEAR;
        } else {
            yearStart = (selectedYear / 15) * 15;
            yearEnd = yearStart + 14;
        }

        setupVisibility(selectedCalendarType);

        updateYearGrid();
        populateMonthGrid();
        populateWeekGrid();

        updateMonthGridDisplay();
        updateWeekSelectionDisplay();
        updateNavigationButtonStates();

        // Year navigation
        btnYearLeft.setOnClickListener(v -> {
            yearStart -= 15;
            yearEnd = yearStart + 14;

            if (yearEnd > MAX_YEAR && yearStart < (MAX_YEAR - 14)) {
                yearEnd = MAX_YEAR;
            }

            updateYearGrid();
            updateNavigationButtonStates();
        });

        btnYearRight.setOnClickListener(v -> {
            if (yearEnd == MAX_YEAR && yearStart == MAX_YEAR - 14) {
                return;
            }

            int nextYearStart = yearStart + 15;

            if (nextYearStart >= (MAX_YEAR - 14)) {
                yearStart = MAX_YEAR - 14;
                yearEnd = MAX_YEAR;
            } else {
                yearStart = nextYearStart;
                yearEnd = yearStart + 14;
            }

            updateYearGrid();
            updateNavigationButtonStates();
        });

        // Month navigation
        btnMonthLeft.setOnClickListener(v -> {
            selectedYear = (selectedMonth == 0) ? selectedYear - 1 : selectedYear;
            selectedMonth = (selectedMonth + 11) % 12;

            weekStart = 0;
            weekEnd = 0;

            populateMonthGrid();
            updateMonthGridDisplay();
            populateWeekGrid();
            updateWeekSelectionDisplay();
            updateNavigationButtonStates();
        });

        btnMonthRight.setOnClickListener(v -> {
            if (selectedYear == MAX_YEAR && selectedMonth >= MAX_MONTH_FOR_MAX_YEAR) {
                showWarningToast("Tidak ada bulan selanjutnya untuk tahun 2025.");
                return;
            }

            selectedYear = (selectedMonth == 11) ? selectedYear + 1 : selectedYear;
            selectedMonth = (selectedMonth + 1) % 12;

            if (selectedYear > MAX_YEAR || (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR)) {
                selectedYear = MAX_YEAR;
                selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
                showWarningToast("Tidak ada bulan selanjutnya untuk tahun 2025.");
            }

            weekStart = 0;
            weekEnd = 0;

            populateMonthGrid();
            updateMonthGridDisplay();
            populateWeekGrid();
            updateWeekSelectionDisplay();
            updateNavigationButtonStates();
        });

        // Week navigation
        btnWeekLeft.setOnClickListener(v -> {
            selectedYear = (selectedMonth == 0) ? selectedYear - 1 : selectedYear;
            selectedMonth = (selectedMonth + 11) % 12;

            weekStart = 0;
            weekEnd = 0;

            populateWeekGrid();
            updateWeekSelectionDisplay();
            updateNavigationButtonStates();
        });

        btnWeekRight.setOnClickListener(v -> {
            if (selectedYear == MAX_YEAR && selectedMonth >= MAX_MONTH_FOR_MAX_YEAR) {
                showWarningToast("Tidak ada minggu selanjutnya untuk tahun 2025.");
                return;
            }

            selectedYear = (selectedMonth == 11) ? selectedYear + 1 : selectedYear;
            selectedMonth = (selectedMonth + 1) % 12;

            if (selectedYear > MAX_YEAR || (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR)) {
                selectedYear = MAX_YEAR;
                selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
                showWarningToast("Tidak ada minggu selanjutnya untuk tahun 2025.");
            }

            weekStart = 0;
            weekEnd = 0;

            populateWeekGrid();
            updateWeekSelectionDisplay();
            updateNavigationButtonStates();
        });


        // Finish button listener
        finishButton.setOnClickListener(v -> {
            if (validateSelection()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("type", selectedCalendarType);
                resultIntent.putExtra("year", selectedYear);
                resultIntent.putExtra("month", selectedMonth);
                resultIntent.putExtra("weekStart", weekStart);
                resultIntent.putExtra("weekEnd", weekEnd);
                resultIntent.putExtra("dateSelected", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                String typeForToast = selectedCalendarType.isEmpty() ? "date" : selectedCalendarType.toLowerCase();
                showWarningToast("Please select a valid " + typeForToast + "!");
            }
        });
    }

    private void setupVisibility(String type) {
        findViewById(R.id.tvChooseYearTitle).setVisibility(View.GONE);
        findViewById(R.id.layoutYearRange).setVisibility(View.GONE);
        yearGrid.setVisibility(View.GONE);

        findViewById(R.id.tvChooseMonthTitle).setVisibility(View.GONE);
        findViewById(R.id.layoutMonthYearSelector).setVisibility(View.GONE);
        monthGrid.setVisibility(View.GONE);

        findViewById(R.id.tvChooseWeekTitle).setVisibility(View.GONE);
        findViewById(R.id.layoutWeekMonthSelector).setVisibility(View.GONE);
        weekGrid.setVisibility(View.GONE);

        findViewById(R.id.tvChooseYearTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutYearRange).setVisibility(View.VISIBLE);
        yearGrid.setVisibility(View.VISIBLE);

        findViewById(R.id.tvChooseMonthTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutMonthYearSelector).setVisibility(View.VISIBLE);
        monthGrid.setVisibility(View.VISIBLE);

        findViewById(R.id.tvChooseWeekTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutWeekMonthSelector).setVisibility(View.VISIBLE);
        weekGrid.setVisibility(View.VISIBLE);

        finishButton.setVisibility(View.VISIBLE);
    }

    private void updateNavigationButtonStates() {
        // Tahun
        if (yearEnd == MAX_YEAR && yearStart == MAX_YEAR - 14) {
            btnYearRight.setEnabled(false);
        } else {
            btnYearRight.setEnabled(true);
        }
        btnYearRight.setColorFilter(ContextCompat.getColor(this, btnYearRight.isEnabled() ? android.R.color.black : R.color.grey));

        btnYearLeft.setColorFilter(ContextCompat.getColor(this, android.R.color.black));

        boolean canGoNextMonth = !(selectedYear == MAX_YEAR && selectedMonth >= MAX_MONTH_FOR_MAX_YEAR);
        btnMonthRight.setEnabled(canGoNextMonth);
        btnMonthRight.setColorFilter(ContextCompat.getColor(this, btnMonthRight.isEnabled() ? android.R.color.black : R.color.grey));
        btnMonthLeft.setColorFilter(ContextCompat.getColor(this, android.R.color.black));

        btnWeekRight.setEnabled(canGoNextMonth);
        btnWeekRight.setColorFilter(ContextCompat.getColor(this, btnWeekRight.isEnabled() ? android.R.color.black : R.color.grey));
        btnWeekLeft.setColorFilter(ContextCompat.getColor(this, android.R.color.black));
    }

    private void updateYearGrid() {
        yearRange.setText(yearStart + " - " + yearEnd);
        yearGrid.removeAllViews();
        for (int y = yearStart; y <= yearEnd; y++) {
            Button btn = createItem(String.valueOf(y), y == selectedYear, true);
            final int yearClicked = y;

            // --- Pembatasan klik tahun ---
            boolean isClickable = true;
            if (yearClicked > MAX_YEAR) {
                isClickable = false;
            }

            btn.setEnabled(isClickable);
            if (!isClickable) {
                btn.setTextColor(ContextCompat.getColor(this, R.color.grey));
            } else {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }


            btn.setOnClickListener(v -> {
                if (!btn.isEnabled()) return;

                selectedYear = yearClicked;
                if (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR) {
                    selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
                }
                weekStart = 0;
                weekEnd = 0;

                updateYearSelection();
                populateMonthGrid();
                updateMonthGridDisplay();
                populateWeekGrid();
                updateWeekSelectionDisplay();
                updateNavigationButtonStates();
            });
            yearGrid.addView(btn);
        }
        updateYearSelection();
        updateNavigationButtonStates();
    }

    private void populateMonthGrid() {
        monthGrid.removeAllViews();
        tvMonthYear.setText(String.valueOf(selectedYear));

        for (int i = 0; i < 12; i++) {
            final int index = i;
            boolean isClickable = true;
            if (selectedYear == MAX_YEAR && index > MAX_MONTH_FOR_MAX_YEAR) {
                isClickable = false;
            }

            Button btn = createItem(getMonthName(i), i == selectedMonth, true);
            btn.setEnabled(isClickable);
            if (!isClickable) {
                btn.setTextColor(ContextCompat.getColor(this, R.color.grey));
            } else {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }

            btn.setOnClickListener(v -> {
                if (!btn.isEnabled()) return;

                selectedMonth = index;
                weekStart = 0;
                weekEnd = 0;
                updateMonthSelection();
                populateWeekGrid();
                updateWeekSelectionDisplay();
                updateNavigationButtonStates();
            });
            monthGrid.addView(btn);
        }
        updateMonthSelection();
        updateNavigationButtonStates();
    }

    private void updateMonthGridDisplay() {
        tvMonthYear.setText(String.valueOf(selectedYear));
        updateMonthSelection();
    }

    private void populateWeekGrid() {
        weekGrid.removeAllViews();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, selectedYear);
        cal.set(Calendar.MONTH, selectedMonth);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int finalNumWeeksForDisplay = 4;

        if (selectedMonth == Calendar.MARCH ||
                selectedMonth == Calendar.JUNE ||
                selectedMonth == Calendar.AUGUST ||
                selectedMonth == Calendar.NOVEMBER) {

            int calculatedNumWeeks = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
            finalNumWeeksForDisplay = Math.min(calculatedNumWeeks, 3);
        }

        int maxButtonsToDisplay = 5;

        for (int i = 1; i <= maxButtonsToDisplay; i++) {
            final int week = i;
            boolean selected = (week >= weekStart && week <= weekEnd && weekEnd != 0) || (week == weekStart && weekEnd == 0 && weekStart != 0);

            boolean isClickable = true;
            if (week > finalNumWeeksForDisplay) {
                isClickable = false;
            }

            Button btn = createItem("Week " + week, selected, true);
            btn.setEnabled(isClickable);
            if (!isClickable) {
                btn.setTextColor(ContextCompat.getColor(this, R.color.grey));
            } else {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }

            btn.setOnClickListener(v -> {
                if (!btn.isEnabled()) return;

                if (weekStart == 0) {
                    weekStart = week;
                    weekEnd = 0;
                } else if (weekEnd == 0) {
                    weekEnd = week;
                    if (weekEnd < weekStart) {
                        int temp = weekStart;
                        weekStart = weekEnd;
                        weekEnd = temp;
                    }
                } else {
                    weekStart = week;
                    weekEnd = 0;
                }
                updateWeekSelectionDisplay();
            });
            weekGrid.addView(btn);
        }
        updateWeekSelectionDisplay();
    }

    private void setupButtonAppearance(Button btn, boolean selected, boolean isRounded) {
        if (selected) {
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            btn.setTypeface(null, Typeface.BOLD);
        } else {
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            if (btn.isEnabled()) {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }
            btn.setTypeface(null, Typeface.NORMAL);
        }
    }

    private Button createItem(String text, boolean selected, boolean isRounded) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(16f);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(4, 4, 4, 4);
        btn.setLayoutParams(params);

        setupButtonAppearance(btn, selected, isRounded);

        return btn;
    }

    private void updateYearSelection() {
        for (int i = 0; i < yearGrid.getChildCount(); i++) {
            Button btn = (Button) yearGrid.getChildAt(i);
            boolean selected = Integer.parseInt(btn.getText().toString()) == selectedYear;
            setupButtonAppearance(btn, selected, true);
        }
    }

    private void updateMonthSelection() {
        for (int i = 0; i < monthGrid.getChildCount(); i++) {
            Button btn = (Button) monthGrid.getChildAt(i);
            boolean selected = (i == selectedMonth);
            setupButtonAppearance(btn, selected, true);
        }
    }

    private void updateWeekSelectionDisplay() {
        for (int i = 0; i < weekGrid.getChildCount(); i++) {
            Button btn = (Button) weekGrid.getChildAt(i);
            int weekNum = i + 1;

            boolean selected = false;
            if (weekStart != 0) {
                if (weekEnd != 0) {
                    selected = (weekNum >= weekStart && weekNum <= weekEnd);
                } else {
                    selected = (weekNum == weekStart);
                }
            }
            setupButtonAppearance(btn, selected, true);
        }
        tvWeekMonth.setText(getMonthName(selectedMonth) + " " + selectedYear);
    }


    private String getMonthName(int index) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (index >= 0 && index < months.length) {
            return months[index];
        }
        return "";
    }

    private boolean validateSelection() {
        if (selectedYear == 0) return false;

        if (selectedCalendarType == null || selectedCalendarType.isEmpty()) {
            return selectedYear != 0;
        }

        switch (selectedCalendarType) {
            case "year":
                return selectedYear != 0 && selectedYear <= MAX_YEAR;
            case "month":
                return selectedYear != 0 && selectedMonth != -1 &&
                        (selectedYear < MAX_YEAR || (selectedYear == MAX_YEAR && selectedMonth <= MAX_MONTH_FOR_MAX_YEAR));
            case "week":
                return selectedYear != 0 && selectedMonth != -1 && weekStart != 0 &&
                        (selectedYear < MAX_YEAR || (selectedYear == MAX_YEAR && selectedMonth <= MAX_MONTH_FOR_MAX_YEAR));
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("type", selectedCalendarType);
        resultIntent.putExtra("year", selectedYear);
        resultIntent.putExtra("month", selectedMonth);
        resultIntent.putExtra("weekStart", weekStart);
        resultIntent.putExtra("weekEnd", weekEnd);
        resultIntent.putExtra("dateSelected", false);
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    private void showWarningToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
        toast.show();
    }
}