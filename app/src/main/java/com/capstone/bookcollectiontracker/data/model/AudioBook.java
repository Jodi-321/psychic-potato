package com.capstone.bookcollectiontracker.data.model;

public class AudioBook extends BaseBook {
    private String durationInMinutes;

    public AudioBook(String title, String author, String isbn, String genre, String publicationDate, boolean isRead, String notes, String durationInMinutes) {
        super(title, author, isbn, genre, publicationDate, isRead, notes);
        this.durationInMinutes = durationInMinutes;
    }

    public String getDurationInMinutes() {
        return durationInMinutes;
    }
    public void setDurationInMinutes(String durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public String getFormatDetails() {
        return "Duration: " + durationInMinutes + " minutes";
    }

}
