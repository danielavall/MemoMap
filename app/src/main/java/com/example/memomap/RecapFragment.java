package com.example.memomap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;

public class RecapFragment extends Fragment {
    private TextView textHint;
    private ImageView arrowHint, boatImage;
    private FrameLayout layoutRecap;
    private ViewPager2 viewPagerRecap;
    private LinearLayout linearLayoutIndicator;

    private ImageButton backButtonRecapCards;

    private Button chooseDateButton;
    private Button yearButton, monthButton, weekButton;
    private Button createButton;

    private TranslateAnimation floatAnimation;

    private View rootView;

    private int selectedYear = 0;
    private int weekStart = 0;
    private int weekEnd = 0;
    private int selectedMonth = -1;

    private TextView title, subtitle;
    private LinearLayout dateButtons; // LinearLayout yang membungkus tombol-tombol

    private TextView tvLoadingQuote;
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

    private ShapeableImageView btnProfile;
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

        chooseDateButton = rootView.findViewById(R.id.chooseDateButton);
        yearButton = rootView.findViewById(R.id.yearButton);
        monthButton = rootView.findViewById(R.id.monthButton);
        weekButton = rootView.findViewById(R.id.weekButton);
        createButton = rootView.findViewById(R.id.createButton);

        boatImage = rootView.findViewById(R.id.boat);
        tvLoadingQuote = rootView.findViewById(R.id.tv_loading_quote);

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
        dateButtons = rootView.findViewById(R.id.dateButtons); // Inisialisasi LinearLayout parent
        layoutMainContent = rootView.findViewById(R.id.layout_main_content);

        if (textHint != null) {
            textHint.setGravity(Gravity.CENTER_HORIZONTAL);
            int paddingHorizontal = (int) (getResources().getDisplayMetrics().density * 24);
            textHint.setPadding(paddingHorizontal, textHint.getPaddingTop(), paddingHorizontal, textHint.getPaddingBottom());
        }

