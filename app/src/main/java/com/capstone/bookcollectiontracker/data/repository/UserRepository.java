package com.capstone.bookcollectiontracker.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.capstone.bookcollectiontracker.data.dao.UserDao;
import com.capstone.bookcollectiontracker.data.model.User;
import com.capstone.bookcollectiontracker.data.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private ExecutorService executorService;

        public UserRepository(Application application) {
            AppDatabase db = AppDatabase.getDatabase(application);
            userDao = db.userDao();
            executorService = Executors.newSingleThreadExecutor();
        }

        public void insert(User user) {
            executorService.execute(() -> userDao.insert(user));
        }
        public void update(User user) {
            executorService.execute(() -> userDao.update(user));
        }
        public void delete(User user) {
            executorService.execute(() -> userDao.delete(user));
        }

        public LiveData<User> getUserById(int id) {
            return userDao.getUserById(id);
        }
        public LiveData<User> getUserByUsername(String username) {
            return userDao.getUserByUsername(username);
        }
        public LiveData<User> getUserByEmail(String email) {
            return userDao.getUserByEmail(email);
        }
        public boolean checkUsernameExists(String username) {
            return userDao.checkUsernameExists(username) > 0;
        }
        public boolean checkEmailExists(String email) {
            return userDao.checkEmailExists(email) > 0;
        }
}
