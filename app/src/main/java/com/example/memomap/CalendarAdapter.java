package com.example.memomap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memomap.R;
import com.example.memomap.CalendarModel; // Import diubah ke CalendarModel

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> { // Nama kelas diubah menjadi CalendarAdapter

    private final List<CalendarModel> months; // Tipe List diubah menjadi CalendarModel

    public CalendarAdapter(List<CalendarModel> months) { // Parameter constructor diubah menjadi CalendarModel
        this.months = months;
    }

    private Context context;

    // ViewHolder: Memegang referensi ke View dalam setiap item list
    public static class CalendarViewHolder extends RecyclerView.ViewHolder { // Nama kelas diubah menjadi CalendarViewHolder
        public final TextView textViewMonthName;
        public final GridLayout gridLayoutCalendarDays;

        public CalendarViewHolder(View itemView) { // Nama constructor diubah menjadi CalendarViewHolder
            super(itemView);
            textViewMonthName = itemView.findViewById(R.id.textViewMonthName);
            gridLayoutCalendarDays = itemView.findViewById(R.id.gridLayoutCalendarDays);
        }
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // Tipe return dan parameter diubah
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_calendar, parent, false);
        return new CalendarViewHolder(view); // Nama constructor diubah
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) { // Tipe parameter diubah
        CalendarModel calendarModel = months.get(position); // Tipe variabel diubah menjadi CalendarModel

        holder.textViewMonthName.setText(calendarModel.getMonthName());

        holder.gridLayoutCalendarDays.removeAllViews();

        holder.itemView.setOnClickListener(v -> {
            String month = calendarModel.getMonthName();

            if (month.equals("JAN")) {
                Intent intent = new Intent(context, FullMonthActivity.class);
                intent.putExtra("month", "JAN");
                intent.putExtra("year", calendarModel.getYear());

                // Kirim coloredDates ke FullMonthActivity
                if (calendarModel.getColoredDates() != null) {
                    intent.putExtra("coloredDates", new HashMap<>(calendarModel.getColoredDates()));
                }

                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Belum tersedia untuk bulan " + month, Toast.LENGTH_SHORT).show();
            }
        });

        Calendar firstDay = calendarModel.getCalendarForFirstDay();
        int firstDayOfWeek = firstDay != null ? firstDay.get(Calendar.DAY_OF_WEEK) : Calendar.SUNDAY;

        int startOffset = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7;

        for (int i = 0; i < startOffset; i++) {
            TextView emptyTextView = new TextView(holder.itemView.getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyTextView.setLayoutParams(params);
            holder.gridLayoutCalendarDays.addView(emptyTextView);
        }

        for (int day = 1; day <= calendarModel.getDaysInMonth(); day++) {
            TextView dayTextView = new TextView(holder.itemView.getContext());
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);

            int size = dpToPx(holder.itemView.getContext(), 19);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size; // Pastikan square!
            params.setMargins(4, 4, 4, 4); // Optional spacing

            dayTextView.setLayoutParams(params);

            Map<Integer, Integer> coloredDates = calendarModel.getColoredDates();

            if (coloredDates != null && coloredDates.containsKey(day)) {
                int colorResId = coloredDates.get(day);
                dayTextView.setBackgroundResource(R.drawable.bg_circle_generic); // Satu shape drawable saja
                dayTextView.setTextColor(Color.BLACK);
                dayTextView.getBackground().setTint(
                        dayTextView.getContext().getResources().getColor(colorResId)
                );
            } else {
                dayTextView.setBackgroundResource(0);
                dayTextView.setTextColor(Color.BLACK);
            }

            holder.gridLayoutCalendarDays.addView(dayTextView);
        }

    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
}