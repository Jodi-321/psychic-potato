package com.capstone.bookcollectiontracker.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.capstone.bookcollectiontracker.data.dao.BookDao;
import com.capstone.bookcollectiontracker.data.model.BaseBook;
import com.capstone.bookcollectiontracker.data.model.Book;
import com.capstone.bookcollectiontracker.data.database.AppDatabase;
import com.capstone.bookcollectiontracker.util.InputSanitizer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookRepository {
    private BookDao bookDao;
    private ExecutorService executorService;
    private FirebaseFirestore firestore;
    private MutableLiveData<String> errorLiveData;

    public BookRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        bookDao = db.bookDao();
        executorService = Executors.newSingleThreadExecutor();
        firestore = FirebaseFirestore.getInstance();
        errorLiveData = new MutableLiveData<>();
    }

    public LiveData<List<BaseBook>> getAllBaseBooks() {
        return Transformations.map(bookDao.getAllBooks(), entities -> {
            List<BaseBook> baseBooks = new ArrayList<>();
            for (Book entity : entities) {
                baseBooks.add(BookConverter.fromEntity(entity));
            }
            return baseBooks;
        });
    }
    public void insertBook(BaseBook baseBook, int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {;

            Book bookEntity = BookConverter.toEntity(baseBook, userId, -1);
            long generatedId = bookDao.insert(bookEntity);

            bookEntity.setBookId((int) generatedId);
            //bookId = bookDao.insert(bookEntity);
            //bookEntity.setBookId(bookId);

            //bookEntity.setUserId(userId);
            bookDao.update(bookEntity);
            //bookDao.insert(BookConverter.toEntity(baseBook, userId));
        });
    }
    public void updateBook(BaseBook baseBook, int userId, int bookId) {
        Executors.newSingleThreadExecutor().execute(() -> {;
            bookDao.update(BookConverter.toEntity(baseBook, userId, bookId));
        });
    }
    public void deleteBook(BaseBook baseBook, int userId, int bookId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            //bookId = baseBook.getBookId();
            Book bookEntity = BookConverter.toEntity(baseBook, userId, bookId);
            bookEntity.setBookId(bookId);

            Log.d("BookRepository", "Attempting Delete of bookId: " + bookId);
            bookDao.delete(bookEntity);
            Log.d("BookRepository", "Delete of bookId: " + bookId + " successful");
        });
    }

    public void insert(Book book) {
        executorService.execute(() -> {
            long roomId = bookDao.insert(book);
            book.setId((int) roomId);
            book.setRoomId(roomId);

            String firestoreId = firestore.collection("books").document().getId();
            book.setFirestoreId(firestoreId);

            firestore.collection("books").document(firestoreId)
                    .set(book)
                    .addOnSuccessListener(aVoid -> {
                        bookDao.update(book);
                    })
                    .addOnFailureListener(e -> {
                        errorLiveData.postValue("Failed to sync book to cloud: " + e.getMessage());

                        book.setNeedSync(true);
                        bookDao.update(book);
                    });
    });

    }
    public void update(Book book){
        executorService.execute(() -> {
            bookDao.update(book);
        });
        /*
        executorService.execute(() -> {
            bookDao.update(book);

            firestore.collection("books").document(book.getFirestoreId()).set(book)
                    .addOnFailureListener(e -> {
                        errorLiveData.postValue("Failed to update book in cloud: " + e.getMessage());
                        book.setNeedSync(true);
                        bookDao.update(book);
                    });
        });

         */
    }
    public void delete(Book book) {
        executorService.execute(() -> {
            bookDao.delete(book);
        });
        /*
        if (book.getFirestoreId() == null || book.getFirestoreId().isEmpty()) {
            Log.e("BookRepository", "Cannot delete book: Document ID is null or empty.");
            return;
        }

        CollectionReference booksRef = firestore.collection("books");
        booksRef.document(book.getFirestoreId())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("BookRepository", "Book succesfully deleted"))
                .addOnFailureListener(e -> Log.e("BookRepository", "Error deleting book", e));

        executorService.execute(() -> bookDao.delete(book));

        executorService.execute(() -> {
            bookDao.delete(book);

            firestore.collection("books").document(book.getFirestoreId()).delete()
                    .addOnFailureListener(e -> {
                        errorLiveData.postValue("Failed to delete book from cloud: " + e.getMessage());
                    });
        });

         */
    }

    public LiveData<Book> getBookById(int id) {
        return bookDao.getBookById(id);
    }
    public LiveData<List<Book>> getAllBooksForUser(int userId){
        return bookDao.getAllBooksForUser(userId);
    }
    public LiveData<List<Book>> searchBooks(int userId, String searchQuery) {
        searchQuery = InputSanitizer.sanitizeInput(searchQuery);
        return bookDao.searchBooks(userId, "%" + searchQuery + "%");
    }
    public LiveData<List<Book>> getBooksByGenre(int userId, String genre) {
        return bookDao.getBooksByGenre(userId, genre);
    }
    public LiveData<List<Book>> getBooksByReadStatus(int userId, boolean isRead){
        return bookDao.getBooksByReadStatus(userId, isRead);
    }

    public void syncWithFireStore() {
        firestore.collection("books").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Book> books = queryDocumentSnapshots.toObjects(Book.class);
            executorService.execute(() -> {
                for (Book book : books) {
                    bookDao.insert(book);
                }
            });
        }).addOnFailureListener(e -> {
            errorLiveData.postValue("Failed to sync books from cloud: " + e.getMessage());
        });
    }

    public void syncBooks() {
        executorService.execute(() -> {
            List<Book> booksToSync = bookDao.getBooksNeedingSync();
            for (Book book : booksToSync) {
                firestore.collection("books").document(book.getFirestoreId())
                        .set(book)
                        .addOnSuccessListener(aVoid -> {
                            book.setNeedSync(false);
                            bookDao.update(book);
                        })
                        .addOnFailureListener(e -> {
                            errorLiveData.postValue("Failed to sync book to cloud: " + e.getMessage());
                        });
            }
        });
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<List<Book>> getAllBooks() {
        return bookDao.getAllBooks();
    }

    public LiveData<List<Book>> getBooksSortedByTitle(int userId) {
        return bookDao.getBooksSortedByTitle(userId);
    }
    public LiveData<List<Book>> getBooksSortedByAuthor(int userId) {
        return bookDao.getBooksSortedByAuthor(userId);
    }
    public LiveData<List<Book>> getBooksSortedByPublicationDate(int userId) {
        return bookDao.getBooksSortedByPublicationDate(userId);
    }



    public void populateTestData() {
        executorService.execute(() -> {
            List<Book> existingBooks = bookDao.getBooksByTitleAndAuthor("1984", "George Orwell");
            if (existingBooks == null || existingBooks.isEmpty()) {
                bookDao.deleteAllBooks();

                bookDao.insert(new Book(1, "1984", "George Orwell", "9780451524935", "Dystopian", "1949-06-08", true, "Classic novel", "PrintedBook", "Paperback"));
                bookDao.insert(new Book(1, "Brave New World", "Aldous Huxley", "9780060850524", "Science Fiction", "1932-08-31", false, "Dystopian society", "EBook", "Kindle Edition"));
                bookDao.insert(new Book(1, "Fahrenheit 451", "Ray Bradbury", "9781451673319", "Science Fiction", "1953-10-19", true, "Censorship theme", "Ebook", "paperback"));
                bookDao.insert(new Book(1, "To Kill a Mockingbird", "Harper Lee", "9780061120084", "Fiction", "1960-07-11", false, "Racial injustice", "PrintedBook", "Hardcover"));
                bookDao.insert(new Book(1, "The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "Fiction", "1925-04-10", true, "American dream", "PrintedBook", "Paperback"));
            } else {
                Log.d("BookRepository", "Test data already exists. Skipping population.");
            }
        });
    }

    public void deleteTestData() {
        executorService.execute(() -> {
            bookDao.deleteBooksByTitle("1984", "George Orwell");
            bookDao.deleteBooksByTitle("Brave New World", "Aldous Huxley");
            bookDao.deleteBooksByTitle("Fahrenheit 451", "Ray Bradbury");
            bookDao.deleteBooksByTitle("To Kill a Mockingbird", "Harper Lee");
            bookDao.deleteBooksByTitle("The Great Gatsby", "F. Scott Fitzgerald");

            Log.d("BookRepository", "Test data deleted.");
        });
    }
}
