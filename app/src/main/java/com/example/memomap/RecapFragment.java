package com.example.memomap;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;
import android.view.Gravity; // Import Gravity

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Calendar;

public class RecapFragment extends Fragment {
    private TextView textHint;
    private ImageView arrowHint, boatImage;
    private FrameLayout layoutRecap;
    private ViewPager2 viewPagerRecap;
    private LinearLayout linearLayoutIndicator;

    private ImageButton backButtonRecapCards;

    private Button yearButton, monthButton, weekButton;
    private Button createButton;

    private TranslateAnimation floatAnimation;

    private View rootView;

    private int selectedYear = 0;
    private int weekStart = 0;
    private int weekEnd = 0; // Tetap 0 sebagai indikator "tidak dipilih" atau "single week" jika weekStart saja yang ada
    private int selectedMonth = -1;

    private TextView title, subtitle;
    private LinearLayout dateButtons;

    // private TextView quoteTextView; // HAPUS ATAU UBAH INI JIKA SUDAH TIDAK ADA DI XML DENGAN ID INI
    private TextView tvLoadingQuote; // Deklarasi baru untuk TextView kutipan
    private LinearLayout loadingScreenLayout;
    private RelativeLayout layoutMainContent;
    private LinearLayout hintLayout;

    private final int[] imageResources = {
            R.drawable.recap1,
            R.drawable.recap2,
            R.drawable.recap3,
            R.drawable.recap4,
            R.drawable.recap5,
            R.drawable.recap6,
            R.drawable.recap7,
    };

