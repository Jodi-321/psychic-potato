package com.capstone.bookcollectiontracker.data.model;

public class EBook extends BaseBook {
    private String fileFormat;

    public EBook(String title, String author, String isbn, String genre, String publicationDate, boolean isRead, String notes, String fileFormat) {
        super(title, author, isbn, genre, publicationDate, isRead, notes);
        this.fileFormat = fileFormat;
    }
    public String getFileFormat() {
        return fileFormat;
    }
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
    @Override
    public String getFormatDetails() {
        return "EBook Format: " + fileFormat;
    }
}
