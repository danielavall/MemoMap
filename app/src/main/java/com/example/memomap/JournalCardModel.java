package com.example.memomap;

public class JournalCardModel {
    private String day;
    private String date;
    private String month;
    private String description;
    private int photoCount;
    private int videoCount;
    private int backgroundColor;

    public JournalCardModel(String day, String date, String month, String description, int photoCount, int videoCount, int backgroundColor) {
        this.day = day;
        this.date = date;
        this.month = month;
        this.description = description;
        this.photoCount = photoCount;
        this.videoCount = videoCount;
        this.backgroundColor = backgroundColor;
    }

    public String getDay() { return day; }

    public String getDate() { return date; }

    public String getMonth() { return month; }

    public String getDescription() { return description; }

    public int getPhotoCount() { return photoCount; }

    public int getVideoCount() { return videoCount; }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
