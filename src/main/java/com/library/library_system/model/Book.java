package com.library.library_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Books")
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private String category;
    private String status; // BORROWED, AVAILABLE, RETURNED
    private Integer borrowCount; // Track how many times borrowed
    private Integer quantity; // Track total quantity of this book

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getBorrowCount() { return borrowCount; }
    public void setBorrowCount(Integer borrowCount) { this.borrowCount = borrowCount; }

    public Integer getQuantity() { 
        return quantity == null ? 1 : quantity; 
    }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}