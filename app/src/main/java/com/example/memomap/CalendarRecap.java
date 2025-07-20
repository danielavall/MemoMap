package com.example.memomap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

        // --- Perubahan Baru: Terapkan Rounded Background dan Warna Biru pada Tombol Finish ---
        GradientDrawable finishButtonShape = new GradientDrawable();
        finishButtonShape.setShape(GradientDrawable.RECTANGLE);
        finishButtonShape.setCornerRadius(50); // Sesuaikan radius sesuai keinginan
        finishButtonShape.setColor(ContextCompat.getColor(this, R.color.blue)); // Menggunakan warna biru standar Android
        finishButton.setBackground(finishButtonShape);
        finishButton.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Teks putih agar kontras
        finishButton.setTypeface(null, Typeface.BOLD); // Opsional: teks tebal
        // --- Akhir Perubahan Baru ---

        // BACA DATA DARI INTENT DAN INISIALISASI VARIABEL SELECTION
        Intent intent = getIntent();
        selectedCalendarType = intent.getStringExtra("type");

        selectedYear = intent.getIntExtra("currentYear", Calendar.getInstance().get(Calendar.YEAR));
        selectedMonth = intent.getIntExtra("currentMonth", Calendar.getInstance().get(Calendar.MONTH));
        weekStart = intent.getIntExtra("currentWeekStart", 0);
        weekEnd = intent.getIntExtra("currentWeekEnd", 0);

        yearStart = (selectedYear / 15) * 15;
        yearEnd = yearStart + 14;

        setupVisibility(selectedCalendarType);

        updateYearGrid();
        populateMonthGrid();
        populateWeekGrid();

        updateMonthGridDisplay();
        updateWeekSelectionDisplay();


        // Year navigation
        btnYearLeft.setOnClickListener(v -> {
            yearStart -= 15;
            yearEnd -= 15;
            updateYearGrid();
        });

        btnYearRight.setOnClickListener(v -> {
            yearStart += 15;
            yearEnd += 15;
            updateYearGrid();
        });

        // Month navigation
        btnMonthLeft.setOnClickListener(v -> {
            selectedYear = (selectedMonth == 0) ? selectedYear - 1 : selectedYear;
            selectedMonth = (selectedMonth + 11) % 12;

            weekStart = 0; // Reset pilihan minggu saat bulan berubah
            weekEnd = 0;   // Pastikan weekEnd direset juga

            populateMonthGrid();
            updateMonthGridDisplay();
            populateWeekGrid(); // Render ulang grid minggu karena bulan berubah
            updateWeekSelectionDisplay();
        });

        btnMonthRight.setOnClickListener(v -> {
            selectedYear = (selectedMonth == 11) ? selectedYear + 1 : selectedYear;
            selectedMonth = (selectedMonth + 1) % 12;

            weekStart = 0; // Reset pilihan minggu saat bulan berubah
            weekEnd = 0;   // Pastikan weekEnd direset juga

            populateMonthGrid();
            updateMonthGridDisplay();
            populateWeekGrid(); // Render ulang grid minggu karena bulan berubah
            updateWeekSelectionDisplay();
        });

        // Week navigation
        btnWeekLeft.setOnClickListener(v -> {
            selectedYear = (selectedMonth == 0) ? selectedYear - 1 : selectedYear;
            selectedMonth = (selectedMonth + 11) % 12;

            weekStart = 0;
            weekEnd = 0;

            populateWeekGrid();
            updateWeekSelectionDisplay();
        });

        btnWeekRight.setOnClickListener(v -> {
            selectedYear = (selectedMonth == 11) ? selectedYear + 1 : selectedYear;
            selectedMonth = (selectedMonth + 1) % 12;

            weekStart = 0;
            weekEnd = 0;

            populateWeekGrid();
            updateWeekSelectionDisplay();
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
                showWarningToast("Please select a valid " + selectedCalendarType.toLowerCase() + "!");
            }
        });
    }

    private void setupVisibility(String type) {
        // Sembunyikan semua section terlebih dahulu
        findViewById(R.id.tvChooseYearTitle).setVisibility(View.GONE);
        findViewById(R.id.layoutYearRange).setVisibility(View.GONE);
        yearGrid.setVisibility(View.GONE);

        findViewById(R.id.tvChooseMonthTitle).setVisibility(View.GONE);
        findViewById(R.id.layoutMonthYearSelector).setVisibility(View.GONE);
        monthGrid.setVisibility(View.GONE);

        findViewById(R.id.tvChooseWeekTitle).setVisibility(View.GONE);
        findViewById(R.id.layoutWeekMonthSelector).setVisibility(View.GONE);
        weekGrid.setVisibility(View.GONE);

        // Tampilkan SEMUA section TERKAIT PILIHAN TANGGAL
        findViewById(R.id.tvChooseYearTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutYearRange).setVisibility(View.VISIBLE);
        yearGrid.setVisibility(View.VISIBLE);

        findViewById(R.id.tvChooseMonthTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutMonthYearSelector).setVisibility(View.VISIBLE);
        monthGrid.setVisibility(View.VISIBLE);

        findViewById(R.id.tvChooseWeekTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutWeekMonthSelector).setVisibility(View.VISIBLE);
        weekGrid.setVisibility(View.VISIBLE);

        // Finish button selalu terlihat
        finishButton.setVisibility(View.VISIBLE);
    }


    private void updateYearGrid() {
        yearRange.setText(yearStart + " - " + yearEnd);
        yearGrid.removeAllViews();
        for (int y = yearStart; y <= yearEnd; y++) {
            // isRounded di sini tidak terlalu relevan karena kita tidak menggunakan background warna
            Button btn = createItem(String.valueOf(y), y == selectedYear, true);
            final int yearClicked = y;
            btn.setOnClickListener(v -> {
                selectedYear = yearClicked;
                weekStart = 0;
                weekEnd = 0;

                updateYearSelection();
                populateMonthGrid();
                updateMonthGridDisplay();
                populateWeekGrid();
                updateWeekSelectionDisplay();
            });
            yearGrid.addView(btn);
        }
        updateYearSelection();
    }

    private void populateMonthGrid() {
        monthGrid.removeAllViews();
        for (int i = 0; i < 12; i++) {
            final int index = i;
            // isRounded di sini tidak terlalu relevan karena kita tidak menggunakan background warna
            Button btn = createItem(getMonthName(i), i == selectedMonth, true);
            btn.setOnClickListener(v -> {
                selectedMonth = index;
                weekStart = 0;
                weekEnd = 0;
                updateMonthSelection();
                populateWeekGrid(); // Render ulang grid minggu karena bulan berubah
                updateWeekSelectionDisplay();
            });
            monthGrid.addView(btn);
        }
        updateMonthSelection();
    }

    private void updateMonthGridDisplay() {
        tvMonthYear.setText(String.valueOf(selectedYear));
        updateMonthSelection();
    }

    private void populateWeekGrid() {
        weekGrid.removeAllViews();
        for (int i = 1; i <= 5; i++) { // Asumsi ada 5 minggu per bulan
            final int week = i;
            boolean selected = (week >= weekStart && week <= weekEnd && weekEnd != 0) || (week == weekStart && weekEnd == 0 && weekStart != 0);
            // isRounded di sini tidak terlalu relevan karena kita tidak menggunakan background warna
            Button btn = createItem("Week " + week, selected, true);

            btn.setOnClickListener(v -> {
                if (weekStart == 0) { // Belum ada yang dipilih sama sekali
                    weekStart = week;
                    weekEnd = 0; // Default ke 0, menunggu pilihan kedua
                } else if (weekEnd == 0) { // weekStart sudah dipilih, ini adalah pilihan kedua
                    weekEnd = week;
                    if (weekEnd < weekStart) { // Jika pilihan kedua lebih kecil dari pertama, tukar
                        int temp = weekStart;
                        weekStart = weekEnd;
                        weekEnd = temp;
                    }
                } else { // Rentang sudah dipilih, mulai ulang pemilihan
                    weekStart = week;
                    weekEnd = 0;
                }
                updateWeekSelectionDisplay();
            });
            weekGrid.addView(btn);
        }
        updateWeekSelectionDisplay();
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
        tvWeekMonth.setText(getMonthName(selectedMonth));
    }


    // Metode helper untuk mengatur tampilan tombol (DIUBAH UNTUK TIDAK MENGGUNAKAN BACKGROUND UNTUK PILIHAN DATE)
    private void setupButtonAppearance(Button btn, boolean selected, boolean isRounded) {
        if (selected) {
            // Hanya teks bold, tanpa background warna
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent)); // Background transparan
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Teks HITAM
            btn.setTypeface(null, Typeface.BOLD);
        } else {
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Teks hitam untuk tidak dipilih
            btn.setTypeface(null, Typeface.NORMAL);
        }
    }

    // Metode createItem yang digunakan untuk semua grid (Tahun, Bulan, Minggu)
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


    private String getMonthName(int index) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (index >= 0 && index < months.length) {
            return months[index];
        }
        return "";
    }

    private boolean validateSelection() {
        switch (selectedCalendarType) {
            case "year":
                return selectedYear != 0;
            case "month":
                return selectedYear != 0 && selectedMonth != -1;
            case "week":
                return selectedYear != 0 && selectedMonth != -1 && weekStart != 0;
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
        resultIntent.putExtra("dateSelected", false); // Menandakan bahwa pengguna membatalkan atau kembali
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    private void showWarningToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
        toast.show();
    }
}