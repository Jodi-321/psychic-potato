package com.capstone.bookcollectiontracker.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.bookcollectiontracker.R;
import com.capstone.bookcollectiontracker.data.model.Book;
import com.capstone.bookcollectiontracker.ui.viewmodels.AuthViewModel;
import com.capstone.bookcollectiontracker.ui.viewmodels.BookViewModel;
import com.capstone.bookcollectiontracker.ui.adapters.BookListAdapter;
import com.capstone.bookcollectiontracker.ui.viewmodels.ReportViewModel;
import com.capstone.bookcollectiontracker.ui.viewmodels.ReportViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class BookListFragment extends Fragment {
    private BookViewModel bookViewModel;
    private AuthViewModel authViewModel;
    private BookListAdapter adapter;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private FloatingActionButton fabAddBook;
    private ReportViewModel reportViewModel;

    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_books);
        textViewEmpty = view.findViewById(R.id.text_view_empty);
        fabAddBook = view.findViewById(R.id.fab_add_book);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new BookListAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        Bundle arguments = getArguments();

        if(arguments != null && arguments.containsKey("userId")) {
            userId = getArguments().getInt("userId", -1);

            Log.d("BookListFragment", "Loading books for user ID: " + userId);

            bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
            authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

            bookViewModel.getAllBooksForUser(userId).observe(getViewLifecycleOwner(), books -> {
                Log.d("BookListFragment", "Books loaded: " + books.size());
                ReportViewModelFactory factory = new ReportViewModelFactory(books);
                reportViewModel = new ViewModelProvider(this, new ReportViewModelFactory(books)).get(ReportViewModel.class);
                //adapter.submitList(books);
                updateBookList(books);
            });


            setupMenu();

        } else {
            Log.e("BookListFragment","Arguments bundle or UserId not found");
            textViewEmpty.setText("User ID not found.");
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        //bookViewModel.deleteTestData(); //comment this out when not testing
        //bookViewModel.populateTestData(); //comment this out when not testing


        fabAddBook.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            //int bookId = -1;
            bundle.putInt("bookId", -1);
            bundle.putInt("userId", userId);
            Navigation.findNavController(v).navigate(R.id.action_bookListFragment_to_addEditBookFragment);
        });

        adapter.setOnItemClickListener(book -> {
            int bookId = book.getId();
            Log.d("BookListFragment", "Book ID: " + bookId);
            Log.d("BookListFragment", "User ID: " + userId);

            Bundle bundle = new Bundle();
            bundle.putInt("bookId", book.getId());
            bundle.putInt("userId", userId);
            Navigation.findNavController(view).navigate(R.id.action_bookListFragment_to_bookDetailsFragment, bundle);
        });



    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_book_list,menu);

                MenuItem searchItem = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) searchItem.getActionView();

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        bookViewModel.searchBooks(userId, s).observe(getViewLifecycleOwner(), books -> {
                            updateBookList(books);
                        });
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        bookViewModel.searchBooks(userId, s).observe(getViewLifecycleOwner(), books -> {
                            updateBookList(books);
                        });
                        return true;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                 if (itemId == R.id.action_logout) {
                     authViewModel.logout(requireContext());
                     Navigation.findNavController(requireView()).navigate(R.id.action_bookListFragment_to_loginFragment);
                     return true;
                 } else if (itemId == R.id.action_search) {
                     return true;
                 } else if (itemId == R.id.sort_title) {
                     bookViewModel.sortBooksByTitle();
                     refreshList();
                     return true;
                 } else if (itemId == R.id.sort_author) {
                    bookViewModel.sortBooksByAuthor();
                     refreshList();
                    return true;
                 } else if (itemId == R.id.sort_date) {
                    bookViewModel.sortBooksByDate();
                     refreshList();
                    return true;
                 } else if (itemId == R.id.action_generate_report) {
                     Log.d("BookListFragment", "Generate Report button clicked");
                     //reportViewModel.generateReport();

                     Bundle bundle = new Bundle();
                     bundle.putInt("userId", userId);
                     Navigation.findNavController(requireView()).navigate(R.id.action_bookListFragment_to_reportGenerationFragment, bundle);


                     /*
                     reportViewModel.getReportPreview().observe(getViewLifecycleOwner(), report -> {
                         if (report != null && !report.isEmpty()) {
                             new AlertDialog.Builder(requireContext())
                                     .setTitle("Report Preview")
                                     .setMessage(report)
                                     .setPositiveButton("OK", null)
                                     .show();
                         } else {
                             Log.d("BookListFragment", "Report preview is empty");
                         }
                     });

                      */
                     return true;
                 } else {
                    return false;
                }
                 //return super.onOptionsItemSelected(menuItem);

            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    /*

    private void sortBooksByTitle() {
        bookViewModel.sortBooksByTitle().observe(getViewLifecycleOwner(), books -> {
            updateBookList(books);
        });
    }
    private void sortBooksByAuthor() {
        bookViewModel.sortBooksByAuthor().observe(getViewLifecycleOwner(), books -> {
            updateBookList(books);
        });
    }
    private void sortBooksByDate() {
        bookViewModel.sortBooksByDate().observe(getViewLifecycleOwner(), books -> {
            updateBookList(books);
        });
    }

     */

    private void updateBookList(List<Book> books) {
        adapter.submitList(books);
        if(books.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }
    private void refreshList() {
        bookViewModel.getAllBooks().observe(getViewLifecycleOwner(), books -> {
            Log.d("BookListFragment", "Books refreshed: " + books.size());
            adapter.submitList(null);
            adapter.submitList(books);
        });
    }


}
