package com.capstone.bookcollectiontracker.data.database;


import android.os.AsyncTask;
import android.util.Log;

import com.capstone.bookcollectiontracker.data.dao.BookDao;
import com.capstone.bookcollectiontracker.data.dao.UserDao;
import com.capstone.bookcollectiontracker.data.model.Book;
import com.capstone.bookcollectiontracker.data.model.User;

public class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
    private final UserDao userDao;
    private final BookDao bookDao;

    public PopulateDbAsync(AppDatabase db) {
        this.userDao = db.userDao();
        this.bookDao = db.bookDao();
    }

    @Override
    protected Void doInBackground(final Void... params) {
        Log.d("PopulateDbAsync", "Inserting guest user into the database...");
        try {
            User guestUser = new User("guest_user", "guest123", "guest@example.com");
            long guestUserId = userDao.insert(guestUser);
            guestUser.setUserId((int) guestUserId);

            Log.d("PopulateDbAsync", "Guest user inserted with ID: " + guestUserId);

            Book book1 = new Book(guestUser.getUserId(), "The Adventures of Amina Al-Sirafi", "Shannon Charkraborty", "9798212203753", "Fantasy Fiction", "2023", true, "", "AudioBook", "10");
            Book book2 = new Book(guestUser.getUserId(), "I'm Not Done With You Yet", "Jesse Q. Sutanto", "9780008558789", "Thriller", "2023", true, "", "AudioBook", "11");

            long book1Id = bookDao.insert(book1);
            book1.setId((int) book1Id);
            //bookDao.insert(book1);
            Log.d("PopulateDbTask", "Inserted book with ID: " + book1.getId() + " for user Id: " + guestUser.getUserId());

            long book2Id = bookDao.insert(book2);
            book2.setId((int) book2Id);
            //bookDao.insert(book2);
            Log.d("PopulateDbTask", "Inserted book with ID: " + book1.getId() + " for user Id: " + guestUser.getUserId());

            Log.d("PopulateDbAsync", "Books inserted for guest user.");
        } catch (Exception e) {
            Log.e("PopulateDbAsync", "Error during database population", e);
        }

        return null;
    }
}
