package com.ua.bookcatalog.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {

    @Insert
    long insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books ORDER BY title ASC")
    List<Book> getAllBooksByTitle();

    @Query("SELECT * FROM books ORDER BY year DESC")
    List<Book> getAllBooksByYear();

    @Query("SELECT * FROM books ORDER BY rating DESC")
    List<Book> getAllBooksByRating();

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY title ASC")
    List<Book> searchBooks(String query);

    @Query("SELECT COUNT(*) FROM books")
    int getBookCount();

    @Query("SELECT COALESCE(SUM(pages), 0) FROM books")
    int getTotalPages();

    @Query("SELECT * FROM books WHERE id = :id")
    Book getBookById(int id);

    @Query("DELETE FROM books")
    void deleteAll();
}
