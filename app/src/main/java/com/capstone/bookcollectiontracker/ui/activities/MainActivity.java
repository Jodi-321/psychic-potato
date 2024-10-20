package com.capstone.bookcollectiontracker.ui.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.capstone.bookcollectiontracker.data.dao.BookDao;
import com.capstone.bookcollectiontracker.data.dao.UserDao;
import com.capstone.bookcollectiontracker.data.database.AppDatabase;
import com.capstone.bookcollectiontracker.data.model.Book;
import com.capstone.bookcollectiontracker.data.model.User;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.capstone.bookcollectiontracker.R;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        prepopulateDatabase();

        //Start FB
        FirebaseApp.initializeApp(this);

        //FB Firestore offline
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        //FB crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navHostFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment instanceof NavHostFragment) {
            navController = ((NavHostFragment) navHostFragment).getNavController();
        } else {
            throw new IllegalStateException("Activity does not have a NavController set.");
        }


        //navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);

        /*
        FloatingActionButton fabAddBook = findViewById(R.id.fab_add_book);
        fabAddBook.setOnClickListener(view -> {
            navController.navigate(R.id.action_bookListFragment_to_addEditBookFragment);
        });

         */

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();

    }

    private void prepopulateDatabase() {
        AppDatabase db = AppDatabase.getDatabase(this);


        Executors.newSingleThreadExecutor().execute(() -> {

            try {
                UserDao userDao = db.userDao();
                BookDao bookDao = db.bookDao();
                User guestUser = userDao.getUserByUsernameSync("guest_user");

                if(guestUser == null) {
                    guestUser = new User("guest_user", "guest123", "guest@example.com");
                    long guestUserId = userDao.insert(guestUser);
                    guestUser.setUserId((int) guestUserId);
                    Log.d("MainActivity", "Inserting guest user into the database...");
                    Log.d("MainActivity", "Guest user inserted with ID: " + guestUserId);
                }

                boolean hasBook1 = bookDao.getBookByTitleAndUserId("The Adventures of Amina Al-Sirafi", guestUser.getUserId()) != null;
                boolean hasBook2 = bookDao.getBookByTitleAndUserId("I'm Not Done With You Yet", guestUser.getUserId()) != null;


                if (guestUser != null) {
                    Log.d("MainActivity", "Guest user already has the prepopulated books");



                    if (hasBook1 && hasBook2) {
                        Log.d("MainActivity", "Guest user already has the prepopulated books");
                        return;
                    } else {
                        Log.d("MainActivity", "Adding missing books for guest user.");
                    }
                } else {
                    guestUser = new User("guest_user", "guest123", "guest@example.com");
                    long guestUserId = userDao.insert(guestUser);
                    guestUser.setUserId((int) guestUserId);
                    //Log.d("MainActivity", "Inserting guest user into the database...");
                    Log.d("MainActivity", "Guest user inserted with ID: " + guestUserId);
                }

                if (!hasBook1) {
                    Book book1 = new Book(guestUser.getUserId(), "The Adventures of Amina Al-Sirafi", "Shannon Charkraborty", "9798212203753", "Fantasy Fiction", "2023", true, "", "AudioBook", "10");
                    long book1Id = bookDao.insert(book1);
                    book1.setId((int) book1Id);
                    //bookDao.insert(book1);
                    Log.d("MainActivity", "Inserted book with ID: " + book1.getId() + " for user Id: " + guestUser.getUserId());
                }
                if (!hasBook2) {
                    Book book2 = new Book(guestUser.getUserId(), "I'm Not Done With You Yet", "Jesse Q. Sutanto", "9780008558789", "Thriller", "2023", true, "", "AudioBook", "11");
                    long book2Id = bookDao.insert(book2);
                    book2.setId((int) book2Id);
                    //bookDao.insert(book2);
                    Log.d("MainActivity", "Inserted book with ID: " + book2.getId() + " for user Id: " + guestUser.getUserId());

                    //book1.setId((int) book1Id);
                }


                Log.d("MainActivity", "Books inserted for guest user.");
            } catch (Exception e) {
                Log.e("MainActivity", "Error during database population", e);
            }
        });
    }
}