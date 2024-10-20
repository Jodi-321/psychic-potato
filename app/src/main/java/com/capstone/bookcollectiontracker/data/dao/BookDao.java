package com.capstone.bookcollectiontracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.capstone.bookcollectiontracker.data.model.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Insert
    long insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books WHERE id = :id")
    LiveData<Book> getBookById(int id);

    @Query("SELECT * FROM books WHERE user_id = :userId")
    LiveData<List<Book>> getAllBooksForUser(int userId);

    @Query("SELECT * FROM books WHERE title = :title AND user_id = :userId LIMIT 1")
    Book getBookByTitleAndUserId(String title, int userId);

    @Query("SELECT * FROM books WHERE user_id = :userId AND title LIKE '%' || :searchQuery || '%' OR author LIKE '%' || :searchQuery || '%'")
    LiveData<List<Book>> searchBooks(int userId, String searchQuery);

    @Query("SELECT * FROM books WHERE user_id = :userId AND genre = :genre")
    LiveData<List<Book>> getBooksByGenre(int userId, String genre);

    @Query("SELECT * FROM books WHERE user_id = :userId AND is_read = :isRead")
    LiveData<List<Book>> getBooksByReadStatus(int userId, boolean isRead);

    @Query("SELECT * FROM books")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM books WHERE need_sync = 1")
    List<Book> getBooksNeedingSync();

    @Query("SELECT * FROM books WHERE user_id = :userId ORDER BY title ASC")
    LiveData<List<Book>> getBooksSortedByTitle(int userId);

    @Query("SELECT * FROM books WHERE user_id = :userId ORDER BY author ASC")
    LiveData<List<Book>> getBooksSortedByAuthor(int userId);

    @Query("SELECT * FROM books WHERE user_id = :userId ORDER BY publication_date ASC")
    LiveData<List<Book>> getBooksSortedByPublicationDate(int userId);

    @Query("SELECT * FROM books WHERE title = :title AND author = :author")
    List<Book> getBooksByTitleAndAuthor(String title, String author);

    @Query("DELETE FROM books")
    void deleteAllBooks();

    @Query("DELETE FROM books WHERE title = :title AND author = :author")
    void deleteBooksByTitle(String title, String author);
}