        // Pastikan semua view berhasil ditemukan, ini penting untuk debugging
        if (boatImage == null || textHint == null || arrowHint == null || layoutRecap == null ||
                linearLayoutIndicator == null || backButtonRecapCards == null ||
                tvLoadingQuote == null || loadingScreenLayout == null || layoutMainContent == null ||
                title == null || subtitle == null || dateButtons == null ||
                chooseDateButton == null ||
                yearButton == null || monthButton == null || weekButton == null || createButton == null || hintLayout == null) {
            Log.e("RecapFragment", "View from fragment_recap.xml not found! Check IDs or includes.");
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
                            int weekEnd = data.getIntExtra("weekEnd", 0);

                            boolean dateSelected = data.getBooleanExtra("dateSelected", false);

                            if (dateSelected) {
                                // Jika tanggal dipilih, perbarui tombol dan tampilkan 3 tombol + Create!
                                updateDateButtons(type, year, month, weekStart, weekEnd);
                                showDetailedDateButtons(); // Panggil method untuk menampilkan 3 tombol
                                if (textHint != null) {
                                    textHint.setText("Make sure the date you selected is correct, then \nClick 'Create!' button!");
                                    arrowHint.setVisibility(View.GONE);

                                    // MODIFIED: Turunkan sedikit textHint
                                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textHint.getLayoutParams();
                                    // Menggunakan margin top untuk menurunkan. Sesuaikan nilai 16dp sesuai kebutuhan.
                                    // Anda juga bisa menambahkan padding top jika tidak ingin mempengaruhi layout induk.
                                    params.topMargin = (int) (getResources().getDisplayMetrics().density * 16); // Menambahkan margin top 16dp
                                    textHint.setLayoutParams(params);
                                }
                            } else {
                                Log.d("RecapFragment", "No valid date selected from CalendarRecap, maintaining previous selection state.");
                                // Jika tidak ada tanggal yang dipilih, kembali ke state "Choose Date"
                                resetToInitialState();
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.d("RecapFragment", "CalendarRecap canceled by system back button.");
                        // Jika dibatalkan, kembali ke state "Choose Date"
                        resetToInitialState();
                    }
                }
        );

        // Atur tampilan awal
        resetToInitialState(); // Panggil resetToInitialState() untuk mengatur kondisi awal

        // Set text for the individual date buttons if they are ever visible later
        if (yearButton != null) yearButton.setText("Year");
        if (monthButton != null) monthButton.setText("Month");
        if (weekButton != null) weekButton.setText("Week");

        // Listener untuk tombol Choose Date
        if (chooseDateButton != null) {
            chooseDateButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CalendarRecap.class);
                Calendar currentCal = Calendar.getInstance();
                intent.putExtra("currentYear", currentCal.get(Calendar.YEAR));
                intent.putExtra("currentMonth", currentCal.get(Calendar.MONTH));
                intent.putExtra("currentWeekStart", 0);
                intent.putExtra("currentWeekEnd", 0);
                calendarRecapLauncher.launch(intent);
            });
        }

        // Listener untuk tombol Year, Month, Week (sudah ada, tidak perlu diubah lagi)
        yearButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalendarRecap.class);
            intent.putExtra("type", "year");
            if (selectedYear != 0) intent.putExtra("currentYear", selectedYear);
            if (selectedMonth != -1) intent.putExtra("currentMonth", selectedMonth);
            if (weekStart != 0) intent.putExtra("currentWeekStart", weekStart);
            if (weekEnd != 0) intent.putExtra("currentWeekEnd", weekEnd);
            calendarRecapLauncher.launch(intent);
        });

        monthButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalendarRecap.class);
            intent.putExtra("type", "month");
            if (selectedYear != 0) intent.putExtra("currentYear", selectedYear);
            if (selectedMonth != -1) intent.putExtra("currentMonth", selectedMonth);
            if (weekStart != 0) intent.putExtra("currentWeekStart", weekStart);
            if (weekEnd != 0) intent.putExtra("currentWeekEnd", weekEnd);
            calendarRecapLauncher.launch(intent);
        });

        weekButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalendarRecap.class);
            intent.putExtra("type", "week");
            if (selectedYear != 0) intent.putExtra("currentYear", selectedYear);
            if (selectedMonth != -1) intent.putExtra("currentMonth", selectedMonth);
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
            boolean isWeekSelected = (weekStart != 0);

            // Validasi harus juga mempertimbangkan jika user langsung klik create tanpa choose date
            // Jika tombol Choose Date terlihat, berarti belum ada tanggal yang dipilih secara spesifik
            if (chooseDateButton != null && chooseDateButton.getVisibility() == View.VISIBLE) {
                if (getContext() != null) {
                    Toast toast = Toast.makeText(getContext(), "Please choose your memorable date first!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
                    toast.show();
                }
                Log.d("RecapFragment", "Validation failed: No specific date selected (Choose Date button is visible).");
                return;
            }

            // Validasi ini hanya relevan jika tombol Year/Month/Week sudah terlihat
            if (!isYearSelected && !isMonthSelected && !isWeekSelected) {
                if (getContext() != null) {
                    Toast toast = Toast.makeText(getContext(), "Please choose your memorable date first!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
                    toast.show();
                }
                Log.d("RecapFragment", "Validation failed: No date selected from Year/Month/Week.");
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

        // MODIFIED: Logic for back button from recap cards
        backButtonRecapCards.setOnClickListener(v -> {
            Log.d("RecapFragment", "Tombol Back Recap Cards diklik!");
            // Check current selection state to return to the correct view
            if (selectedYear != 0 || selectedMonth != -1 || (this.weekStart != 0)) {
                // If a date was selected, show the Year/Month/Week buttons
                showDetailedDateButtons();
                updateDateButtons("", selectedYear, selectedMonth, weekStart, weekEnd); // Re-set text for chosen date
                if (textHint != null) {
                    textHint.setText("Make sure the date you selected is correct and Click 'Create!' button!");
                    arrowHint.setVisibility(View.GONE);
                    // Reset margin top when returning to this state
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textHint.getLayoutParams();
                    params.topMargin = (int) (getResources().getDisplayMetrics().density * 16); // Restore the added margin
                    textHint.setLayoutParams(params);
                }
            } else {
                // Otherwise, revert to the initial "Choose Date" state
                showInitialDateButtons();
                if (textHint != null) {
                    textHint.setText("Choose your memorable date!");
                    arrowHint.setVisibility(View.VISIBLE);
                    // Reset margin top to initial state (0 or default from XML)
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textHint.getLayoutParams();
                    params.topMargin = 0; // Reset margin top
                    textHint.setLayoutParams(params);
                }
            }
            showHintState(); // Make sure other UI elements are shown/hidden correctly
            if (getActivity() instanceof MainActivity) {
                Log.d("RecapFragment", "Calling showBottomNav() on MainActivity from back button handler.");
                ((MainActivity) requireActivity()).showBottomNav();
            } else {
                Log.e("RecapFragment", "Activity is not MainActivity when trying to show bottom nav.");
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize and set image for btn_profile
        ShapeableImageView btnProfile = view.findViewById(R.id.profileIcon);

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

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
    }

    // --- Method untuk menampilkan state tombol awal (Choose Date + Create!) ---
    private void showInitialDateButtons() {
        if (dateButtons != null) {
            dateButtons.setWeightSum(4); // Set weightSum ke 4 agar Choose Date bisa mengambil 3 bagian
            dateButtons.setGravity(Gravity.CENTER_HORIZONTAL); // Pastikan gravity tetap center
        }

        if (chooseDateButton != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chooseDateButton.getLayoutParams();
            params.width = 0; // Menggunakan 0dp agar weight berlaku
            params.weight = 3; // Mengambil 3 bagian dari 4
            params.height = (int) (40 * getResources().getDisplayMetrics().density); // Sesuaikan tinggi dengan tombol lain
            // HAPUS ATAU UBAH INI MENJADI 0: params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density)); // Tambahkan marginEnd
            params.setMarginEnd(0); // Menghilangkan margin
            chooseDateButton.setLayoutParams(params);
            chooseDateButton.setVisibility(View.VISIBLE);
        }
        if (yearButton != null) yearButton.setVisibility(View.GONE);
        if (monthButton != null) monthButton.setVisibility(View.GONE);
        if (weekButton != null) weekButton.setVisibility(View.GONE);

        if (createButton != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) createButton.getLayoutParams();
            params.width = 0; // Menggunakan 0dp agar weight berlaku
            params.weight = 1; // Mengambil 1 bagian dari 4
            params.height = (int) (48 * getResources().getDisplayMetrics().density); // Pertahankan tinggi 48dp
            params.setMarginStart(0); // Menghilangkan margin
            createButton.setLayoutParams(params);
            createButton.setVisibility(View.VISIBLE); // Pastikan Create! selalu terlihat
        }
    }

    // --- Method untuk menampilkan state tombol detail (Year + Month + Week + Create!) ---
    private void showDetailedDateButtons() {
        if (dateButtons != null) {
            dateButtons.setWeightSum(4); // Kembali ke weightSum 4
            dateButtons.setGravity(Gravity.CENTER_HORIZONTAL); // Pastikan gravity tetap center
        }

        if (chooseDateButton != null) chooseDateButton.setVisibility(View.GONE);
        if (yearButton != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) yearButton.getLayoutParams();
            params.width = 0;
            params.weight = 1;
            params.height = (int) (40 * getResources().getDisplayMetrics().density);
            params.setMarginEnd(0); // Menghilangkan margin
            yearButton.setLayoutParams(params);
            yearButton.setVisibility(View.VISIBLE);
        }
        if (monthButton != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) monthButton.getLayoutParams();
            params.width = 0;
            params.weight = 1;
            params.height = (int) (40 * getResources().getDisplayMetrics().density);
            params.setMarginStart(0); // Menghilangkan margin
            params.setMarginEnd(0); // Menghilangkan margin
            monthButton.setLayoutParams(params);
            monthButton.setVisibility(View.VISIBLE);
        }
        if (weekButton != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) weekButton.getLayoutParams();
            params.width = 0;
            params.weight = 1;
            params.height = (int) (40 * getResources().getDisplayMetrics().density);
            params.setMarginStart(0); // Menghilangkan margin
            params.setMarginEnd(0); // Menghilangkan margin
            weekButton.setLayoutParams(params);
            weekButton.setVisibility(View.VISIBLE);
        }

        if (createButton != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) createButton.getLayoutParams();
            params.width = 0; // Tetap 0dp
            params.weight = 1; // Tetap 1
            params.height = (int) (48 * getResources().getDisplayMetrics().density); // Pertahankan tinggi 48dp
            params.setMarginStart(0); // Menghilangkan margin
            createButton.setLayoutParams(params);
            createButton.setVisibility(View.VISIBLE); // Pastikan Create! selalu terlihat
        }
    }

    public void resetToInitialState() {
        Log.d("RecapFragment", "resetToInitialState() called.");
        // Note: selectedYear, selectedMonth, weekStart, weekEnd are NOT reset here
        // as they hold the *last chosen date* state which we want to return to.
        // We only reset them if the user explicitly clears the selection.

        // This method is now used for initial setup and for returning from CalendarRecap if cancelled
        // For returning from Recap Cards, we have specific logic in backButtonRecapCards.setOnClickListener

        // Only reset if no date was previously selected, or if we want a hard reset
        if (selectedYear == 0 && selectedMonth == -1 && weekStart == 0) {
            showInitialDateButtons(); // Show 'Choose Date' if no date was picked yet
        } else {
            // If a date was selected, show the detailed buttons
            showDetailedDateButtons();
            updateDateButtons("", selectedYear, selectedMonth, weekStart, weekEnd);
        }

        // Reset teks tombol individu ke defaultnya jika visible
        if (yearButton != null) {
            if (selectedYear == 0) yearButton.setText("Year");
        }
        if (monthButton != null) {
            if (selectedMonth == -1) monthButton.setText("Month");
        }
        if (weekButton != null) {
            if (weekStart == 0) weekButton.setText("Week");
        }

        showHintState();

        // MODIFIED: Reset margin top textHint in resetToInitialState
        if (textHint != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textHint.getLayoutParams();
            params.topMargin = 0; // Reset margin top to its original value (or 0 if not set in XML)
            textHint.setLayoutParams(params);
        }


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
        // Hint visibility for arrow and text are handled in resetToInitialState and updateDateButtons
        // This method only ensures the parent layouts are visible.

        if (loadingScreenLayout != null) loadingScreenLayout.setVisibility(View.GONE);
        if (tvLoadingQuote != null) tvLoadingQuote.setVisibility(View.GONE);
        if (boatImage != null) boatImage.setVisibility(View.GONE);

        if (layoutRecap != null) layoutRecap.setVisibility(View.GONE);
        if (backButtonRecapCards != null) backButtonRecapCards.setVisibility(View.GONE);
        if (viewPagerRecap != null) viewPagerRecap.setVisibility(View.GONE);
        if (linearLayoutIndicator != null) linearLayoutIndicator.setVisibility(View.GONE);
    }

    private void showLoading() {
        if (loadingScreenLayout != null) loadingScreenLayout.setVisibility(View.VISIBLE);
        if (tvLoadingQuote != null) {
            tvLoadingQuote.setVisibility(View.VISIBLE);
        }
        if (boatImage != null) boatImage.setVisibility(View.VISIBLE);

        if (hintLayout != null) hintLayout.setVisibility(View.GONE);

        if (layoutRecap != null) layoutRecap.setVisibility(View.GONE);
        if (backButtonRecapCards != null) backButtonRecapCards.setVisibility(View.GONE);
        if (viewPagerRecap != null) viewPagerRecap.setVisibility(View.GONE);
        if (linearLayoutIndicator != null) linearLayoutIndicator.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingScreenLayout != null) loadingScreenLayout.setVisibility(View.GONE);
        if (tvLoadingQuote != null) tvLoadingQuote.setVisibility(View.GONE);
        if (boatImage != null) boatImage.setVisibility(View.GONE);
    }

    private void showRecapCards() {
        if (layoutRecap != null) {
            layoutRecap.setVisibility(View.VISIBLE);
            if (viewPagerRecap != null) viewPagerRecap.setVisibility(View.VISIBLE);
            if (linearLayoutIndicator != null) linearLayoutIndicator.setVisibility(View.VISIBLE);
            if (backButtonRecapCards != null) backButtonRecapCards.setVisibility(View.VISIBLE);
        }

        if (layoutMainContent != null) layoutMainContent.setVisibility(View.GONE);
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
        this.weekEnd = weekEnd;

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

        // Update Week Button
        if (weekButton != null) {
            if (this.weekStart != 0) {
                if (this.weekEnd != 0 && this.weekStart != this.weekEnd) {
                    weekButton.setText(this.weekStart + "-" + this.weekEnd);
                } else {
                    weekButton.setText(String.valueOf(this.weekStart));
                }
            } else {
                weekButton.setText("Week");
            }
        }

        // Logika untuk menampilkan/menyembunyikan hint (TIDAK BERUBAH)
        if (selectedYear != 0 || selectedMonth != -1 || (this.weekStart != 0)) {
            if (textHint != null) {
                textHint.setText("Make sure the date you selected is correct and Click 'Create!' button!");
                arrowHint.setVisibility(View.GONE);
                // MODIFIED: Turunkan sedikit textHint saat tanggal dipilih
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textHint.getLayoutParams();
                params.topMargin = (int) (getResources().getDisplayMetrics().density * 16); // Menambahkan margin top 16dp
                textHint.setLayoutParams(params);
            }
        } else {
            if (textHint != null) {
                textHint.setText("Choose your memorable date!");
                arrowHint.setVisibility(View.VISIBLE);
                // Reset margin top saat tidak ada tanggal dipilih
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textHint.getLayoutParams();
                params.topMargin = 0; // Reset margin top
                textHint.setLayoutParams(params);
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
            Log.d("RecapFragment", "onDestroyView: ViewPager2 OnPageChangeCallback unregistered.");
        }
    }
}