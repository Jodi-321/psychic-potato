package com.capstone.bookcollectiontracker.util;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FirebaseErrorHandler {

    public static void handleAuthError(Context context, Exception e) {
        String errorMessage;
        if (e instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "No account associated with this email.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Invalid password or email.";
        } else if (e instanceof FirebaseNetworkException) {
            errorMessage = "Network error. Please check connection";
        } else {
            errorMessage = "Authentication failed. Try again";
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }

    public static void handleFirestoreError(Context context, FirebaseFirestoreException e) {
        String errorMessage;
        switch (e.getCode()) {
            case PERMISSION_DENIED:
                errorMessage = "You don't have permission to perform this action.";
                break;
            case UNAVAILABLE:
                errorMessage = "The service is currently unavilable. Try again later.";
                break;
            default:
                errorMessage = "An eeor occurred. Pelase try again.";
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }
}
