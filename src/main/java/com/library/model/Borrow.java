package com.library.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "borrows")
public class Borrow {

    @Id
    private String id;

    @NotBlank(message = "Book ID is required")
    @Indexed
    private String bookId;

    @NotBlank(message = "Member ID is required")
    @Indexed
    private String memberId;

    @NotNull(message = "Borrow date is required")
    private LocalDate borrowDate;

    @NotNull(message = "Due date is required")
    @Indexed
    private LocalDate dueDate;

    private LocalDate returnedDate;

    @NotNull
    private BorrowStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Borrow() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = BorrowStatus.ACTIVE;
    }

    public Borrow(String bookId, String memberId, LocalDate borrowDate, LocalDate dueDate) {
        this();
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
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
        return "Borrow{" +
                "id='" + id + '\'' +
                ", bookId='" + bookId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnedDate=" + returnedDate +
                ", status=" + status +
                '}';
    }

    public enum BorrowStatus {
        ACTIVE,
        RETURNED,
        OVERDUE
    }
}
