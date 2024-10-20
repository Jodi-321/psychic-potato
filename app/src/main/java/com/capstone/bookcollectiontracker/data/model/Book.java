package com.capstone.bookcollectiontracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;


@Entity(tableName = "books",
        foreignKeys = @ForeignKey(entity = User.class,
                                    parentColumns = "userId",
                                    childColumns = "user_id",
                                    onDelete = ForeignKey.CASCADE))
public class Book {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "firestore_id")
    private String firestoreId;

    @ColumnInfo(name = "room_id")
    private Long roomId;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "isbn")
    private String isbn;

    @ColumnInfo(name = "genre")
    private String genre;

    @ColumnInfo(name = "publication_date")
    private String publicationDate;

    @ColumnInfo(name = "is_read")
    private boolean isRead;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "need_sync")
    private boolean needSync;

    @ColumnInfo(name = "book_type")
    private String bookType;

    @ColumnInfo(name = "formatDetails")
    private String formatDetails;



    public Book(int userId, String title, String author, String isbn, String genre, String publicationDate, boolean isRead, String notes, String bookType, String formatDetails){
        this.userId = userId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.publicationDate = publicationDate;
        this.isRead = isRead;
        this.notes = notes;
        this.needSync = false;
        this.roomId = null;
        this.bookType = bookType;
        this.formatDetails = formatDetails;

    }

    public String getFirestoreId() {
        return firestoreId;
    }
    public void setFirestoreId(String firestoreId) {this.firestoreId = firestoreId;}

    public Long getRoomId(){
        return roomId;
    }
    public void setRoomId(Long roomId){this.roomId = roomId;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}

    public String getIsbn() {return isbn;}
    public void setIsbn(String isbn) {this.isbn = isbn;}

    public String getGenre() {return genre;}
    public void setGenre(String genre) {this.genre = genre;}

    public String getPublicationDate() {return publicationDate;}
    public void setPublicationDate(String publicationDate) {this.publicationDate = publicationDate;}

    public boolean isRead() {return isRead;}
    public void setRead(boolean read) {this.isRead = isRead;}

    public String getNotes() {return notes;}
    public void setNotes(String notes) {this.notes = notes;}

    public boolean isNeedSync() {return needSync;}
    public void setNeedSync(boolean needSync) {this.needSync = needSync;}

    public String getBookType() {
        return bookType;
    }
    public void setBookType(String bookType) {
        this.bookType = bookType;
    }
    public String getFormatDetails() {
        return formatDetails;
    }
    public void setFormatDetails(String formatDetails) {
        this.formatDetails = formatDetails;
    }

    public int getBookId() {
        return id;
    }
    public void setBookId(int bookId) {
        this.id = bookId;
    }

}
