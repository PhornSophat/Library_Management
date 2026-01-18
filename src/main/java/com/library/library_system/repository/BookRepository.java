package com.library.library_system.repository;

import com.library.library_system.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    // Used for Pie Chart counts: "BORROWED", "AVAILABLE", "RETURNED"
    long countByStatus(String status);

    // Used for potential Category charts
    long countByCategory(String category);

    // Get top 5 most borrowed books
    List<Book> findTop5ByOrderByBorrowCountDesc();

    List<Book> findByStatus(String status);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByCategoryContainingIgnoreCase(String category);

}