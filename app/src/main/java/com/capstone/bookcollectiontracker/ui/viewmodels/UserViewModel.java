package com.capstone.bookcollectiontracker.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.capstone.bookcollectiontracker.data.repository.UserRepository;
import com.capstone.bookcollectiontracker.data.model.User;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;

    public UserViewModel(Application application){
        super(application);
        repository = new UserRepository(application);
    }

    public void insert(User user) {
        repository.insert(user);
    }
    public void update(User user) {
        repository.update(user);
    }
    public void delete(User user) {
        repository.delete(user);
    }

    public LiveData<User> getUserById(int id) {
        return repository.getUserById(id);
    }
    public LiveData<User> getUserByUsername(String username) {
        return repository.getUserByUsername(username);
    }
    public LiveData<User> getUserbyEmail(String email) {
        return repository.getUserByEmail(email);
    }
    public boolean checkUsernameExists(String username) {
        return repository.checkUsernameExists(username);
    }
    public boolean checkEmailExists(String email) {
        return repository.checkEmailExists(email);
    }
}
