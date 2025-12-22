package com.library.service;

import com.library.model.Book;
import com.library.model.Book.BookStatus;
import com.library.repository.BookRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book create(@Valid Book book) {
        if (book.getStatus() == null) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("ISBN already exists: " + book.getIsbn());
        }
        return bookRepository.save(book);
    }

    public Book getById(@NotBlank String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    public List<Book> search(@NotBlank String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    public Book update(@NotBlank String id, @Valid Book update) {
        Book existing = getById(id);
        if (update.getTitle() != null && !update.getTitle().isBlank()) {
            existing.setTitle(update.getTitle());
        }
        if (update.getAuthor() != null && !update.getAuthor().isBlank()) {
            existing.setAuthor(update.getAuthor());
        }
        if (update.getIsbn() != null && !update.getIsbn().isBlank() && !update.getIsbn().equals(existing.getIsbn())) {
            if (bookRepository.existsByIsbn(update.getIsbn())) {
                throw new IllegalArgumentException("ISBN already exists: " + update.getIsbn());
            }
            existing.setIsbn(update.getIsbn());
        }
        if (update.getTags() != null) {
            existing.setTags(update.getTags());
        }
        if (update.getStatus() != null) {
            existing.setStatus(update.getStatus());
        }
        return bookRepository.save(existing);
    }

    public void delete(@NotBlank String id) {
        Book existing = getById(id);
        bookRepository.delete(existing);
    }

    public Book updateStatus(@NotBlank String id, @NotNull BookStatus status) {
        Book existing = getById(id);
        existing.setStatus(status);
        return bookRepository.save(existing);
    }
}
