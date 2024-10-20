package com.capstone.bookcollectiontracker.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.bookcollectiontracker.data.model.Book;

import java.util.List;

public class ReportViewModelFactory implements ViewModelProvider.Factory{
    private final List<Book> books;

    public ReportViewModelFactory(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReportViewModel.class)) {
            return (T) new ReportViewModel(books);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
