package com.capstone.bookcollectiontracker.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.capstone.bookcollectiontracker.R;
import com.capstone.bookcollectiontracker.data.model.BaseBook;
import com.capstone.bookcollectiontracker.data.repository.BookConverter;
import com.capstone.bookcollectiontracker.ui.viewmodels.BookViewModel;
import com.google.android.material.button.MaterialButton;

public class BookDetailsFragment extends Fragment {
    private BookViewModel bookViewModel;
    private BaseBook currentBook;
    private int bookId;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textViewTitle = view.findViewById(R.id.text_view_title);
        TextView textViewAuthor = view.findViewById(R.id.text_view_author);
        MaterialButton buttonEdit = view.findViewById(R.id.button_edit);
        MaterialButton buttonDelete = view.findViewById(R.id.button_delete);

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);



        if(getArguments() != null) {
            bookId = getArguments().getInt("bookId", -1);
            //userId = getArguments().getInt("userId", -1);
        } else {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
            bookId = sharedPreferences.getInt("bookId", -1);
            //userId = sharedPreferences.getInt("userId", -1);
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
            //int bookId = getArguments().getInt("bookId");
            bookViewModel.getBookById(bookId).observe(getViewLifecycleOwner(), book -> {
                if (book != null) {
                    //SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("bookId", bookId);
                    editor.putInt("userId", userId);
                    editor.apply();

                    currentBook = BookConverter.fromEntity(book);


                    //currentBook carries null right now
                    //currentBook = book;
                    textViewTitle.setText(book.getTitle());
                    textViewAuthor.setText(book.getAuthor());
                    //add rest of book deatils. dont forget to add TextViews for them
                }

            });


        buttonEdit.setOnClickListener(v -> {
            if (currentBook != null) {
                //int bookId = currentBook.getBookId();
                //int userId = currentBook.getUserId();
                Log.d("BookDetailsFragment", "Attempting to edit book id: " + bookId);

                Bundle bundle = new Bundle();
                bundle.putInt("bookId", bookId);
                bundle.putInt("userId", userId);

                Navigation.findNavController(view).navigate(R.id.action_bookDetailsFragment_to_addEditBookFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Book not found", Toast.LENGTH_SHORT).show();
            }
        });

        buttonDelete.setOnClickListener(v -> {
            if (currentBook != null) {
                //userId =
                //int bookId = currentBook.getBookId();

                Log.d("BookDetailsFragment", "Attempting to delete book id: " + bookId);
                if (bookId != -1) {
                    bookViewModel.delete(currentBook, userId, bookId);
                    Toast.makeText(getContext(), "Book deleted", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigateUp();
                } else {
                    Toast.makeText(getContext(), "Book not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Book not found", Toast.LENGTH_SHORT).show();
            }
            //bookViewModel.delete(currentBook);
            //Navigation.findNavController(view).navigateUp();
        });
    }
}
