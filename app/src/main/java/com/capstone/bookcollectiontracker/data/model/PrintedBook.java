package com.capstone.bookcollectiontracker.data.model;

public class PrintedBook extends BaseBook {
    private String coverType;

    public PrintedBook(String title, String author, String isbn, String genre, String publicationDate, boolean isRead, String notes, String coverType) {
        super(title, author, isbn, genre, publicationDate, isRead, notes);
        this.coverType = coverType;
    }
    public String getCoverType() {
        return coverType;
    }
    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }
    @Override
    public String getFormatDetails() {
        return "Cover Type: " + coverType;
    }

}
