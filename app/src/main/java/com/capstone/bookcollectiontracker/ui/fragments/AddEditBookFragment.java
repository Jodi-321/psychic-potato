package com.capstone.bookcollectiontracker.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.capstone.bookcollectiontracker.R;
import com.capstone.bookcollectiontracker.data.model.AudioBook;
import com.capstone.bookcollectiontracker.data.model.BaseBook;
import com.capstone.bookcollectiontracker.data.model.Book;
import com.capstone.bookcollectiontracker.data.model.EBook;
import com.capstone.bookcollectiontracker.data.model.PrintedBook;
import com.capstone.bookcollectiontracker.ui.viewmodels.BookViewModel;
import com.capstone.bookcollectiontracker.util.InputSanitizer;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditBookFragment extends Fragment {
    private BookViewModel bookViewModel;
    private TextInputEditText editTextTitle, editTextAuthor, editTextIsbn, editTextGenre, editTextPublicationDate, editTextFormatDetails;
    private Spinner spinnerBookType;
    private Button buttonSave;
    private int bookId = -1;
    private int userId = -1;

    //int userId = getArguments().getInt("userId", -1);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_book, container, false);

        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextAuthor = view.findViewById(R.id.edit_text_author);
        editTextIsbn = view.findViewById(R.id.edit_text_isbn);
        editTextGenre = view.findViewById(R.id.edit_text_genre);
        editTextPublicationDate = view.findViewById(R.id.edit_text_publication_date);
        spinnerBookType = view.findViewById(R.id.spinner_book_type);
        editTextFormatDetails = view.findViewById(R.id.edit_text_format_details);
        buttonSave = view.findViewById(R.id.button_save);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        int bookId = sharedPreferences.getInt("bookId", -1);
        //SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        if (getArguments() != null) {
            bookId = getArguments().getInt("bookId", -1);
        } else {
            bookId = -1;
        }



        if (userId != -1) {
            Log.d("AddEditBookFragment", "User ID: " + userId);
        } else {
            Log.d("AddEditBookFragment", "User ID not found in arguments: " + userId);
        }




        if (bookId != -1) {
            bookViewModel.getBookById(bookId).observe(getViewLifecycleOwner(), book -> {
                if (book != null) {
                    populateFields(book);

                } else {
                    //Log.e("AddEditBookFragment", "Book with ID " + bookId + " not found");
                }
            });
        } else {
            clearFields();
            Log.d("AddEditBookFragment", "Fields cleared.");
        }

        Log.d("AddEditBookFragment", "saveBook about to be called.Book ID: " + bookId + " User ID: " + userId);
        buttonSave.setOnClickListener(v -> saveBook());
    }

    private void populateFields(Book book) {
        if (book != null) {
            editTextTitle.setText(book.getTitle());
            editTextAuthor.setText(book.getAuthor());
            editTextIsbn.setText(book.getIsbn());
            editTextGenre.setText(book.getGenre());
            editTextPublicationDate.setText(book.getPublicationDate());
            editTextFormatDetails.setText(book.getFormatDetails());

            spinnerBookType.setSelection(
                    java.util.Arrays.asList(getResources().getStringArray(R.array.book_type_options))
                            .indexOf(book.getBookType())
            );
        } else {
            Log.e("AddEditBookFragment", "Book with ID " + bookId + " not found");
            Toast.makeText(getContext(), "Book not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextTitle.setText("");
        editTextAuthor.setText("");
        editTextIsbn.setText("");
        editTextGenre.setText("");
        editTextPublicationDate.setText("");
        editTextFormatDetails.setText("");
        spinnerBookType.setSelection(0);
    }

    private void saveBook() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        if (getArguments() != null && getArguments().containsKey("bookId")) {
            bookId = getArguments().getInt("bookId", -1);
        } else {
            bookId = -1;
        }
        //bookId = sharedPreferences.getInt("bookId", -1);


        userId = sharedPreferences.getInt("userId", -1);


        Log.d("AddEditBookFragment", "saveBook called with bookId: " + bookId + " and userId: " + userId);

        String title = editTextTitle.getText().toString().trim();
        String author = editTextAuthor.getText().toString().trim();
        String isbn = editTextIsbn.getText().toString().trim();
        String genre = editTextGenre.getText().toString().trim();
        String publicationDate = editTextPublicationDate.getText().toString().trim();
        String formatDetails = editTextFormatDetails.getText().toString().trim();
        String bookType = spinnerBookType.getSelectedItem().toString();

        if(title.isEmpty()){
            showToast("Title is required");
            editTextTitle.requestFocus();
            return;
        } else if (InputSanitizer.containsSqlKeywords(title)) {
            showToast("Invalid title. Possible SQL injection keywords detected");
            editTextTitle.requestFocus();
            return;
        } else {
            title = InputSanitizer.sanitizeInput(title);
        }


        if(author.isEmpty()){
            showToast("Author is required");
            editTextAuthor.requestFocus();
            return;
        } else if (InputSanitizer.containsSqlKeywords(author)) {
            showToast("Invalid author. Possible SQL injection keywords detected");
            editTextAuthor.requestFocus();
            return;
        }else {
            author = InputSanitizer.sanitizeInput(author);
        }

        if(!InputSanitizer.isValidIsbn(isbn)){
            showToast("Invalid ISBN must be 10 or 13 numerical digits");
            editTextIsbn.requestFocus();
            return;
        }

        if(genre.isEmpty()){
            showToast("Genre is required");
            editTextGenre.requestFocus();
            return;
        } else if (InputSanitizer.containsSqlKeywords(genre)) {
            showToast("Invalid genre. Possible SQL injection keywords detected");
            editTextGenre.requestFocus();
            return;
        }else {
            genre = InputSanitizer.sanitizeInput(genre);
        }

        if(!isValidPublicationDate(publicationDate)){
            showToast("Invalid publication date form (YYYY-MM-DD or YYYY");
            editTextPublicationDate.requestFocus();
            return;
        }

        if (formatDetails.isEmpty()) {
            showToast("format details is required");
            editTextFormatDetails.requestFocus();
            return;
        } else if (InputSanitizer.containsSqlKeywords(formatDetails)) {
            showToast("Invalid format details. Possible SQL injection keywords detected");
            editTextFormatDetails.requestFocus();
            return;
        }else {
            formatDetails = InputSanitizer.sanitizeInput(formatDetails);
        }

        BaseBook baseBook;
        switch (bookType) {
            case "EBook":
                baseBook = new EBook(title, author, isbn, genre, publicationDate, false, "", formatDetails);
                break;
            case "PrintedBook":
                baseBook = new PrintedBook(title, author, isbn, genre, publicationDate, false, "", formatDetails);
                break;
            case "AudioBook":
                baseBook = new AudioBook(title, author, isbn, genre, publicationDate, false, "", formatDetails);
                break;
            default:
                throw new IllegalArgumentException("Invalid book type: " + bookType);
        }
        baseBook.setUserId(userId);
        //baseBook.setBookId(bookId);
        Log.d("Debug", "Setting userId to: " + userId);
        Log.d("Debug", "Setting bookId to: " + bookId);


        //userId in .update(baseBook, 1) 1 can't be used because it will make all saved books 1? check on this
        if (bookId != -1) {
            baseBook.setBookId(bookId);
            bookViewModel.update(baseBook, userId, bookId);
            Log.d("Debug", "Updating book with ID: " + bookId);
        } else {
            //baseBook.setBookId(bookId);
            bookViewModel.insert(baseBook, userId);
            Log.d("Debug", "Inserting new book");
        }

        clearFields();



        Navigation.findNavController(requireView()).navigateUp();

    }
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private boolean isValidPublicationDate(String date) {
        return date.matches("^\\d{4}$") || (date.matches("^\\d{4}-\\d{2}-\\d{2}$") && !isDateInFuture(date));
    }
    private boolean isDateInFuture(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date inputDate = sdf.parse(date);
            //Date parsedDate = dateFormat.parse(date);
            //Date currentDate = new Date();
            return inputDate != null && inputDate.after(new Date());
        } catch (ParseException e) {
            return true;
        }
    }

    private boolean isValidFormatDetails(String details, String bookType) {
        String lowerCaseDetails = details.toLowerCase();

        String[] sqlKeywords = {"select","insert","update","delete","drop","alter","--", ";"};

        for (String keyword : sqlKeywords) {
            if (lowerCaseDetails.contains(keyword)) {
                return false;
            }
        }
        return true;
    }



}
