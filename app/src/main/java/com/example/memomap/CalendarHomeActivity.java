package com.example.memomap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Ubah nama kelas menjadi CalendarHomeActivity
public class CalendarHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private final List<CalendarModel> monthsData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_home); // Layout XML tetap sama

        recyclerView = findViewById(R.id.recycler_view_item_calendar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        prepareMonthsData();
        adapter = new CalendarAdapter(monthsData);
        recyclerView.setAdapter(adapter);
    }

    private void prepareMonthsData() {
        int currentYear = 2025; // Sesuai tanggal hari ini, 2025 adalah tahun yang relevan.

        // Isi data bulan seperti sebelumnya...
        // JANUARI
        Map<Integer, Integer> janColors = new HashMap<>();
        janColors.put(1, R.color.orange);
        janColors.put(2, R.color.pink);
        janColors.put(3, R.color.pink);
        janColors.put(4, R.color.purple);
        janColors.put(5, R.color.blue);
        janColors.put(6, R.color.blue);
        janColors.put(7, R.color.green);
        janColors.put(8, R.color.purple);
        janColors.put(9, R.color.blue);
        janColors.put(10, R.color.pink);
        janColors.put(11, R.color.green);
        janColors.put(12, R.color.green);
        janColors.put(13, R.color.blue);
        janColors.put(14, R.color.orange);
        janColors.put(15, R.color.green);
        janColors.put(16, R.color.purple);
        janColors.put(17, R.color.orange);
        janColors.put(18, R.color.pink);
        janColors.put(19, R.color.green);
        janColors.put(20, R.color.blue);

        monthsData.add(new CalendarModel("JAN", currentYear, Calendar.WEDNESDAY, 31, janColors));


        // ... tambahkan bulan lainnya seperti yang sudah saya berikan sebelumnya
        // FEBRUARI
        monthsData.add(new CalendarModel("FEB", currentYear, Calendar.SATURDAY, 28));
        // MARET
        monthsData.add(new CalendarModel("MAR", currentYear, Calendar.SATURDAY, 31));
        // APRIL
        monthsData.add(new CalendarModel("APR", currentYear, Calendar.TUESDAY, 30));
        // MEI
        monthsData.add(new CalendarModel("MAY", currentYear, Calendar.THURSDAY, 31));
        // JUNI
        monthsData.add(new CalendarModel("JUN", currentYear, Calendar.SUNDAY, 30));
        // JULI
        monthsData.add(new CalendarModel("JUL", currentYear, Calendar.TUESDAY, 31));
        // AGUSTUS
        monthsData.add(new CalendarModel("AUG", currentYear, Calendar.FRIDAY, 31));
        // SEPTEMBER
        monthsData.add(new CalendarModel("SEP", currentYear, Calendar.MONDAY, 30));
        // OKTOBER
        monthsData.add(new CalendarModel("OCT", currentYear, Calendar.WEDNESDAY, 31));
        // NOVEMBER
        monthsData.add(new CalendarModel("NOV", currentYear, Calendar.SATURDAY, 30));
        // DESEMBER
        monthsData.add(new CalendarModel("DEC", currentYear, Calendar.MONDAY, 31));
    }
}