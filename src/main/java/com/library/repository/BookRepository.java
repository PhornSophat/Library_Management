package com.library.repository;

import com.library.model.Book;
import com.library.model.Book.BookStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    boolean existsByIsbn(String isbn);
    Book findByIsbn(String isbn);
    List<Book> findByStatus(BookStatus status);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
