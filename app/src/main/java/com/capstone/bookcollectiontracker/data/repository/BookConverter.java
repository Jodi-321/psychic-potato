package com.capstone.bookcollectiontracker.data.repository;

import android.util.Log;

import com.capstone.bookcollectiontracker.data.model.*;

public class BookConverter {

    public static BaseBook fromEntity(Book entity) {
        String bookType = entity.getBookType().toLowerCase();

        switch (bookType) {
            case "ebook":
                return new EBook(entity.getTitle(), entity.getAuthor(), entity.getIsbn(), entity.getGenre(), entity.getPublicationDate(), entity.isRead(), entity.getNotes(),entity.getFormatDetails());
            case "printedbook":
                return new PrintedBook(entity.getTitle(), entity.getAuthor(), entity.getIsbn(), entity.getGenre(), entity.getPublicationDate(), entity.isRead(), entity.getNotes(), entity.getFormatDetails());
            case "audiobook":
                String duration = entity.getFormatDetails();
                try{
                        duration = entity.getFormatDetails();
                    //return new AudioBook(entity.getTitle(), entity.getAuthor(), entity.getIsbn(), entity.getGenre(), entity.getPublicationDate(), entity.isRead(), entity.getNotes(), duration);
                } catch (NumberFormatException e) {
                    Log.e("BookConverter", "Invalid duration format: " + entity.getFormatDetails(), e);
                    duration = "0";
                    //throw new IllegalArgumentException("Invalid duration format: " + entity.getFormatDetails(), e);
                }
                    return new AudioBook(entity.getTitle(), entity.getAuthor(), entity.getIsbn(), entity.getGenre(), entity.getPublicationDate(), entity.isRead(), entity.getNotes(), duration);
                default:
                    throw new IllegalArgumentException("Unknown book type: " + entity.getBookType());
        }
    }

    public static Book toEntity(BaseBook baseBook, int userId, int bookId) {
        Book entity = new Book(userId, baseBook.getTitle(), baseBook.getAuthor(), baseBook.getIsbn(), baseBook.getGenre(), baseBook.getPublicationDate(), baseBook.isRead(), baseBook.getNotes(),getBookType(baseBook),getFormatDetails(baseBook));
        if (baseBook instanceof EBook) {
            entity.setBookType("EBook");
            entity.setFormatDetails(((EBook) baseBook).getFileFormat());
            entity.setUserId(userId);
            //entity.setBookId(bookId);
        } else if (baseBook instanceof PrintedBook) {
            entity.setBookType("PrintedBook");
            entity.setFormatDetails(((PrintedBook) baseBook).getCoverType());
            entity.setUserId(userId);
            //entity.setBookId(bookId);
        } else if (baseBook instanceof AudioBook) {
            String duration = ((AudioBook) baseBook).getDurationInMinutes();
            entity.setBookType("AudioBook");
            entity.setFormatDetails(String.valueOf(((AudioBook) baseBook).getDurationInMinutes()));
            entity.setUserId(userId);
            //entity.setBookId(bookId);
        } else {
            throw new IllegalArgumentException("Unknown book type: " + baseBook.getClass().getSimpleName());
        }

        if (baseBook.getBookId() != -1) {
            entity.setBookId(baseBook.getBookId());
        }
        return entity;
    }

    private static String getBookType(BaseBook baseBook) {
        if (baseBook instanceof EBook) {
            return "EBook";
        } else if (baseBook instanceof PrintedBook) {
            return "PrintedBook";
        } else if (baseBook instanceof AudioBook) {
            return "AudioBook";
        } else {
            throw new IllegalArgumentException("Unknown book type: " + baseBook.getClass().getSimpleName());
        }
    }

    private static String getFormatDetails(BaseBook baseBook) {
        if (baseBook instanceof EBook) {
            return ((EBook) baseBook).getFileFormat();
        } else if (baseBook instanceof PrintedBook) {
            return ((PrintedBook) baseBook).getCoverType();
        } else if (baseBook instanceof AudioBook) {
            return String.valueOf(((AudioBook) baseBook).getDurationInMinutes());
        } else {
            throw new IllegalArgumentException("Unknown book type: " + baseBook.getClass().getSimpleName());
        }
    }
}
