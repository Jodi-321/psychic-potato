/*
package com.example.com.capstone.bookcollectiontracker.util;

import android.content.Context;
import android.widget.Toast;

import org.jetbrains.annotations.TestOnly;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class FirebaseErrorHandlerTest {
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
    }

    @Test
    public void handleAuthError_invalidUser_showsCorrectToast() {
        FirebaseErrorHandler.handleAuthError(context, new FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "No user record found"));
        assertEquals("No account found with this email.", ShadowToast.getTextOfLatestToast());
    }
}
*/