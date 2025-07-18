package com.example.memomap;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CalendarModel { // Nama kelas diubah menjadi CalendarModel
    private String monthName;
    private int year;
    private int firstDayOfMonth; // Day of week (Calendar.SUNDAY, Calendar.MONDAY, etc.)
    private int daysInMonth;
    private List<Integer> selectedDates;
    private List<Integer> highlightedDates;

    private Map<Integer, Integer> coloredDates;


    // Constructor dasar
    public CalendarModel(String monthName, int year, int firstDayOfMonth, int daysInMonth) {
        this(monthName, year, firstDayOfMonth, daysInMonth, Collections.emptyMap());
    }
    // Constructor lengkap
    public CalendarModel(String monthName, int year, int firstDayOfMonth, int daysInMonth,
                         Map<Integer, Integer> coloredDates) {
        this.monthName = monthName;
        this.year = year;
        this.firstDayOfMonth = firstDayOfMonth;
        this.daysInMonth = daysInMonth;
        this.coloredDates = coloredDates;
    }


    // --- Getters ---

    public Map<Integer, Integer> getColoredDates() {
        return coloredDates;
    }

    public String getMonthName() {
        return monthName;
    }

    public int getYear() {
        return year;
    }

    public int getFirstDayOfMonth() {
        return firstDayOfMonth;
    }

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public List<Integer> getSelectedDates() {
        return selectedDates;
    }

    public List<Integer> getHighlightedDates() {
        return highlightedDates;
    }

    // Helper method untuk mendapatkan instance Calendar yang disetel ke hari pertama bulan ini
    public Calendar getCalendarForFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, getMonthNumber(monthName), 1);
        return calendar;
    }

    // Helper method untuk mengonversi nama bulan string ke konstanta integer Calendar.MONTH
    private int getMonthNumber(String monthName) {
        switch (monthName) {
            case "JAN": return Calendar.JANUARY;
            case "FEB": return Calendar.FEBRUARY;
            case "MAR": return Calendar.MARCH;
            case "APR": return Calendar.APRIL;
            case "MAY": return Calendar.MAY;
            case "JUN": return Calendar.JUNE;
            case "JUL": return Calendar.JULY;
            case "AUG": return Calendar.AUGUST;
            case "SEP": return Calendar.SEPTEMBER;
            case "OCT": return Calendar.OCTOBER;
            case "NOV": return Calendar.NOVEMBER;
            case "DEC": return Calendar.DECEMBER;
            default: return -1;
        }
    }
}
