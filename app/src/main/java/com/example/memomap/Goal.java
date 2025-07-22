// app/src/main/java/com/example/memomap/Goal.java
package com.example.memomap;

import java.util.Objects;

public class Goal {
    private String title;
    private String label;
    private boolean isCompleted;
    private long lastCompletionTimestamp;
    private long id; // Tambahkan ID unik

    // Static counter untuk ID sementara (dalam aplikasi nyata, ini dari database)
    private static long nextId = 0;

    public Goal(String title, String label, boolean isCompleted) {
        this.title = title;
        this.label = label;
        this.isCompleted = isCompleted;
        this.lastCompletionTimestamp = isCompleted ? System.currentTimeMillis() : 0;
        this.id = nextId++; // Beri ID unik
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getLabel() { return label; }
    public boolean isCompleted() { return isCompleted; }
    public long getLastCompletionTimestamp() { return lastCompletionTimestamp; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setLabel(String label) { this.label = label; }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
        if (completed) {
            this.lastCompletionTimestamp = System.currentTimeMillis();
        } else {
            this.lastCompletionTimestamp = 0;
        }
    }
    // Tidak perlu setter untuk lastCompletionTimestamp jika sudah di handle di setCompleted

    // PENTING: equals() dan hashCode() berdasarkan ID unik
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return id == goal.id; // Bandingkan berdasarkan ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash berdasarkan ID
    }

    // Optional: toString() untuk debugging
    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", label='" + label + '\'' +
                ", isCompleted=" + isCompleted +
                ", lastCompletionTimestamp=" + lastCompletionTimestamp +
                '}';
    }
}



//// app/src/main/java/com/example/memomap/Goal.java
//
//package com.example.memomap;
//
//import java.util.Objects; // Import ini
//
//public class Goal {
//    private String title;
//    private String label;
//    private boolean isCompleted;
//    private long lastCompletionTimestamp;
//    private long id; // <<< TAMBAHKAN ID UNIK UNTUK IDENTIFIKASI
//
//    // Static counter for simple unique ID (for demonstration, in real app use database ID)
//    private static long nextId = 0;
//
//    public Goal(String title, String label, boolean isCompleted) {
//        this.title = title;
//        this.label = label;
//        this.isCompleted = isCompleted;
//        this.lastCompletionTimestamp = isCompleted ? System.currentTimeMillis() : 0;
//        this.id = nextId++; // Assign a unique ID
//    }
//
//    public long getId() { // <<< TAMBAHKAN GETTER ID
//        return id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//    public void setLabel(String label) {
//        this.label = label;
//    }
//
//    public boolean isCompleted() {
//        return isCompleted;
//    }
//
//    public void setCompleted(boolean completed) {
//        this.isCompleted = completed;
//        if (completed) {
//            this.lastCompletionTimestamp = System.currentTimeMillis();
//        } else {
//            this.lastCompletionTimestamp = 0;
//        }
//    }
//
//    public long getLastCompletionTimestamp() {
//        return lastCompletionTimestamp;
//    }
//
//    // <<< TAMBAHKAN EQUALS DAN HASHCODE INI >>>
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Goal goal = (Goal) o;
//        return id == goal.id; // Bandingkan berdasarkan ID unik
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id); // Hash berdasarkan ID unik
//    }
//}