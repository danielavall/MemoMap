package com.example.memomap;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarRecap extends AppCompatActivity {

    private TextView yearRange;
    private GridLayout yearGrid, monthGrid, weekGrid;
    private Button finishButton;

    private int selectedYearInt = 2027;
    private int selectedMonthInt = 2;
    private int selectedWeekInt = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_recap);

        yearRange = findViewById(R.id.yearRange);
        yearGrid = findViewById(R.id.yearGrid);
        monthGrid = findViewById(R.id.monthGrid);
        weekGrid = findViewById(R.id.weekGrid);
        finishButton = findViewById(R.id.finishButton);

        populateYearGrid();
        populateMonthGrid();
        populateWeekGrid();

        finishButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("year", selectedYearInt);
            resultIntent.putExtra("month", selectedMonthInt);
            resultIntent.putExtra("week", selectedWeekInt);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void populateYearGrid() {
        int start = 2025;
        int end = 2039;
        yearRange.setText(start + " - " + end);

        for (int y = start; y <= end; y++) {
            Button btn = createItem(String.valueOf(y), y == selectedYearInt);
            btn.setOnClickListener(v -> {
                selectedYearInt = Integer.parseInt(btn.getText().toString());
                updateYearSelection();
            });
            yearGrid.addView(btn);
        }
    }

    private void populateMonthGrid() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 0; i < months.length; i++) {
            final int index = i;
            Button btn = createItem(months[i], i == selectedMonthInt);
            btn.setOnClickListener(v -> {
                selectedMonthInt = index;
                updateMonthSelection();
            });
            monthGrid.addView(btn);
        }
    }

    private void populateWeekGrid() {
        for (int i = 1; i <= 5; i++) {
            final int week = i;
            Button btn = createItem("Week " + i, i == selectedWeekInt);
            btn.setOnClickListener(v -> {
                selectedWeekInt = week;
                updateWeekSelection();
            });
            weekGrid.addView(btn);
        }
    }

    private void updateYearSelection() {
        for (int i = 0; i < yearGrid.getChildCount(); i++) {
            Button btn = (Button) yearGrid.getChildAt(i);
            boolean selected = Integer.parseInt(btn.getText().toString()) == selectedYearInt;
            btn.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    private void updateMonthSelection() {
        for (int i = 0; i < monthGrid.getChildCount(); i++) {
            Button btn = (Button) monthGrid.getChildAt(i);
            btn.setTypeface(null, i == selectedMonthInt ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    private void updateWeekSelection() {
        for (int i = 0; i < weekGrid.getChildCount(); i++) {
            Button btn = (Button) weekGrid.getChildAt(i);
            btn.setTypeface(null, (i + 1) == selectedWeekInt ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    private Button createItem(String text, boolean selected) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(16f);
        btn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btn.setTextColor(getResources().getColor(android.R.color.black));
        btn.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(12, 12, 12, 12);
        btn.setLayoutParams(params);

        return btn;
    }
}
