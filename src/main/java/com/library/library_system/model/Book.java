package com.library.library_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Document(collection = "Books")
public class Book {
    @Id
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Status is required")
    private String status; // BORROWED, AVAILABLE, RETURNED
    
    @Min(value = 0, message = "Borrow count cannot be negative")
    private Integer borrowCount; // Track how many times borrowed
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity; // Track total quantity of this book

    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQuantity; // Track copies that can still be borrowed

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

    public Integer getAvailableQuantity() {
        // Default available to total quantity when null
        return availableQuantity == null ? getQuantity() : availableQuantity;
    }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
}