package com.capstone.bookcollectiontracker.ui.viewmodels;

import static androidx.core.app.PendingIntentCompat.getActivity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.capstone.bookcollectiontracker.data.dao.UserDao;
import com.capstone.bookcollectiontracker.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {
    private FirebaseAuth auth;
    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<String> errorLiveData;
    private UserDao userDao;
    User currentUser;
    int currentUserId;

    public AuthViewModel(Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        userLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();

        if (auth.getCurrentUser() != null) {
            userLiveData.postValue(auth.getCurrentUser());
        }
    }

    public void register(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        userLiveData.postValue(auth.getCurrentUser());
                    } else {
                        errorLiveData.postValue(task.getException().getMessage());
                    }
                });
    }

    public void login(Context context, String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if(firebaseUser != null) {
                            String userEmail = firebaseUser.getEmail();

                            Executors.newSingleThreadExecutor().execute(() -> {
                                User currentUser = userDao.getUserByEmailSync(userEmail);

                                if (currentUser != null) {
                                    int currentUserId = currentUser.getUserId();
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("userId", currentUserId);
                                    editor.apply();
                                    userLiveData.postValue(firebaseUser);
                                } else {
                                    errorLiveData.postValue("User not found");
                                }
                            });
                        }
                    } else {
                        errorLiveData.postValue(task.getException().getMessage());
                    }

                        /*
                        //Code to set Shared Preference - fix how userId is saved for reg users
                        currentUserId = currentUser.getUserId();
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", currentUserId);
                        editor.apply();
                        userLiveData.postValue(auth.getCurrentUser());
                    } else {
                        errorLiveData.postValue(task.getException().getMessage());
                    }

                         */
                });
    }

    public void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.apply();
        auth.signOut();
        userLiveData.postValue(null);
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
