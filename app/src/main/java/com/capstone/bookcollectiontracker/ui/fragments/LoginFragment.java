package com.capstone.bookcollectiontracker.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.capstone.bookcollectiontracker.R;
import com.capstone.bookcollectiontracker.data.dao.BookDao;
import com.capstone.bookcollectiontracker.data.dao.UserDao;
import com.capstone.bookcollectiontracker.data.database.AppDatabase;
import com.capstone.bookcollectiontracker.data.model.User;
import com.capstone.bookcollectiontracker.ui.viewmodels.AuthViewModel;

import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {
    private AuthViewModel authViewModel;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister, btnContinueAsGuest;
    private int userId;

    private UserDao userDao;
    private BookDao bookDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getDatabase(getContext());
        userDao = db.userDao();
        bookDao = db.bookDao();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        btnContinueAsGuest = view.findViewById(R.id.btnContinueAsGuest);


        btnContinueAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueAsGuest();
            }
        });


        buttonLogin.setOnClickListener(v -> loginUser());
        buttonRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));

        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_bookListFragment);
            }
        });

        authViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.login(requireContext(),email,password);
    }

    private void continueAsGuest() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("LoginFragment", "Waiting for prepoulation to complete...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("LoginFragment", "Fetching guest user form database..");
                User guestUser = userDao.getUserByUsernameSync("guest_user");


                //if (guestUser != null) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.d("LoginFragment", "Guest user found: " + guestUser.getUsername());
                        userId = guestUser.getUserId();

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", userId);
                        editor.apply();

                        navigateToBookList(guestUser);
                    });
                } else {
                    Log.d("LoginFragment", "Guest user not found.");
                }

                if (guestUser != null && getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("LoginFragment", "Navigating to book list...");

                            navigateToBookList(guestUser);
                        }
                    });
                }
            }
        });
    }

    private void navigateToBookList(User guestUser) {
        View view = getView();
        //Navigation logic to BookList Fragment
        if (view != null && guestUser != null) {
            NavController navController = Navigation.findNavController(view);

            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.bookListFragment) {
                Bundle bundle = new Bundle();
                bundle.putInt("userId", guestUser.getUserId());
                Log.d("LoginFrgment", "Passing userId: " + guestUser.getUsername());
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_bookListFragment, bundle);
                Log.d("LoginFragment", "Navigation triggered.");
            } else {
                Log.d("LoginFragment", "Navigation not triggered, already on BookListFragment.");
            }
        } else {
            if (view == null) {
                Log.e("LoginFragment", "View is null, cannot navigate");
            }
            if (guestUser == null) {
                Log.e("LoginFragment", "Guest user is null, cannot navigate");
            }
        }

    }
}




