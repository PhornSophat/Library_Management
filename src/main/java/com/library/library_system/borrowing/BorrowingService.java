package com.library.library_system.borrowing;

import com.library.library_system.book.BookService;
import com.library.library_system.member.MemberService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookService bookService;
    private final MemberService memberService;

    public BorrowingService(BorrowingRepository borrowingRepository,
            BookService bookService,
            MemberService memberService) {
        this.borrowingRepository = borrowingRepository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    /**
     * Borrow a book for a member
     * Checks:
     * 1. Book availability
     * 2. Member can borrow (within limit)
     * 3. Updates Book available count
     * 4. Updates Member borrowed count
     */
    public Borrowing borrowBook(String memberId, String bookId) throws IllegalArgumentException {
        // Check if member can borrow
        if (!memberService.canMemberBorrowBook(memberId)) {
            throw new IllegalArgumentException("Member has reached borrowing limit");
        }

        // Check if book is available
        if (!bookService.isBookAvailable(bookId)) {
            throw new IllegalArgumentException("Book is not available for borrowing");
        }

        // Create borrowing record with 14-day loan period
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14);
        Borrowing borrowing = new Borrowing(memberId, bookId, borrowDate, dueDate);

        // Save borrowing record
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        // Update book available count
        bookService.decrementAvailableCount(bookId);

        // Update member borrowed count
        memberService.incrementBorrowedBooksCount(memberId);

        return savedBorrowing;
    }

    /**
     * Return a borrowed book
     * Updates:
     * 1. Borrowing record with return date and status
     * 2. Book available count (increment)
     * 3. Member borrowed count (decrement)
     */
    public Borrowing returnBook(String borrowingId) throws IllegalArgumentException {
        Optional<Borrowing> borrowingOpt = borrowingRepository.findById(borrowingId);

        if (borrowingOpt.isEmpty()) {
            throw new IllegalArgumentException("Borrowing record not found");
        }

        Borrowing borrowing = borrowingOpt.get();

        if (!borrowing.getStatus().equals("BORROWED")) {
            throw new IllegalArgumentException("Book is not currently borrowed");
        }

        // Update borrowing record
        borrowing.setReturnDate(LocalDate.now());
        borrowing.setStatus("RETURNED");
        Borrowing returnedBorrowing = borrowingRepository.save(borrowing);

        // Update book available count
        bookService.incrementAvailableCount(borrowing.getBookId());

        // Update member borrowed count
        memberService.decrementBorrowedBooksCount(borrowing.getMemberId());

        return returnedBorrowing;
    }

    /**
     * Get all unreturned books where due date has passed
     * Used by scheduled task to detect overdue books
     */
    public List<Borrowing> findOverdueBooks() {
        return borrowingRepository.findOverdueBooks(LocalDate.now());
    }

    /**
     * Update status of overdue books
     */
    public void updateOverdueStatus(String borrowingId) {
        Optional<Borrowing> borrowingOpt = borrowingRepository.findById(borrowingId);

        if (borrowingOpt.isPresent()) {
            Borrowing borrowing = borrowingOpt.get();
            if ("BORROWED".equals(borrowing.getStatus()) && borrowing.isOverdue()) {
                borrowing.setStatus("OVERDUE");
                borrowingRepository.save(borrowing);
            }
        }
    }

    /**
     * Scheduled task to run daily at midnight and detect overdue books
     * Updates status of all overdue borrowings
     */
    @Scheduled(cron = "0 0 0 * * *") // Runs daily at midnight
    public void detectAndMarkOverdueBooks() {
        List<Borrowing> overdueBooks = findOverdueBooks();

        for (Borrowing borrowing : overdueBooks) {
            borrowing.setStatus("OVERDUE");
            borrowingRepository.save(borrowing);
        }

        if (!overdueBooks.isEmpty()) {
            System.out.println("Overdue detection completed. " + overdueBooks.size() + " books marked as overdue.");
        }
    }

    /**
     * Get borrowing history for a member
     */
    public List<Borrowing> getMemberBorrowingHistory(String memberId) {
        return borrowingRepository.findByMemberId(memberId);
    }

    /**
     * Get borrowing records for a book
     */
    public List<Borrowing> getBookBorrowingHistory(String bookId) {
        return borrowingRepository.findByBookId(bookId);
    }

    /**
     * Get a borrowing record by ID
     */
    public Optional<Borrowing> getBorrowingById(String borrowingId) {
        return borrowingRepository.findById(borrowingId);
    }
}
