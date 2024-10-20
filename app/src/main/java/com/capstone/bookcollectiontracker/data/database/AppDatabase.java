package com.capstone.bookcollectiontracker.data.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.capstone.bookcollectiontracker.data.dao.BookDao;
import com.capstone.bookcollectiontracker.data.dao.UserDao;
import com.capstone.bookcollectiontracker.data.model.User;
import com.capstone.bookcollectiontracker.data.model.Book;


@Database(entities = {Book.class, User.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract BookDao bookDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE ==null) {
            synchronized (AppDatabase.class){
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "book_collection_database")
                            //.addCallback(prepopulateCallback)
                            .fallbackToDestructiveMigration() //comment out before finishing
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback prepopulateCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("AppDatabase", "Database created, starting prepopulation...");
            new PopulateDbAsync(INSTANCE).execute();
        }
    };
}


