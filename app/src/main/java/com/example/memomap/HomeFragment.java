package com.example.memomap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

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
    private List<JournalCardModel> journalList;

    // Keep track of the current month and year displayed in the app
    private Calendar currentCalendar;
    // Keep track of the actual current date (today) for future/past logic
    private Calendar todayCalendar;

    // Tambahkan deklarasi untuk btn_profile
//    private ImageButton btnProfile;
    private ShapeableImageView btnProfile;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.YEAR, 2025);
        currentCalendar.set(Calendar.MONTH, Calendar.JANUARY); // January is 0
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1); // Set to 1st day for consistency
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0); // Reset time to start of day for accurate month comparison
        currentCalendar.set(Calendar.MINUTE, 0);
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);

        // Initialize todayCalendar to the actual current date: July 21, 2025
        todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.YEAR, 2025);
        todayCalendar.set(Calendar.MONTH, Calendar.JANUARY); // JANUARY
        todayCalendar.set(Calendar.DAY_OF_MONTH, 21);
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0); // Reset time to start of day for accurate month comparison
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);

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

        // Use checkAndHandleMonthClick to manage clickability based on future status
        textMonthNov.setOnClickListener(v -> checkAndHandleMonthClick(textMonthNov));
        textMonthDec.setOnClickListener(v -> checkAndHandleMonthClick(textMonthDec));
        textMonthFeb.setOnClickListener(v -> checkAndHandleMonthClick(textMonthFeb));
        textMonthMar.setOnClickListener(v -> checkAndHandleMonthClick(textMonthMar));

        // Set listener for the calendar button
        ImageButton btnCalendar = view.findViewById(R.id.btn_calendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarHomeActivity.class);
                startActivity(intent);
            }
        });

        // TEMUKAN btn_profile DAN TAMBAHKAN LISTENER-NYA DI SINI
        btnProfile = view.findViewById(R.id.btn_profile);
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String avatarPath = prefs.getString("avatarPath", null);

        if (avatarPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
            if (bitmap != null) {
                btnProfile.setImageBitmap(bitmap);
            }
        } else {
            btnProfile.setImageResource(R.drawable.ic_profile); // fallback icon
        }
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk memulai ProfileActivity
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent); // Mulai aktivitas ProfileActivity
            }
        });


        // Initial setup of the calendar and journal data
        updateCalendarUI();
    }

    /**
     * Updates the UI elements for month selection and loads journal data.
     * This method handles month name display, coloring, and clickability.
     */
    private void updateCalendarUI() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

        // --- Update Current Month (textMonthCurrent) ---
        String currentMonthName = monthFormat.format(currentCalendar.getTime()).toUpperCase();
        textMonthCurrent.setText(currentMonthName);
        textMonthCurrent.setTextColor(getResources().getColor(R.color.black, null));
        textMonthCurrent.setClickable(true);
        textMonthCurrent.setEnabled(true);

        // --- Update Previous Months (textMonthNov, textMonthDec) ---
        // These are always in the past relative to the current active month,
        // and per your logic, past months are always black and clickable.
        Calendar prev1MonthCal = (Calendar) currentCalendar.clone();
        prev1MonthCal.add(Calendar.MONTH, -1);
        textMonthDec.setText(monthFormat.format(prev1MonthCal.getTime()).toUpperCase());
        textMonthDec.setTextColor(getResources().getColor(R.color.black, null));
        textMonthDec.setClickable(true);
        textMonthDec.setEnabled(true);

        Calendar prev2MonthCal = (Calendar) currentCalendar.clone();
        prev2MonthCal.add(Calendar.MONTH, -2);
        textMonthNov.setText(monthFormat.format(prev2MonthCal.getTime()).toUpperCase());
        textMonthNov.setTextColor(getResources().getColor(R.color.black, null));
        textMonthNov.setClickable(true);
        textMonthNov.setEnabled(true);

        // --- Update Next Months (textMonthFeb, textMonthMar) ---
        Calendar next1MonthCal = (Calendar) currentCalendar.clone();
        next1MonthCal.add(Calendar.MONTH, 1);
        textMonthFeb.setText(monthFormat.format(next1MonthCal.getTime()).toUpperCase());
        applyMonthStyle(textMonthFeb, next1MonthCal); // Apply style based on future status

        Calendar next2MonthCal = (Calendar) currentCalendar.clone();
        next2MonthCal.add(Calendar.MONTH, 2);
        textMonthMar.setText(monthFormat.format(next2MonthCal.getTime()).toUpperCase());
        applyMonthStyle(textMonthMar, next2MonthCal); // Apply style based on future status

        // --- Control Next/Previous Buttons ---
        // btnPrevious is always enabled as past months are always accessible
        btnPrevious.setEnabled(true);
        btnPrevious.setAlpha(1.0f); // Ensure it's not faded

        // btnNext is enabled only if the next month displayed (next1MonthCal) is NOT in the future
        if (isFutureMonth(next1MonthCal)) {
            btnNext.setEnabled(false);
            btnNext.setAlpha(0.5f); // Fade out if disabled
        } else {
            btnNext.setEnabled(true);
            btnNext.setAlpha(1.0f); // Full opacity if enabled
        }

        // Load journal data for the currently displayed month
        loadJournalDataForMonth(currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR));
    }

    /**
     * Applies styling (color and clickability) to a TextView based on whether
     * the associated month is in the future relative to `todayCalendar`.
     * @param textView The TextView to style.
     * @param monthCalendar The Calendar object representing the month for the TextView.
     */
    private void applyMonthStyle(TextView textView, Calendar monthCalendar) {
        if (isFutureMonth(monthCalendar)) {
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
     * Checks if a given month is in the future relative to `todayCalendar`.
     * The comparison is done at the beginning of the month.
     * @param monthToCheck The Calendar object representing the month to check.
     * @return true if `monthToCheck` is after `todayCalendar` (month/year), false otherwise.
     */
    private boolean isFutureMonth(Calendar monthToCheck) {
        // Create Calendar instances representing the start of the month for comparison
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

        // Compare the two calendars. `after` checks if checkMonthStart is strictly later than todayMonthStart.
        return checkMonthStart.after(todayMonthStart);
    }

    /**
     * Handles clicks on month TextViews, checking if they are enabled before proceeding.
     * @param clickedMonthTextView The TextView that was clicked.
     */
    private void checkAndHandleMonthClick(TextView clickedMonthTextView) {
        if (clickedMonthTextView.isEnabled()) {
            goToSpecificMonth(clickedMonthTextView);
        } else {
            Toast.makeText(getContext(), "Belum tersedia untuk bulan ini", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads journal data for the specified month and year.
     * IMPORTANT: Replace this with your actual data fetching logic (e.g., from a database).
     * @param month The Calendar month constant (e.g., Calendar.JANUARY).
     * @param year The year.
     */
    private void loadJournalDataForMonth(int month, int year) {
        journalList.clear(); // Clear existing data

        // --- Your actual data loading logic goes here ---
        // This is where you would query your database or other data source
        // based on the 'month' and 'year' parameters.

        // Example: Only showing January 2025 data for demonstration.
        // You MUST expand this to load data for all relevant months/years.
        if (month == Calendar.JANUARY && year == 2025) {
            journalList.add(new JournalCardModel("WED", "01", "JAN", "Hari ini aku merasa sedikit lebih ringan… karena ditemani.", 0, 0, R.color.pink));   // Calm/Happy
            journalList.add(new JournalCardModel("THU", "02", "JAN", "Bersihin balkon istana, nggak terlalu beku sekarang", 1, 2, R.color.pink));         // Calm/Happy
            journalList.add(new JournalCardModel("FRI", "03", "JAN", "Nulis jurnal satu kalimat ternyata bikin hati nggak dingin lagi.", 2, 1, R.color.purple)); // Very Happy
            journalList.add(new JournalCardModel("SAT", "04", "JAN", "Nulis memo pas badai salju, rasanya kayak ngobrol sama diri sendiri.", 5, 0, R.color.orange)); // Neutral/Meh
            journalList.add(new JournalCardModel("SUN", "05", "JAN", "Hari ini aku centang semua to-do list. Produktif banget!", 0, 3, R.color.purple));  // Very Happy
            journalList.add(new JournalCardModel("MON", "06", "JAN", "Setiap hari cuma satu kalimat, tapi rasanya hidupku makin jelas.", 2, 6, R.color.pink)); // Calm/Happy
            journalList.add(new JournalCardModel("TUE", "07", "JAN", "Anna bawain teh hangat", 2, 0, R.color.pink));                                         // Calm/Happy
            journalList.add(new JournalCardModel("WED", "08", "JAN", "Udara segar bikin mood oke", 3, 0, R.color.pink));                                     // Calm/Happy
            journalList.add(new JournalCardModel("THU", "09", "JAN", "Bersihkan balkon istana, saljunya berkurang", 1, 4, R.color.pink));                    // Calm/Happy
            journalList.add(new JournalCardModel("FRI", "10", "JAN", "Ngopi sore sambil ngerjain tugas kecil-kecilan", 3, 1, R.color.pink));                 // Calm/Happy
            journalList.add(new JournalCardModel("SAT", "11", "JAN", "Hari ini aku centang semua to-do list… rasanya hebat!", 4, 0, R.color.purple));        // Very Happy
            journalList.add(new JournalCardModel("SUN", "12", "JAN", "Tidur seharian, badan dan otak butuh recharge", 0, 0, R.color.green));                 // Tired/Sad
            journalList.add(new JournalCardModel("MON", "13", "JAN", "Anna bilang aku lebih sering senyum sekarang.", 1, 3, R.color.purple));                // Very Happy
            journalList.add(new JournalCardModel("TUE", "14", "JAN", "Anna bawa teh coklat panas, suasana jadi lebih cair.", 2, 2, R.color.pink));           // Calm/Happy
            journalList.add(new JournalCardModel("WED", "15", "JAN", "Hari ini aku nyanyi sebentar. Rasanya seperti ada kehangatan kembali.", 1, 1, R.color.purple)); // Very Happy
            journalList.add(new JournalCardModel("THU", "16", "JAN", "Aku coba menulis memo, rasanya aneh tapi sedikit lega", 4, 0, R.color.orange));        // Neutral/Meh
            journalList.add(new JournalCardModel("FRI", "17", "JAN", "Tugas mulai numpuk, tapi masih bisa santai dikit", 2, 2, R.color.blue));                // Worried/Low
            journalList.add(new JournalCardModel("SAT", "18", "JAN", "Aku butuh support hari ini.", 3, 0, R.color.blue));                                     // Worried/Low
            journalList.add(new JournalCardModel("SUN", "19", "JAN", "Kayaknya mulai butuh refreshing", 1, 1, R.color.green));                                // Tired/Sad
            journalList.add(new JournalCardModel("MON", "20", "JAN", "Mulai minggu dengan pikiran berat soal kerajaan", 0, 4, R.color.blue));                 // Worried/Low
        }
        // Add more 'else if' blocks for other months/years if you have specific dummy data for them
        else if (month == Calendar.JULY && year == 2025) {
            journalList.add(new JournalCardModel("MON", "21", "JUL", "Hari ini!", 0, 0, R.color.purple));
            journalList.add(new JournalCardModel("TUE", "22", "JUL", "Besok nih!", 1, 0, R.color.blue));
        }
        else if (month == Calendar.JUNE && year == 2025) {
            journalList.add(new JournalCardModel("SUN", "15", "JUN", "Udah lewat dari dulu", 0, 0, R.color.orange));
        }
        else {
            Toast.makeText(getContext(), "No entries for " + new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentCalendar.getTime()), Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged(); // Notify adapter that data has changed
    }

    /**
     * Decrements the current displayed month and updates the UI.
     */
    private void goToPreviousMonth() {
        currentCalendar.add(Calendar.MONTH, -1);
        updateCalendarUI();
    }

    /**
     * Increments the current displayed month and updates the UI.
     * It prevents navigating to future months if they are beyond `todayCalendar`.
     */
    private void goToNextMonth() {
        Calendar tempNextMonth = (Calendar) currentCalendar.clone();
        tempNextMonth.add(Calendar.MONTH, 1);

        // Check if the next month would be in the future relative to today
        if (isFutureMonth(tempNextMonth)) {
            Toast.makeText(getContext(), "Belum ada entri untuk bulan ini", Toast.LENGTH_SHORT).show();
        } else {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarUI();
        }
    }

    /**
     * Navigates to a specific month based on the clicked TextView.
     * This handles year transitions for months like Nov/Dec (previous year) from Jan,
     * or Jan/Feb (next year) from Dec.
     * @param clickedMonthTextView The TextView representing the month to navigate to.
     */
    private void goToSpecificMonth(TextView clickedMonthTextView) {
        String monthAbbreviation = clickedMonthTextView.getText().toString();

        int targetMonth;
        try {
            // Convert month abbreviation (e.g., "NOV") to Calendar month constant (e.g., Calendar.NOVEMBER)
            SimpleDateFormat parseFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(parseFormat.parse(monthAbbreviation));
            targetMonth = tempCal.get(Calendar.MONTH);
        } catch (java.text.ParseException e) {
            Toast.makeText(getContext(), "Error parsing month", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        // Clone currentCalendar to work with the target month/year
        Calendar targetCalendar = (Calendar) currentCalendar.clone();
        targetCalendar.set(Calendar.MONTH, targetMonth);

        int currentDisplayedMonth = currentCalendar.get(Calendar.MONTH);

        // Logic to adjust year when jumping between months that cross year boundaries
        // E.g., if current is JAN and user clicks DEC, it's DEC of previous year.
        // If current is DEC and user clicks JAN, it's JAN of next year.
        if (targetMonth == Calendar.DECEMBER && currentDisplayedMonth == Calendar.JANUARY) {
            targetCalendar.add(Calendar.YEAR, -1); // From Jan to Dec -> previous year
        } else if (targetMonth == Calendar.JANUARY && currentDisplayedMonth == Calendar.DECEMBER) {
            targetCalendar.add(Calendar.YEAR, 1); // From Dec to Jan -> next year
        }

        // Before setting, check if the calculated target month is in the future
        if (isFutureMonth(targetCalendar)) {
            Toast.makeText(getContext(), "Belum tersedia untuk bulan ini", Toast.LENGTH_SHORT).show();
        } else {
            currentCalendar = targetCalendar; // Update currentCalendar with the new target
            updateCalendarUI(); // Refresh the UI
        }
    }
}