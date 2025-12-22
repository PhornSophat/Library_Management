package com.library.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "books")
public class Book {
    
    @Id
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Indexed(unique = true)
    private String isbn;
    
    @NotNull(message = "Status is required")
    private BookStatus status;
    
    private List<String> tags;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Book() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = BookStatus.AVAILABLE;
    }
    
    public Book(String title, String author, String isbn) {
        this();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", status=" + status +
                ", tags=" + tags +
                '}';
    }
    
    // Enum for Book Status
    public enum BookStatus {
        AVAILABLE,
        BORROWED,
        RESERVED,
        MAINTENANCE
    }
}
