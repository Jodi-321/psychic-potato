package com.capstone.bookcollectiontracker.ui.fragments;

import android.os.Bundle;
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
import androidx.navigation.Navigation;

import com.capstone.bookcollectiontracker.R;
import com.capstone.bookcollectiontracker.ui.viewmodels.AuthViewModel;

public class RegisterFragment extends Fragment {
    private AuthViewModel authViewModel;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState) {
        super.onViewCreated(view, savedInstancesState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(v -> registerUser());

        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_bookListFragment);
            }
        });

        authViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.register(email,password);

    }
}
