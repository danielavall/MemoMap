package com.example.memomap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView; // Import TextView
import android.widget.Toast;   // Import Toast for quick feedback

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar; // Import Calendar for date manipulation
import java.util.List;
import java.text.SimpleDateFormat; // For formatting month names
import java.util.Locale; // For locale-specific month names

public class HomeFragment extends Fragment {

    // Removed ARG_PARAM1/ARG_PARAM2 as they are not used in your provided code
    // private static final String ARG_PARAM1 = "param1";
    // private static final String ARG_PARAM2 = "param2";
    // private String mParam1;
    // private String mParam2;

    // Declare your month selector views
    private TextView textMonthNov;
    private TextView textMonthDec;
    private ImageButton btnPrevious;
    private TextView textMonthCurrent;
    private ImageButton btnNext;
    private TextView textMonthFeb;
    private TextView textMonthMar;

    private RecyclerView recyclerView;
    private JournalCardAdapter adapter;
    private List<JournalCardModel> journalList; // Make it a member variable

    // Keep track of the current month and year
    private Calendar currentCalendar;

    public HomeFragment() {
        // Required empty public constructor
    }

    // You might not need newInstance if you're not passing arguments this way
    /*
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize currentCalendar to the current date or a default (e.g., January 2025)
        currentCalendar = Calendar.getInstance();
        // For testing, let's set it to January 2025 as your initial data suggests
        currentCalendar.set(Calendar.YEAR, 2025);
        currentCalendar.set(Calendar.MONTH, Calendar.JANUARY); // January is 0
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1); // Set to 1st day for consistency

        // No need for mParam1/mParam2 if newInstance isn't used to pass them
        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.recycler_journal_card);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize JournalList (empty for now, will be populated by updateCalendarUI)
        journalList = new ArrayList<>();
        adapter = new JournalCardAdapter(journalList);
        recyclerView.setAdapter(adapter);

        // Initialize month selector views
        textMonthNov = view.findViewById(R.id.text_month_nov);
        textMonthDec = view.findViewById(R.id.text_month_dec);
        btnPrevious = view.findViewById(R.id.btn_previous);
        textMonthCurrent = view.findViewById(R.id.text_month_current);
        btnNext = view.findViewById(R.id.btn_next);
        textMonthFeb = view.findViewById(R.id.text_month_feb);
        textMonthMar = view.findViewById(R.id.text_month_mar);

        // Set Click Listeners for month navigation
        btnPrevious.setOnClickListener(v -> goToPreviousMonth());
        btnNext.setOnClickListener(v -> goToNextMonth());
        textMonthNov.setOnClickListener(v -> goToSpecificMonth(Calendar.NOVEMBER));
        textMonthDec.setOnClickListener(v -> goToSpecificMonth(Calendar.DECEMBER));
        textMonthFeb.setOnClickListener(v -> goToSpecificMonth(Calendar.FEBRUARY));
        textMonthMar.setOnClickListener(v -> goToSpecificMonth(Calendar.MARCH));

        // Set listener for the calendar button
        ImageButton btnCalendar = view.findViewById(R.id.btn_calendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarHomeActivity.class);
                startActivity(intent);
            }
        });

        // Initial setup of the calendar and journal data
        updateCalendarUI();
    }

    // --- Helper Methods for Month Navigation ---

    private void updateCalendarUI() {
        // Update current month display
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        String currentMonthName = monthFormat.format(currentCalendar.getTime()).toUpperCase();
        textMonthCurrent.setText(currentMonthName);

        // Update previous months display
        Calendar prev2Month = (Calendar) currentCalendar.clone();
        prev2Month.add(Calendar.MONTH, -2);
        textMonthNov.setText(monthFormat.format(prev2Month.getTime()).toUpperCase());

        Calendar prev1Month = (Calendar) currentCalendar.clone();
        prev1Month.add(Calendar.MONTH, -1);
        textMonthDec.setText(monthFormat.format(prev1Month.getTime()).toUpperCase());

        // Update next months display
        Calendar next1Month = (Calendar) currentCalendar.clone();
        next1Month.add(Calendar.MONTH, 1);
        textMonthFeb.setText(monthFormat.format(next1Month.getTime()).toUpperCase());

        Calendar next2Month = (Calendar) currentCalendar.clone();
        next2Month.add(Calendar.MONTH, 2);
        textMonthMar.setText(monthFormat.format(next2Month.getTime()).toUpperCase());

        // Now, update the journal entries for the currently selected month
        // In a real app, you would fetch data from a database or a more dynamic source
        // For this example, we'll simulate it with fixed data per month.
        loadJournalDataForMonth(currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR));
    }

    private void loadJournalDataForMonth(int month, int year) {
        journalList.clear(); // Clear existing data

        // IMPORTANT: In a real app, you would fetch this data dynamically from a database
        // or other storage based on the selected month and year.
        // For demonstration, we'll just populate January data.
        // You'll need to expand this to handle data for other months.
        if (month == Calendar.JANUARY && year == 2025) { // Check for specific month and year
            journalList.add(new JournalCardModel("WED", "01", "JAN", "Tahun Baru di rumah aja, bingung mau mulai dari mana", 0, 0, R.color.orange));
            journalList.add(new JournalCardModel("THU", "02", "JAN", "Mulai ngerapihin to-do list dan niat tahun ini", 1, 2, R.color.pink));
            journalList.add(new JournalCardModel("FRI", "03", "JAN", "Beresin file kuliah semester lalu sambil nostalgia", 2, 1, R.color.pink));
            journalList.add(new JournalCardModel("SAT", "04", "JAN", "Quality time bareng keluarga seharian penuh", 5, 0, R.color.purple));
            journalList.add(new JournalCardModel("SUN", "05", "JAN", "Overthinking soal kuliah yang bakal mulai lagi", 0, 3, R.color.blue));
            journalList.add(new JournalCardModel("MON", "06", "JAN", "Hari pertama kuliah, belum siap mental", 2, 6, R.color.blue));
            journalList.add(new JournalCardModel("TUE", "07", "JAN", "Ngoding sampai malam, stuck di layout XML", 2, 0, R.color.green));
            journalList.add(new JournalCardModel("WED", "08", "JAN", "Ketemu teman baru, mulai merasa lebih nyaman", 3, 0, R.color.purple));
            journalList.add(new JournalCardModel("THU", "09", "JAN", "Presentasi perdana semester ini, lumayan grogi", 1, 4, R.color.blue));
            journalList.add(new JournalCardModel("FRI", "10", "JAN", "Ngopi sore sambil ngerjain tugas kecil-kecilan", 3, 1, R.color.pink));
            journalList.add(new JournalCardModel("SAT", "11", "JAN", "Bantu mama belanja mingguan, lumayan capek", 4, 0, R.color.green));
            journalList.add(new JournalCardModel("SUN", "12", "JAN", "Tidur seharian, badan dan otak butuh recharge", 0, 0, R.color.green));
            journalList.add(new JournalCardModel("MON", "13", "JAN", "Mulai minggu dengan kelas pagi yang berat banget", 1, 3, R.color.blue));
            journalList.add(new JournalCardModel("TUE", "14", "JAN", "Ngoding Android bareng temen, seru tapi pusing", 2, 2, R.color.orange));
            journalList.add(new JournalCardModel("WED", "15", "JAN", "Kelas full dari pagi sampe sore, lumayan lelah", 1, 1, R.color.green));
            journalList.add(new JournalCardModel("THU", "16", "JAN", "Dosen kasih motivasi soal masa depan, semangat naik!", 4, 0, R.color.purple));
            journalList.add(new JournalCardModel("FRI", "17", "JAN", "Tugas mulai numpuk, tapi masih bisa santai dikit", 2, 2, R.color.orange));
            journalList.add(new JournalCardModel("SAT", "18", "JAN", "Nugas di kafe sambil dengerin musik jazz", 3, 0, R.color.pink));
            journalList.add(new JournalCardModel("SUN", "19", "JAN", "Kayaknya mulai butuh short escape deh", 1, 1, R.color.green));
            journalList.add(new JournalCardModel("MON", "20", "JAN", "Mulai minggu dengan pikiran berat soal deadline", 0, 4, R.color.blue));
        } else {
            // For other months, you could add different data or show a "No entries" message
            // For now, it will just show an empty list
            Toast.makeText(getContext(), "No entries for " + new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentCalendar.getTime()), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged(); // Notify adapter that data has changed
    }

    private void goToPreviousMonth() {
        currentCalendar.add(Calendar.MONTH, -1); // Decrement month
        updateCalendarUI();
    }

    private void goToNextMonth() {
        currentCalendar.add(Calendar.MONTH, 1); // Increment month
        updateCalendarUI();
    }

    private void goToSpecificMonth(int targetMonth) {
        // This method allows jumping directly to a specific month (e.g., Nov, Dec, Feb, Mar)
        // Adjust the year if needed, assuming these "side" months are relative to the current month.
        // For simplicity, we'll just set the month in the current year.
        // You might need more complex logic if moving from Jan to Nov (previous year) or Dec to Feb (next year)
        // based on your exact UI design.

        // Example: If current month is JAN (0) and user clicks NOV (10), it should go to Nov of previous year.
        // If current month is MAR (2) and user clicks FEB (1), it should go to Feb of current year.

        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        if (targetMonth > currentMonth + 2) { // e.g., current is JAN (0), clicked MAR (2) -> current year is fine
            // If the target month is significantly "forward" and not just the next visible,
            // you might want to adjust the year logic here.
            // For now, assuming you are displaying +2 and -2 months from current
            // clicking on these simply sets the month in the current year.
        } else if (targetMonth < currentMonth - 2) { // e.g., current is MAR (2), clicked NOV (10) -> previous year
            // Similar to above, if target is significantly "backward"
            // For now, assuming clicking on these simply sets the month in the current year.
        }

        currentCalendar.set(Calendar.MONTH, targetMonth);
        updateCalendarUI();
    }
}