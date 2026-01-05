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
        return bookRepository.save(book);
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }
        String lowerQuery = query.toLowerCase();
        return bookRepository.findAll().stream()
            .filter(book -> 
                (book.getTitle() != null && book.getTitle().toLowerCase().contains(lowerQuery)) ||
                (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(lowerQuery)) ||
                (book.getCategory() != null && book.getCategory().toLowerCase().contains(lowerQuery))
            )
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Book> filterBooksByStatus(String status) {
        return bookRepository.findAll().stream()
            .filter(book -> status.equals(book.getStatus()))
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return filterBooksByStatus("AVAILABLE");
    }
}
