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
import java.util.Locale; // Import Locale untuk mengontrol FirstDayOfWeek jika diperlukan

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

    // --- Konstanta untuk pembatasan ---
    private final int MAX_YEAR = 2025;
    private final int MAX_MONTH_FOR_MAX_YEAR = Calendar.JULY; // Juli adalah indeks 6
    // --- Akhir Konstanta ---

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
        finishButtonShape.setCornerRadius(100f); // Nilai tinggi tombol 40dp, radius 100f akan membuat dia sangat rounded/pill-shaped
        finishButtonShape.setColor(ContextCompat.getColor(this, R.color.blue)); // Menggunakan warna dari R.color.blue
        finishButton.setBackground(finishButtonShape); // Terapkan drawable ke background tombol
        finishButton.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Teks hitam agar kontras (sesuai XML Anda)
        // finishButton.setTypeface(null, Typeface.BOLD); // Tidak diminta, bisa dihapus jika tidak diperlukan
        // --- Akhir Perubahan Baru ---

        // BACA DATA DARI INTENT DAN INISIALISASI VARIABEL SELECTION
        Intent intent = getIntent();
        // PERBAIKAN: Pastikan selectedCalendarType tidak null
        selectedCalendarType = intent.getStringExtra("type");
        if (selectedCalendarType == null) {
            selectedCalendarType = ""; // Beri nilai default kosong jika null
        }


        // Ambil tahun/bulan/minggu saat ini dari sistem sebagai default jika tidak ada di intent
        Calendar currentCal = Calendar.getInstance();
        int defaultYear = currentCal.get(Calendar.YEAR);
        int defaultMonth = currentCal.get(Calendar.MONTH);

        selectedYear = intent.getIntExtra("currentYear", defaultYear);
        selectedMonth = intent.getIntExtra("currentMonth", defaultMonth);
        weekStart = intent.getIntExtra("currentWeekStart", 0);
        weekEnd = intent.getIntExtra("currentWeekEnd", 0);

        // --- Penyesuaian Inisialisasi: Pastikan tahun dan bulan awal tidak melebihi batas ---
        if (selectedYear > MAX_YEAR) {
            selectedYear = MAX_YEAR;
            selectedMonth = MAX_MONTH_FOR_MAX_YEAR; // Jika tahun melewati batas, set bulan ke bulan maksimal
            weekStart = 0; // Reset week selection
            weekEnd = 0;
        } else if (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR) {
            selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
            weekStart = 0; // Reset week selection
            weekEnd = 0;
        }
        // --- Akhir Penyesuaian Inisialisasi ---

        // --- Perbaikan logika untuk memastikan 2025 tergabung dengan tahun sebelumnya ---
        // Jika tahun yang dipilih adalah 2025 atau berada dalam blok 15 tahun terakhir yang berisi 2025 (misal 2011-2025)
        if (selectedYear >= (MAX_YEAR - 14) && selectedYear <= MAX_YEAR) {
            yearStart = MAX_YEAR - 14; // Paksa dimulai dari 15 tahun sebelum MAX_YEAR
            yearEnd = MAX_YEAR;        // Paksa berakhir di MAX_YEAR
        } else {
            // Untuk tahun-tahun lainnya, hitung seperti biasa (blok 15 tahun standar)
            yearStart = (selectedYear / 15) * 15;
            yearEnd = yearStart + 14;
        }
        // --- Akhir Perbaikan logika ---


        setupVisibility(selectedCalendarType);

        updateYearGrid();
        populateMonthGrid();
        populateWeekGrid();

        updateMonthGridDisplay();
        updateWeekSelectionDisplay(); // Memastikan pembaruan tampilan awal untuk minggu

        // --- PENTING: Update button visibility setelah inisialisasi ---
        updateNavigationButtonStates();


        // Year navigation
        btnYearLeft.setOnClickListener(v -> {
            yearStart -= 15;
            yearEnd = yearStart + 14; // Hitung yearEnd seperti biasa saat mundur

            // Jika yearStart mundur terlalu jauh ke belakang sebelum 15 tahun yang lalu dari MAX_YEAR
            // (misal tahun 2010 atau lebih rendah, dan MAX_YEAR adalah 2025, maka MAX_YEAR - 14 = 2011)
            // dan yearEnd saat ini masih lebih besar dari MAX_YEAR, pastikan yearEnd tidak melebihi MAX_YEAR
            // Logika ini mungkin tidak terlalu krusial di sini karena fokus ke pembatasan maju.
            // Namun, ini mencegah tahunEnd secara aneh menjadi lebih besar dari MAX_YEAR setelah mundur.
            if (yearEnd > MAX_YEAR && yearStart < (MAX_YEAR - 14)) {
                yearEnd = MAX_YEAR;
            }

            updateYearGrid();
            updateNavigationButtonStates(); // Perbarui status tombol navigasi
        });

        btnYearRight.setOnClickListener(v -> {
            // Jika rentang saat ini sudah menampilkan blok terakhir (misalnya 2011-2025), jangan lakukan apa-apa
            if (yearEnd == MAX_YEAR && yearStart == MAX_YEAR - 14) {
                return;
            }

            int nextYearStart = yearStart + 15;

            // Jika blok tahun berikutnya akan dimulai dalam 15 tahun terakhir yang berisi MAX_YEAR
            // (misalnya nextYearStart 2011 atau lebih), paksa ke blok terakhir 2011-2025.
            if (nextYearStart >= (MAX_YEAR - 14)) {
                yearStart = MAX_YEAR - 14; // Paksa dimulai dari 2011
                yearEnd = MAX_YEAR;        // Paksa berakhir di 2025
            } else {
                // Jika belum mencapai blok terakhir, lanjutkan dengan blok 15 tahun standar
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

            // --- Pembatasan: Jangan biarkan tahun mundur melewati tahun yang bisa ditampilkan (misal < 1990)
            // (Tidak ada batasan minimum tahun yang eksplisit di kode Anda, jadi ini opsional)

            weekStart = 0; // Reset pilihan minggu saat bulan berubah
            weekEnd = 0;   // Pastikan weekEnd direset juga

            populateMonthGrid();
            updateMonthGridDisplay();
            populateWeekGrid(); // Render ulang grid minggu karena bulan berubah
            updateWeekSelectionDisplay();
            updateNavigationButtonStates(); // Perbarui status tombol navigasi
        });

        btnMonthRight.setOnClickListener(v -> {
            // --- Pembatasan navigasi bulan ---
            if (selectedYear == MAX_YEAR && selectedMonth >= MAX_MONTH_FOR_MAX_YEAR) {
                // Sudah di tahun dan bulan maksimal, jangan maju
                showWarningToast("Tidak ada bulan selanjutnya untuk tahun 2025.");
                return;
            }

            selectedYear = (selectedMonth == 11) ? selectedYear + 1 : selectedYear;
            selectedMonth = (selectedMonth + 1) % 12;

            // Jika setelah maju, tahun melebihi MAX_YEAR atau (di MAX_YEAR dan bulan melebihi MAX_MONTH_FOR_MAX_YEAR)
            if (selectedYear > MAX_YEAR || (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR)) {
                selectedYear = MAX_YEAR;
                selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
                showWarningToast("Tidak ada bulan selanjutnya untuk tahun 2025.");
            }

            weekStart = 0; // Reset pilihan minggu saat bulan berubah
            weekEnd = 0;   // Pastikan weekEnd direset juga

            populateMonthGrid();
            updateMonthGridDisplay();
            populateWeekGrid(); // Render ulang grid minggu karena bulan berubah
            updateWeekSelectionDisplay();
            updateNavigationButtonStates(); // Perbarui status tombol navigasi
        });

        // Week navigation
        btnWeekLeft.setOnClickListener(v -> {
            selectedYear = (selectedMonth == 0) ? selectedYear - 1 : selectedYear;
            selectedMonth = (selectedMonth + 11) % 12;

            weekStart = 0;
            weekEnd = 0;

            populateWeekGrid();
            updateWeekSelectionDisplay();
            updateNavigationButtonStates(); // Perbarui status tombol navigasi
        });

        btnWeekRight.setOnClickListener(v -> {
            // --- Pembatasan navigasi minggu (sama seperti bulan) ---
            if (selectedYear == MAX_YEAR && selectedMonth >= MAX_MONTH_FOR_MAX_YEAR) {
                // Sudah di tahun dan bulan maksimal, jangan maju
                showWarningToast("Tidak ada minggu selanjutnya untuk tahun 2025.");
                return;
            }

            selectedYear = (selectedMonth == 11) ? selectedYear + 1 : selectedYear;
            selectedMonth = (selectedMonth + 1) % 12;

            // Jika setelah maju, tahun melebihi MAX_YEAR atau (di MAX_YEAR dan bulan melebihi MAX_MONTH_FOR_MAX_YEAR)
            if (selectedYear > MAX_YEAR || (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR)) {
                selectedYear = MAX_YEAR;
                selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
                showWarningToast("Tidak ada minggu selanjutnya untuk tahun 2025.");
            }

            weekStart = 0;
            weekEnd = 0;

            populateWeekGrid();
            updateWeekSelectionDisplay();
            updateNavigationButtonStates(); // Perbarui status tombol navigasi
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
                // PERBAIKAN: Pastikan selectedCalendarType tidak null saat membuat pesan Toast
                String typeForToast = selectedCalendarType.isEmpty() ? "date" : selectedCalendarType.toLowerCase();
                showWarningToast("Please select a valid " + typeForToast + "!");
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

    // --- Metode untuk memperbarui status tombol navigasi (grey adalah R.color.gray) ---
    private void updateNavigationButtonStates() {
        // Tahun
        // Tombol kanan tahun dinonaktifkan jika yearEnd sudah mencapai MAX_YEAR
        // dan sudah berada di blok 15 tahun terakhir yang mencakup MAX_YEAR.
        if (yearEnd == MAX_YEAR && yearStart == MAX_YEAR - 14) {
            btnYearRight.setEnabled(false);
        } else {
            btnYearRight.setEnabled(true);
        }
        // Pastikan R.color.grey sudah didefinisikan di colors.xml
        btnYearRight.setColorFilter(ContextCompat.getColor(this, btnYearRight.isEnabled() ? android.R.color.black : R.color.grey));
        // Tombol kiri tahun: asumsikan selalu bisa mundur kecuali ada batasan minimum yang eksplisit (tidak ada di kode ini)
        btnYearLeft.setColorFilter(ContextCompat.getColor(this, android.R.color.black)); // Selalu hitam

        // Bulan & Minggu (tombol sama untuk keduanya)
        boolean canGoNextMonth = !(selectedYear == MAX_YEAR && selectedMonth >= MAX_MONTH_FOR_MAX_YEAR);
        btnMonthRight.setEnabled(canGoNextMonth);
        btnMonthRight.setColorFilter(ContextCompat.getColor(this, btnMonthRight.isEnabled() ? android.R.color.black : R.color.grey));
        btnMonthLeft.setColorFilter(ContextCompat.getColor(this, android.R.color.black)); // Selalu hitam

        btnWeekRight.setEnabled(canGoNextMonth);
        btnWeekRight.setColorFilter(ContextCompat.getColor(this, btnWeekRight.isEnabled() ? android.R.color.black : R.color.grey));
        btnWeekLeft.setColorFilter(ContextCompat.getColor(this, android.R.color.black)); // Selalu hitam

        // Untuk tombol kiri, asumsikan selalu bisa mundur jauh ke belakang kecuali ada batasan minimum yang ingin diizinkan
        // Anda bisa menambahkan logika di sini jika ada tahun/bulan minimum yang ingin diizinkan
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
                btn.setTextColor(ContextCompat.getColor(this, R.color.grey)); // Warna teks abu-abu jika tidak bisa diklik
            } else {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Warna teks hitam
            }


            btn.setOnClickListener(v -> {
                if (!btn.isEnabled()) return; // Jangan lakukan apa-apa jika tombol tidak aktif

                selectedYear = yearClicked;
                // --- Jika tahun yang dipilih adalah MAX_YEAR, pastikan bulan tidak melebihi MAX_MONTH_FOR_MAX_YEAR ---
                if (selectedYear == MAX_YEAR && selectedMonth > MAX_MONTH_FOR_MAX_YEAR) {
                    selectedMonth = MAX_MONTH_FOR_MAX_YEAR;
                }
                weekStart = 0;
                weekEnd = 0;

                updateYearSelection();
                populateMonthGrid(); // Panggil lagi untuk render ulang bulan dengan pembatasan
                updateMonthGridDisplay();
                populateWeekGrid(); // Panggil lagi untuk render ulang minggu dengan pembatasan baru
                updateWeekSelectionDisplay(); // Panggil di sini juga untuk memastikan display minggu terupdate
                updateNavigationButtonStates(); // Perbarui status tombol navigasi
            });
            yearGrid.addView(btn);
        }
        updateYearSelection(); // Panggil di sini untuk pertama kali
        updateNavigationButtonStates(); // Panggil setelah update grid
    }

    private void populateMonthGrid() {
        monthGrid.removeAllViews();
        tvMonthYear.setText(String.valueOf(selectedYear)); // Pastikan tahun di tvMonthYear selalu terupdate

        for (int i = 0; i < 12; i++) {
            final int index = i;
            // --- Pembatasan klik bulan ---
            boolean isClickable = true;
            if (selectedYear == MAX_YEAR && index > MAX_MONTH_FOR_MAX_YEAR) {
                isClickable = false;
            }

            Button btn = createItem(getMonthName(i), i == selectedMonth, true);
            btn.setEnabled(isClickable);
            if (!isClickable) {
                btn.setTextColor(ContextCompat.getColor(this, R.color.grey)); // Warna teks abu-abu jika tidak bisa diklik
            } else {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Warna teks hitam
            }

            btn.setOnClickListener(v -> {
                if (!btn.isEnabled()) return; // Jangan lakukan apa-apa jika tombol tidak aktif

                selectedMonth = index;
                weekStart = 0;
                weekEnd = 0;
                updateMonthSelection(); // Call this method to update the appearance of month buttons
                populateWeekGrid(); // Render ulang grid minggu karena bulan berubah
                updateWeekSelectionDisplay();
                updateNavigationButtonStates(); // Perbarui status tombol navigasi
            });
            monthGrid.addView(btn);
        }
        updateMonthSelection(); // Call this method initially to set the correct appearance
        updateNavigationButtonStates(); // Panggil setelah update grid
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
        cal.set(Calendar.DAY_OF_MONTH, 1); // Set ke hari pertama bulan

        // Opsional: Atur hari pertama dalam seminggu jika diperlukan (default locale-dependent).
        // Ini sangat mempengaruhi hasil getActualMaximum(WEEK_OF_MONTH).
        // Misalnya, jika Anda ingin minggu selalu dimulai Senin:
        // cal.setFirstDayOfWeek(Calendar.MONDAY);
        // cal.setMinimalDaysInFirstWeek(1); // Set minimal hari untuk minggu pertama, sering 1 atau 4

        // Default: semua bulan hanya sampai week 4
        int finalNumWeeksForDisplay = 4;

        // Pengecualian: Maret, Juni, Agustus, November
        // Untuk bulan-bulan ini, gunakan jumlah minggu aktual yang dihitung oleh Calendar,
        // namun pastikan tidak melebihi 5 (karena UI dirancang untuk maksimal 5 tombol).
        if (selectedMonth == Calendar.MARCH ||
                selectedMonth == Calendar.JUNE ||
                selectedMonth == Calendar.AUGUST ||
                selectedMonth == Calendar.NOVEMBER) {

            int calculatedNumWeeks = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
            finalNumWeeksForDisplay = Math.min(calculatedNumWeeks, 5); // Batasi maksimal 5 minggu
        }

        // Jumlah maksimum tombol minggu yang akan ditampilkan (sesuai layout UI)
        int maxButtonsToDisplay = 5;

        for (int i = 1; i <= maxButtonsToDisplay; i++) {
            final int week = i;
            boolean selected = (week >= weekStart && week <= weekEnd && weekEnd != 0) || (week == weekStart && weekEnd == 0 && weekStart != 0);

            // --- Pembatasan klik minggu berdasarkan `finalNumWeeksForDisplay` ---
            boolean isClickable = true;
            if (week > finalNumWeeksForDisplay) {
                isClickable = false;
            }

            // PERUBAHAN DI SINI: Ganti "WEEK" menjadi "Week"
            Button btn = createItem("Week " + week, selected, true);
            btn.setEnabled(isClickable);
            if (!isClickable) {
                btn.setTextColor(ContextCompat.getColor(this, R.color.grey));
            } else {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }

            btn.setOnClickListener(v -> {
                if (!btn.isEnabled()) return;

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


    // Metode helper untuk mengatur tampilan tombol (DIUBAH UNTUK TIDAK MENGGUNAKAN BACKGROUND UNTUK PILIHAN DATE)
    private void setupButtonAppearance(Button btn, boolean selected, boolean isRounded) {
        if (selected) {
            // Hanya teks bold, tanpa background warna
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent)); // Background transparan
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Teks HITAM
            btn.setTypeface(null, Typeface.BOLD);
        } else {
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            // Biarkan warna teks abu-abu jika disabled (diatur di updateYearGrid/populateMonthGrid)
            // Jika tidak disabled, set ke hitam normal
            if (btn.isEnabled()) {
                btn.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Teks hitam untuk tidak dipilih
            }
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

    // --- METODE BARU YANG DITAMBAHKAN ---
    private void updateYearSelection() {
        for (int i = 0; i < yearGrid.getChildCount(); i++) {
            Button btn = (Button) yearGrid.getChildAt(i);
            // Pastikan Anda membandingkan dengan nilai integer dari teks tombol
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

    private void updateWeekSelectionDisplay() { // Perbaikan: Hapus 'void' ganda di sini
        for (int i = 0; i < weekGrid.getChildCount(); i++) {
            Button btn = (Button) weekGrid.getChildAt(i);
            int weekNum = i + 1; // Minggu dimulai dari 1

            boolean selected = false;
            if (weekStart != 0) {
                if (weekEnd != 0) { // Jika rentang minggu dipilih
                    selected = (weekNum >= weekStart && weekNum <= weekEnd);
                } else { // Jika hanya satu minggu dipilih (weekEnd masih 0 atau sama dengan weekStart)
                    selected = (weekNum == weekStart);
                }
            }
            setupButtonAppearance(btn, selected, true);
        }
        // Pastikan juga TextView untuk bulan minggu diperbarui
        tvWeekMonth.setText(getMonthName(selectedMonth) + " " + selectedYear);
    }
    // --- AKHIR METODE BARU YANG DITAMBAHKAN ---


    private String getMonthName(int index) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (index >= 0 && index < months.length) {
            return months[index];
        }
        return "";
    }

    private boolean validateSelection() {
        // --- Validasi juga harus mempertimbangkan batas MAX_YEAR dan MAX_MONTH_FOR_MAX_YEAR ---
        if (selectedYear == 0) return false; // Tahun harus dipilih

        // PERBAIKAN: Tambahkan pemeriksaan null untuk selectedCalendarType
        if (selectedCalendarType == null || selectedCalendarType.isEmpty()) {
            // Ini bisa terjadi jika Intent tidak membawa "type" atau string kosong
            // Dalam kasus ini, kita bisa berasumsi bahwa setidaknya tahun harus dipilih
            // atau tambahkan logika validasi default jika tidak ada tipe yang spesifik
            return selectedYear != 0; // Minimal tahun harus dipilih
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
                // Jika selectedCalendarType tidak cocok dengan kasus di atas,
                // mungkin kita juga harus menganggapnya sebagai validasi dasar.
                return false; // Atau return selectedYear != 0; tergantung kebutuhan Anda
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