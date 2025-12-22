package com.library.service;

import com.library.model.Book;
import com.library.model.Book.BookStatus;
import com.library.model.Borrow;
import com.library.model.Borrow.BorrowStatus;
import com.library.model.Member;
import com.library.model.Member.MemberStatus;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.MemberRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public BorrowService(BorrowRepository borrowRepository,
                         BookRepository bookRepository,
                         MemberRepository memberRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public Borrow borrowBook(@NotBlank String memberId, @NotBlank String bookId, LocalDate borrowDate, LocalDate dueDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("Member is not active: " + memberId);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));

        // Rule: must not have active or overdue borrows
        boolean hasOpen = !borrowRepository.findByMemberIdAndStatus(memberId, BorrowStatus.ACTIVE).isEmpty()
                || !borrowRepository.findByMemberIdAndStatus(memberId, BorrowStatus.OVERDUE).isEmpty();
        if (hasOpen) {
            throw new IllegalStateException("Member has unreturned or overdue books");
        }

        // Rule: book must be available and not already actively borrowed
        if (book.getStatus() != BookStatus.AVAILABLE ||
                borrowRepository.findFirstByBookIdAndStatus(bookId, BorrowStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        LocalDate actualBorrowDate = (borrowDate != null) ? borrowDate : LocalDate.now();
        LocalDate actualDueDate = (dueDate != null) ? dueDate : actualBorrowDate.plusDays(14);

        // Create borrow record
        Borrow borrow = new Borrow(bookId, memberId, actualBorrowDate, actualDueDate);
        borrow.setStatus(BorrowStatus.ACTIVE);
        Borrow saved = borrowRepository.save(borrow);

        // Update book status
        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        return saved;
    }

    public Borrow returnBook(@NotBlank String borrowId, LocalDate returnedDate) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow not found: " + borrowId));

        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalStateException("Borrow already returned");
        }

        // Mark returned
        borrow.setStatus(BorrowStatus.RETURNED);
        borrow.setReturnedDate(returnedDate != null ? returnedDate : LocalDate.now());
        Borrow saved = borrowRepository.save(borrow);

        // Free the book
        Book book = bookRepository.findById(borrow.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found for borrow: " + borrow.getBookId()));
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        return saved;
    }

    public int markOverdues() {
        List<Borrow> toMark = borrowRepository.findByDueDateBeforeAndStatus(LocalDate.now(), BorrowStatus.ACTIVE);
        for (Borrow b : toMark) {
            b.setStatus(BorrowStatus.OVERDUE);
            borrowRepository.save(b);
        }
        return toMark.size();
    }

    public List<Borrow> listActiveByMember(@NotBlank String memberId) {
        return borrowRepository.findByMemberIdAndStatus(memberId, BorrowStatus.ACTIVE);
    }

    public List<Borrow> listOverdue() {
        return borrowRepository.findByDueDateBeforeAndStatus(LocalDate.now(), BorrowStatus.ACTIVE)
                .stream()
                .peek(b -> b.setStatus(BorrowStatus.OVERDUE)) // representation-only; not persisted
                .toList();
    }

    public long countActive() { return borrowRepository.countByStatus(BorrowStatus.ACTIVE); }
    public long countOverdue() { return borrowRepository.countByStatus(BorrowStatus.OVERDUE); }
}
