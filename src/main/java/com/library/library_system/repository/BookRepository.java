package com.library.library_system.repository;

import com.library.library_system.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    Optional<Book> findByIsbn(String isbn);
    java.util.List<Book> findByTitleContainingIgnoreCase(String title);
}
