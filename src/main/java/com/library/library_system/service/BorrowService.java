package com.library.library_system.service;

import com.library.library_system.model.Book;
import com.library.library_system.model.BorrowRecord;
import com.library.library_system.model.Member;
import com.library.library_system.repository.BookRepository;
import com.library.library_system.repository.BorrowRecordRepository;
import com.library.library_system.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Check if a member can borrow a book
     * Rules:
     * 1. Member must not be suspended
     * 2. Member must have returned all previously borrowed books
     * 3. Book must have available copies
     */
    public BorrowCheckResponse checkIfCanBorrow(String memberId, String bookId) {
        BorrowCheckResponse response = new BorrowCheckResponse();

        // Check if member exists
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            response.setCanBorrow(false);
            response.setMessage("Member not found");
            return response;
        }

        Member member = memberOpt.get();

        // Check if member is suspended
        if (member.isSuspended()) {
            response.setCanBorrow(false);
            response.setMessage("Member is suspended due to overdue books");
            return response;
        }

        // Check if book exists
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (!bookOpt.isPresent()) {
            response.setCanBorrow(false);
            response.setMessage("Book not found");
            return response;
        }

        Book book = bookOpt.get();

        // Check if book has available copies
        if (book.getAvailableCopies() <= 0) {
            response.setCanBorrow(false);
            response.setMessage("No available copies of this book");
            return response;
        }

        // Check if member has unreturned books
        List<BorrowRecord> unreturnedBooks = borrowRecordRepository.findByMemberIdAndStatus(memberId, "BORROWED");
        if (!unreturnedBooks.isEmpty()) {
            response.setCanBorrow(false);
            response.setMessage("Member has unreturned books. Please return them first");
            return response;
        }

        // Check if member has overdue books
        List<BorrowRecord> allBorrowRecords = borrowRecordRepository.findByMemberId(memberId);
        boolean hasOverdue = allBorrowRecords.stream().anyMatch(BorrowRecord::isOverdue);
        if (hasOverdue) {
            response.setCanBorrow(false);
            response.setMessage("Member has overdue books. Not allowed to borrow");
            return response;
        }

        response.setCanBorrow(true);
        response.setMessage("Member can borrow this book");
        return response;
    }

    /**
     * Borrow a book for a member
     */
    public BorrowRecord borrowBook(String memberId, String bookId) {
        BorrowCheckResponse checkResponse = checkIfCanBorrow(memberId, bookId);
        if (!checkResponse.isCanBorrow()) {
            throw new IllegalArgumentException(checkResponse.getMessage());
        }

        Book book = bookRepository.findById(bookId).get();
        
        BorrowRecord record = new BorrowRecord();
        record.setMemberId(memberId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(14)); // 14 days borrow period
        record.setStatus("BORROWED");

        // Update book available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    /**
     * Return a borrowed book
     */
    public BorrowRecord returnBook(String borrowRecordId) {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(borrowRecordId);
        if (!recordOpt.isPresent()) {
            throw new IllegalArgumentException("Borrow record not found");
        }

        BorrowRecord record = recordOpt.get();
        if (!"BORROWED".equals(record.getStatus())) {
            throw new IllegalArgumentException("This book is not currently borrowed");
        }

        record.setReturnDate(LocalDateTime.now());
        record.setStatus("RETURNED");

        // Update book available copies
        Book book = bookRepository.findById(record.getBookId()).get();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    /**
     * Get all borrowed records for a member
     */
    public List<BorrowRecord> getMemberBorrowHistory(String memberId) {
        return borrowRecordRepository.findByMemberId(memberId);
    }

    /**
     * Get current borrowed books for a member
     */
    public List<BorrowRecord> getMemberCurrentBorrows(String memberId) {
        return borrowRecordRepository.findByMemberIdAndStatus(memberId, "BORROWED");
    }

    /**
     * Check and update member suspension status based on overdue books
     */
    public void updateMemberSuspensionStatus(String memberId) {
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByMemberId(memberId);
        boolean hasOverdue = borrowRecords.stream().anyMatch(BorrowRecord::isOverdue);

        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setSuspended(hasOverdue);
            memberRepository.save(member);
        }
    }

    /**
     * Auto-update suspension status for all members
     */
    public void updateAllMembersSuspensionStatus() {
        List<Member> allMembers = memberRepository.findAll();
        for (Member member : allMembers) {
            updateMemberSuspensionStatus(member.getId());
        }
    }

    // Inner class for borrow check response
    public static class BorrowCheckResponse {
        private boolean canBorrow;
        private String message;

        public boolean isCanBorrow() {
            return canBorrow;
        }

        public void setCanBorrow(boolean canBorrow) {
            this.canBorrow = canBorrow;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
