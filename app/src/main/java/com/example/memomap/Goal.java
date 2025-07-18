// app/src/main/java/com/example/memomap/Goal.java
package com.example.memomap; // Pastikan package ini sesuai dengan package proyekmu

public class Goal {
    private String title;
    private String label;
    private boolean isCompleted;

    public Goal(String title, String label, boolean isCompleted) {
        this.title = title;
        this.label = label;
        this.isCompleted = isCompleted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return String.valueOf(label); // Menggunakan String.valueOf() untuk memastikan pengembalian String
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}