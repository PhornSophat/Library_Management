package com.library.library_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Document(collection = "Loans")
public class Loan {
    @Id
    private String id;
    
    @NotBlank(message = "Book ID is required")
    @Indexed
    private String bookId;
    
    @NotBlank(message = "Book title is required")
    private String bookTitle;
    
    private String bookAuthor; // Author of the borrowed book
    
    @NotBlank(message = "Member ID is required")
    @Indexed
    private String memberId;
    
    @NotBlank(message = "Member name is required")
    private String memberName;
    
    @NotNull(message = "Borrow date is required")
    private LocalDate borrowDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    private LocalDate returnDate;
    
    @NotNull(message = "Status is required")
    private String status; // BORROWED, PENDING_RETURN, or RETURNED

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
