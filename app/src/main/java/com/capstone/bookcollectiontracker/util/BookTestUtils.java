package com.capstone.bookcollectiontracker.util;

/*
import android.widget.Toast;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.example.com.capstone.bookcollectiontracker.R;
import com.example.com.capstone.bookcollectiontracker.ui.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class BookTestUtils {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Test case for adding a book that has an empy title


    @Test
    public void testAddBookWithEmptyTitle() {
        ActivityScenario.launch(MainActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.fab_add_book)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.button_save)).perform(ViewActions.click());

        Espresso.onView(withText("Title is required"))
                .inRoot(ToastMatcher())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Test case for deleting a book form the list.

    @Test
    public void testDeleteBook() {
        ActivityScenario.launch(MainActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.fab_add_book)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_title)).perform(ViewActions.typeText("Test Book"));
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_author)).perform(ViewActions.typeText("Test Author"));
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_isbn)).perform(ViewActions.typeText("1234567890"));
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_genre)).perform(ViewActions.typeText("Test Genre"));
        Espresso.onView(ViewMatchers.withId(R.id.edit_text_publication_date)).perform(ViewActions.typeText("2022"));

        Espresso.pressBak();

        Espresso.onView(withText("Test Book")).perform(ViewActions.longClick());
        Espresso.onView(withText("Delete")).perform(ViewActions.click());

        Espresso.onView(withText("Test Book")).check(ViewAssertions.doesNotExist());





    }

}

*/
