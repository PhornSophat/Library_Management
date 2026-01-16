package com.library.library_system.service;

import com.library.library_system.model.Book;
import com.library.library_system.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public long getCountByStatus(String status) {
        return bookRepository.countByStatus(status);
    }

    public long getTotalBooks() {
        return bookRepository.count();
    }

    public long getTotalAvailableBooks() {
        return bookRepository.countByStatus("AVAILABLE");
    }

    public List<Book> getTopBorrowedBooks() {
        return bookRepository.findTop5ByOrderByBorrowCountDesc();
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Increment borrow count and set status to BORROWED
    public java.util.Optional<Book> incrementBorrowCount(String id) {
        return bookRepository.findById(id).map(book -> {
            int current = book.getBorrowCount() == null ? 0 : book.getBorrowCount();
            book.setBorrowCount(current + 1);
            book.setStatus("BORROWED");
            return bookRepository.save(book);
        });
    }

    public java.util.Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        // Initialize available quantity to total when not provided
        if (book.getAvailableQuantity() == null) {
            book.setAvailableQuantity(book.getQuantity());
        }
        // Derive status from available copies
        book.setStatus(book.getAvailableQuantity() > 0 ? "AVAILABLE" : "BORROWED");
        return bookRepository.save(book);
    }

    public Book updateBook(Book book) {
        // Keep availableQuantity in range of total quantity
        if (book.getAvailableQuantity() == null) {
            book.setAvailableQuantity(book.getQuantity());
        }
        if (book.getAvailableQuantity() > book.getQuantity()) {
            book.setAvailableQuantity(book.getQuantity());
        }
        book.setStatus(book.getAvailableQuantity() > 0 ? "AVAILABLE" : "BORROWED");
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }
        return java.util.stream.Stream.of(
                bookRepository.findByTitleContainingIgnoreCase(query),
                bookRepository.findByAuthorContainingIgnoreCase(query),
                bookRepository.findByCategoryContainingIgnoreCase(query)
            )
            .flatMap(java.util.Collection::stream)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Book> filterBooksByStatus(String status) {
        return bookRepository.findByStatus(status);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByStatus("AVAILABLE");
    }
    public List<Book> getPendingReturnedBooks() {
        return filterBooksByStatus("PENDING_RETURNED");
    }

    /**
     * Decrement available copies when a borrow occurs. Returns saved book or empty if none available.
     */
    public java.util.Optional<Book> consumeOneCopy(String bookId) {
        return bookRepository.findById(bookId).map(book -> {
            int available = book.getAvailableQuantity();
            if (available <= 0) {
                throw new IllegalStateException("No copies available");
            }
            book.setAvailableQuantity(available - 1);
            int current = book.getBorrowCount() == null ? 0 : book.getBorrowCount();
            book.setBorrowCount(current + 1);
            book.setStatus(book.getAvailableQuantity() > 0 ? "AVAILABLE" : "BORROWED");
            return bookRepository.save(book);
        });
    }

    /**
     * Increment available copies when a return is confirmed.
     */
    public java.util.Optional<Book> releaseOneCopy(String bookId) {
        return bookRepository.findById(bookId).map(book -> {
            int available = book.getAvailableQuantity();
            int total = book.getQuantity();
            int newAvailable = Math.min(total, available + 1);
            book.setAvailableQuantity(newAvailable);
            book.setStatus(newAvailable > 0 ? "AVAILABLE" : "BORROWED");
            return bookRepository.save(book);
        });
    }
}