    private final String[] recapTexts = {
            "You achieved your July goals!",
            "Your mood improved this week.",
            "You completed 3 tasks yesterday!",
            "You are awesome!",
            "You are beautiful!",
            "You have just promoted yesterday!",
            "You completed your daily goals!",
    };

    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    private ActivityResultLauncher<Intent> calendarRecapLauncher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recap, container, false);

        yearButton = rootView.findViewById(R.id.yearButton);
        monthButton = rootView.findViewById(R.id.monthButton);
        weekButton = rootView.findViewById(R.id.weekButton);
        createButton = rootView.findViewById(R.id.createButton);

        boatImage = rootView.findViewById(R.id.boat);
        tvLoadingQuote = rootView.findViewById(R.id.tv_loading_quote); // Inisialisasi TextView kutipan yang baru

        textHint = rootView.findViewById(R.id.textHint);
        arrowHint = rootView.findViewById(R.id.arrowHint);
        hintLayout = rootView.findViewById(R.id.hintLayout);

        loadingScreenLayout = rootView.findViewById(R.id.loadingScreenLayout);

        layoutRecap = rootView.findViewById(R.id.layoutRecap);
        viewPagerRecap = rootView.findViewById(R.id.viewPagerRecap);
        linearLayoutIndicator = rootView.findViewById(R.id.linearLayoutIndicator);
        backButtonRecapCards = rootView.findViewById(R.id.backButtonRecapCards);

        title = rootView.findViewById(R.id.title);
        subtitle = rootView.findViewById(R.id.subtitle);
        dateButtons = rootView.findViewById(R.id.dateButtons);
        layoutMainContent = rootView.findViewById(R.id.layout_main_content);


        // Pastikan semua view berhasil ditemukan, ini penting untuk debugging
        // Sesuaikan pengecekan quoteTextView menjadi tvLoadingQuote
        if (boatImage == null || textHint == null || arrowHint == null || layoutRecap == null ||
                linearLayoutIndicator == null || backButtonRecapCards == null ||
                tvLoadingQuote == null || loadingScreenLayout == null || layoutMainContent == null || // tvLoadingQuote yang diperiksa
                title == null || subtitle == null || dateButtons == null ||
                yearButton == null || monthButton == null || weekButton == null || createButton == null || hintLayout == null) {
            Log.e("RecapFragment", "View from fragment_recap.xml not found! Check IDs or includes.");
            // Anda bisa menambahkan Toast atau penanganan error lain di sini jika diperlukan
        }

        calendarRecapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String type = data.getStringExtra("type");
                            int year = data.getIntExtra("year", 0);
                            int month = data.getIntExtra("month", -1);
                            int weekStart = data.getIntExtra("weekStart", 0);
                            int weekEnd = data.getIntExtra("weekEnd", 0); // Pastikan ini juga diambil

                            boolean dateSelected = data.getBooleanExtra("dateSelected", false);

                            if (dateSelected) {
                                updateDateButtons(type, year, month, weekStart, weekEnd);
                                if (textHint != null) {
                                    textHint.setText("Click 'Create!' button!");
                                    arrowHint.setVisibility(View.GONE);
                                }
                            } else {
                                Log.d("RecapFragment", "No valid date selected from CalendarRecap, maintaining previous selection state.");
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.d("RecapFragment", "CalendarRecap canceled by system back button.");
                    }
                }
        );

        if (yearButton != null) yearButton.setText("Year");
        if (monthButton != null) monthButton.setText("Month");
        if (weekButton != null) weekButton.setText("Week");

        showHintState();

        yearButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalendarRecap.class);
            intent.putExtra("type", "year");
            if (selectedYear != 0) intent.putExtra("currentYear", selectedYear);
            if (selectedMonth != -1) intent.putExtra("currentMonth", selectedMonth);
            // Kirim weekStart dan weekEnd saat ini agar CalendarRecap bisa menampilkan pilihan sebelumnya
            if (weekStart != 0) intent.putExtra("currentWeekStart", weekStart);
            if (weekEnd != 0) intent.putExtra("currentWeekEnd", weekEnd);
            calendarRecapLauncher.launch(intent);
        });

        monthButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalendarRecap.class);
            intent.putExtra("type", "month");
            if (selectedYear != 0) intent.putExtra("currentYear", selectedYear);
            if (selectedMonth != -1) intent.putExtra("currentMonth", selectedMonth);
            // Kirim weekStart dan weekEnd saat ini agar CalendarRecap bisa menampilkan pilihan sebelumnya
            if (weekStart != 0) intent.putExtra("currentWeekStart", weekStart);
            if (weekEnd != 0) intent.putExtra("currentWeekEnd", weekEnd);
            calendarRecapLauncher.launch(intent);
        });

        weekButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalendarRecap.class);
            intent.putExtra("type", "week");
            if (selectedYear != 0) intent.putExtra("currentYear", selectedYear);
            if (selectedMonth != -1) intent.putExtra("currentMonth", selectedMonth);
            // Kirim weekStart dan weekEnd apa adanya. CalendarRecap yang akan menentukan apakah itu single atau range.
            // Jika single week, CalendarRecap harusnya mengembalikan weekEnd == weekStart atau weekEnd == 0 (jika Anda mendefinisikannya begitu).
            if (weekStart != 0) {
                intent.putExtra("currentWeekStart", weekStart);
                intent.putExtra("currentWeekEnd", weekEnd);
            }
            calendarRecapLauncher.launch(intent);
        });

        createButton.setOnClickListener(v -> {
            Log.d("RecapFragment", "Create button clicked.");

            boolean isYearSelected = (selectedYear != 0);
            boolean isMonthSelected = (selectedMonth != -1);
            // Kunci perubahan di sini:
            // isWeekSelected true jika weekStart memiliki nilai (baik itu single week atau range)
            boolean isWeekSelected = (weekStart != 0);

            if (!isYearSelected && !isMonthSelected && !isWeekSelected) {
                if (getContext() != null) {
                    Toast toast = Toast.makeText(getContext(), "Please choose your memorable date first!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50); // MUNCUL DARI ATAS KE BAWAH
                    toast.show();
                }
                Log.d("RecapFragment", "Validation failed: No date selected.");
                return;
            }

            showLoading();

            if (boatImage != null) {
                floatAnimation = new TranslateAnimation(0, 0, 0, -30);
                floatAnimation.setDuration(1000);
                floatAnimation.setRepeatMode(TranslateAnimation.REVERSE);
                floatAnimation.setRepeatCount(TranslateAnimation.INFINITE);
                boatImage.startAnimation(floatAnimation);
            }

            new Handler().postDelayed(() -> {
                Log.d("RecapFragment", "Post-delay handler executed. Hiding loading and showing recap cards.");
                if (floatAnimation != null) {
                    boatImage.clearAnimation();
                }
                hideLoading();
                showRecapCards();
                if (getActivity() instanceof MainActivity) {
                    Log.d("RecapFragment", "Calling hideBottomNav() on MainActivity from create button handler.");
                    ((MainActivity) requireActivity()).hideBottomNav();
                } else {
                    Log.e("RecapFragment", "Activity is not MainActivity when trying to hide bottom nav.");
                }
            }, 5000);
        });

        backButtonRecapCards.setOnClickListener(v -> {
            Log.d("RecapFragment", "Tombol Back Recap Cards diklik!");
            resetToInitialState();
        });

        return rootView;
    }

    public void resetToInitialState() {
        Log.d("RecapFragment", "resetToInitialState() called.");
        selectedYear = 0;
        selectedMonth = -1;
        weekStart = 0;
        weekEnd = 0; // Reset weekEnd juga

        if (yearButton != null) yearButton.setText("Year");
        if (monthButton != null) monthButton.setText("Month");
        if (weekButton != null) weekButton.setText("Week");

        showHintState();

        if (getActivity() instanceof MainActivity) {
            Log.d("RecapFragment", "Calling showBottomNav() on MainActivity from resetToInitialState.");
            ((MainActivity) requireActivity()).showBottomNav();
        } else {
            Log.e("RecapFragment", "Activity is not MainActivity when trying to show bottom nav.");
        }
    }

    private void showHintState() {
        if (layoutMainContent != null) layoutMainContent.setVisibility(View.VISIBLE);
        if (hintLayout != null) hintLayout.setVisibility(View.VISIBLE);
        if (arrowHint != null) arrowHint.setVisibility(View.VISIBLE);
        if (textHint != null) {
            textHint.setVisibility(View.VISIBLE);
            textHint.setText("Choose your memorable date!");
        }

        if (loadingScreenLayout != null) loadingScreenLayout.setVisibility(View.GONE);
        if (tvLoadingQuote != null) tvLoadingQuote.setVisibility(View.GONE); // Menggunakan tvLoadingQuote
        if (boatImage != null) boatImage.setVisibility(View.GONE);

        if (layoutRecap != null) layoutRecap.setVisibility(View.GONE);
        if (viewPagerRecap != null) viewPagerRecap.setVisibility(View.GONE);
        if (linearLayoutIndicator != null) linearLayoutIndicator.setVisibility(View.GONE);
        if (backButtonRecapCards != null) backButtonRecapCards.setVisibility(View.GONE);
    }

    private void showLoading() {
        if (loadingScreenLayout != null) loadingScreenLayout.setVisibility(View.VISIBLE);
        if (tvLoadingQuote != null) { // Menggunakan tvLoadingQuote
            tvLoadingQuote.setVisibility(View.VISIBLE);
        }
        if (boatImage != null) boatImage.setVisibility(View.VISIBLE);

        // layout_main_content TETAP TERLIHAT, jadi baris ini tidak dihapus.
        // if (layoutMainContent != null) layoutMainContent.setVisibility(View.GONE);

        if (hintLayout != null) hintLayout.setVisibility(View.GONE); // Pastikan hintLayout tetap disembunyikan

        // Pastikan layout Recap Cards juga tersembunyi
        if (layoutRecap != null) layoutRecap.setVisibility(View.GONE);
        if (backButtonRecapCards != null) backButtonRecapCards.setVisibility(View.GONE);
        if (viewPagerRecap != null) viewPagerRecap.setVisibility(View.GONE);
        if (linearLayoutIndicator != null) linearLayoutIndicator.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingScreenLayout != null) loadingScreenLayout.setVisibility(View.GONE);
        if (tvLoadingQuote != null) tvLoadingQuote.setVisibility(View.GONE); // Menggunakan tvLoadingQuote
        if (boatImage != null) boatImage.setVisibility(View.GONE);
    }

    private void showRecapCards() {
        if (layoutRecap != null) {
            layoutRecap.setVisibility(View.VISIBLE);
            if (viewPagerRecap != null) viewPagerRecap.setVisibility(View.VISIBLE);
            if (linearLayoutIndicator != null) linearLayoutIndicator.setVisibility(View.VISIBLE);
            if (backButtonRecapCards != null) backButtonRecapCards.setVisibility(View.VISIBLE);
        }

        if (layoutMainContent != null) layoutMainContent.setVisibility(View.GONE); // Sembunyikan main content saat recap cards muncul
        if (hintLayout != null) hintLayout.setVisibility(View.GONE);
        if (loadingScreenLayout != null) loadingScreenLayout.setVisibility(View.GONE);

        setupViewPager();
    }


    private void setupViewPager() {
        RecapCardAdapter adapter = new RecapCardAdapter(requireContext(), imageResources);
        if (viewPagerRecap != null) {
            viewPagerRecap.setAdapter(adapter);
            viewPagerRecap.setPageTransformer(new RecapCardTransform());

            setupIndicators(adapter.getItemCount());
            Log.d("RecapFragment", "setupViewPager: Indicators initialized with " + adapter.getItemCount() + " items.");

            if (pageChangeCallback == null) {
                pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        Log.d("RecapFragment", "onPageSelected: Active page = " + position);
                        updateIndicators(position);
                    }
                };
                viewPagerRecap.registerOnPageChangeCallback(pageChangeCallback);
                Log.d("RecapFragment", "setupViewPager: onPageChangeCallback registered.");
            }

            if (adapter.getItemCount() > 0) {
                updateIndicators(0);
                Log.d("RecapFragment", "setupViewPager: Setting initial indicator to page 0.");
            }
        }
    }

    private void setupIndicators(int count) {
        if (linearLayoutIndicator == null) return;
        linearLayoutIndicator.removeAllViews();
        Log.d("RecapFragment", "setupIndicators: Setting up " + count + " indicators.");

        final float scale = getResources().getDisplayMetrics().density;
        int marginEndPx = (int) (4 * scale + 0.5f);

        for (int i = 0; i < count; i++) {
            View indicator = new View(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    (int) (12 * scale + 0.5f),
                    (int) (4 * scale + 0.5f)
            );
            layoutParams.setMarginEnd(marginEndPx);
            indicator.setBackground(getResources().getDrawable(R.drawable.strip_progress_card, null));
            indicator.setLayoutParams(layoutParams);
            linearLayoutIndicator.addView(indicator);
            Log.d("RecapFragment", "setupIndicators: Added indicator View " + i);
        }
    }

    private void updateIndicators(int currentPosition) {
        if (linearLayoutIndicator == null) return;
        Log.d("RecapFragment", "updateIndicators called for position: " + currentPosition);

        final float scale = getResources().getDisplayMetrics().density;

        for (int i = 0; i < linearLayoutIndicator.getChildCount(); i++) {
            View indicator = linearLayoutIndicator.getChildAt(i);
            boolean isSelected = (i == currentPosition);

            indicator.setSelected(isSelected);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicator.getLayoutParams();
            if (isSelected) {
                params.width = (int) (24 * scale + 0.5f);
                params.height = (int) (6 * scale + 0.5f);
            } else {
                params.width = (int) (12 * scale + 0.5f);
                params.height = (int) (4 * scale + 0.5f);
            }
            indicator.setLayoutParams(params);

            Log.d("RecapFragment", "Indicator " + i + ", setSelected(" + isSelected + "), size: " + params.width + "x" + params.height);
        }
    }

    private void updateDateButtons(String type, int year, int month, int weekStart, int weekEnd) {
        this.selectedYear = year;
        this.selectedMonth = month;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd; // Pastikan weekEnd juga di-update

        // Update Year Button
        if (yearButton != null) {
            if (this.selectedYear != 0) {
                yearButton.setText(String.valueOf(this.selectedYear));
            } else {
                yearButton.setText("Year");
            }
        }

        // Update Month Button
        if (monthButton != null) {
            if (this.selectedMonth != -1) {
                String monthName = getMonthName(this.selectedMonth);
                monthButton.setText(monthName);
            } else {
                monthButton.setText("Month");
            }
        }

        // Update Week Button - Kunci perubahan di sini untuk single week
        if (weekButton != null) {
            if (this.weekStart != 0) { // Jika weekStart ada, berarti ada pilihan minggu
                if (this.weekEnd != 0 && this.weekStart != this.weekEnd) { // Jika range (weekEnd berbeda dari 0 dan weekStart)
                    weekButton.setText(this.weekStart + "-" + this.weekEnd);
                } else { // Ini berarti single week (weekEnd adalah 0 atau sama dengan weekStart)
                    weekButton.setText(String.valueOf(this.weekStart));
                }
            } else { // Belum ada minggu yang dipilih
                weekButton.setText("Week");
            }
        }

        // Logika untuk menampilkan/menyembunyikan hint
        // Validasi di sini juga diubah untuk mengizinkan weekStart saja
        if (selectedYear != 0 || selectedMonth != -1 || (this.weekStart != 0)) {
            if (textHint != null) {
                textHint.setText("Click 'Create!' button!");
                arrowHint.setVisibility(View.GONE);
            }
        } else {
            if (textHint != null) {
                textHint.setText("Choose your memorable date!");
                arrowHint.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getMonthName(int monthIndex) {
        String[] months = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        if (monthIndex >= 0 && monthIndex < months.length) {
            return months[monthIndex];
        }
        return "Unknown";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewPagerRecap != null && pageChangeCallback != null) {
            viewPagerRecap.unregisterOnPageChangeCallback(pageChangeCallback);
            Log.d("RecapFragment", "onDestroyView: onPageChangeCallback unregistered.");
        }
        // Pastikan tidak ada referensi ke rootView setelah onDestroyView
        rootView = null;
    }
}