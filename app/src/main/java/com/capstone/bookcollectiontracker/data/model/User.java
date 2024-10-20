package com.capstone.bookcollectiontracker.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "users")
public class User {
   // @PrimaryKey(autoGenerate = true)
    //private int id;
    @PrimaryKey(autoGenerate = true)
    private int userId;

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @NonNull
    @ColumnInfo(name = "password_hash")
    private String passwordHash;

    @NonNull
    @ColumnInfo(name = "email")
    private String email;

    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    //public int getId() {return id;}
    //public void setId(int id) {this.id = id;}

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPasswordHash() {return passwordHash;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
}
