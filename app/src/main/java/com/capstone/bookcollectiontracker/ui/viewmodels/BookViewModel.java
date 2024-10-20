package com.capstone.bookcollectiontracker.ui.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.capstone.bookcollectiontracker.data.model.AudioBook;
import com.capstone.bookcollectiontracker.data.model.EBook;
import com.capstone.bookcollectiontracker.data.model.PrintedBook;
import com.capstone.bookcollectiontracker.data.repository.BookConverter;
import com.capstone.bookcollectiontracker.data.repository.BookRepository;
import com.capstone.bookcollectiontracker.data.model.BaseBook;
import com.capstone.bookcollectiontracker.data.model.Book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookViewModel extends AndroidViewModel {
    private BookRepository repository;
    private LiveData<List<BaseBook>> allBaseBooks;
    private LiveData<String> errorLiveData;
    private MutableLiveData<String> sortOrder = new MutableLiveData<>("title");

    public BookViewModel(Application application) {
        super(application);
        repository = new BookRepository(application);
        errorLiveData = repository.getErrorLiveData();

        allBaseBooks = Transformations.switchMap(sortOrder, order ->
                Transformations.map(repository.getAllBaseBooks(), baseBookList -> {
                    switch (order) {
                        case "title":
                            baseBookList.sort(Comparator.comparing(BaseBook::getTitle));
                            break;
                        case "author":
                            baseBookList.sort(Comparator.comparing(BaseBook::getAuthor));
                            break;
                        case "date":
                            baseBookList.sort(Comparator.comparing(BaseBook::getPublicationDate));
                            break;
                    }
                    return baseBookList;
                })
        );

    }
    //public void update(book book){ repository.update(book);}
    //public void delete(book book){ repository.delete(book);}

    public void insert(BaseBook baseBook, int userId){
        repository.insertBook(baseBook, userId);
    }
    public void update(BaseBook baseBook, int userId, int bookId){repository.updateBook(baseBook, userId, bookId);
    }
    public void delete(BaseBook baseBook, int userId, int bookId){repository.deleteBook(baseBook, userId, bookId);
    }

    public LiveData<String> getErrorLiveData() {return errorLiveData;}
    public LiveData<Book> getBookById(int id){
        return repository.getBookById(id);
    }
    public LiveData<List<Book>> getAllBooksForUser(int userId) {
        return Transformations.switchMap(repository.getAllBooksForUser(userId), books ->
                Transformations.map(sortOrder, order -> {
                    switch (order) {
                        case "title":
                            books.sort(Comparator.comparing(Book::getTitle));
                            break;
                        case "author":
                            books.sort(Comparator.comparing(Book::getAuthor));
                            break;
                        case "date":
                            books.sort(Comparator.comparing(Book::getPublicationDate));
                            break;
                    }
                    return books;
                })
        );
    }
    public LiveData<List<Book>> searchBooks(int userId, String searchQuery){
        return repository.searchBooks(userId, searchQuery);
    }
    public LiveData<List<Book>> getBooksByGenre(int userId, String genre){
        return repository.getBooksByGenre(userId, genre);
    }
    public LiveData<List<Book>> getBooksByReadStatus(int userId, boolean isRead){
        return repository.getBooksByReadStatus(userId, isRead);
    }
    public void populateTestData() {
        repository.populateTestData();
    }
    public void deleteTestData() {
        repository.deleteTestData();
    }

    public LiveData<List<Book>> getAllBooks() {
        return Transformations.map(allBaseBooks,baseBooks -> {
            List<Book> bookEntities = new ArrayList<>();
            for (BaseBook baseBook : baseBooks) {
                bookEntities.add(BookConverter.toEntity(baseBook, baseBook.getUserId(), baseBook.getBookId()));
            }
            return bookEntities;
        });
        //return allBaseBooks;
    }

    public void sortBooksByTitle() {
        Log.d("BookViewModel", "Sorting books by title");
        sortOrder.setValue("title");
    }
    public void sortBooksByAuthor() {
        Log.d("BookViewModel", "Sorting books by author");
        sortOrder.setValue("author");

    }
    public void sortBooksByDate() {
        Log.d("BookViewModel", "Sorting books by date");
        sortOrder.setValue("date");
    }

    public LiveData<List<Book>> getBooksSortedByTitle(int userId){
        return repository.getBooksSortedByTitle(userId);
    }
    public LiveData<List<Book>> getBooksSortedByAuthor(int userId) {
        return repository.getBooksSortedByAuthor(userId);
    }
    public LiveData<List<Book>> getBooksSortedByPublicationDate(int userId) {
        return repository.getBooksSortedByPublicationDate(userId);
    }
    public LiveData<List<BaseBook>> getBooksByType(String bookType) {
        return Transformations.map(allBaseBooks, baseBooks -> {
            List<BaseBook> filteredBooks = new ArrayList<>();
            for (BaseBook book : baseBooks) {
                if (bookType.equals("EBook") && book instanceof EBook) {
                    filteredBooks.add(book);
                } else if (bookType.equals("PrintedBook") && book instanceof PrintedBook) {
                    filteredBooks.add(book);
                } else if (bookType.equals("AudioBook") && book instanceof AudioBook) {
                    filteredBooks.add(book);
                }
            }
            return filteredBooks;
        });
    }
}
