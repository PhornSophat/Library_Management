package com.library.library_system.service;

import com.library.library_system.model.Loan;
import com.library.library_system.repository.LoanRepository;
import com.library.library_system.repository.BookRepository;
import com.library.library_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Optional<Loan> borrowBook(String bookId, String memberId, LocalDate dueDate) {
        // First check: Can the member borrow? (Consolidated check)
        if (!canBorrow(memberId)) {
            return Optional.empty();
        }
        
        return bookRepository.findById(bookId).flatMap(book -> {
            if (!"AVAILABLE".equals(book.getStatus())) {
                return Optional.empty();
            }
            
            return userRepository.findById(memberId).map(member -> {
                Loan loan = new Loan();
                loan.setBookId(book.getId());
                loan.setBookTitle(book.getTitle());
                loan.setBookAuthor(book.getAuthor());
                loan.setMemberId(member.getId());
                loan.setMemberName(member.getName());
                loan.setBorrowDate(LocalDate.now());
                loan.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusWeeks(2));
                loan.setStatus("BORROWED");

                book.setStatus("BORROWED");
                int current = book.getBorrowCount() == null ? 0 : book.getBorrowCount();
                book.setBorrowCount(current + 1);

                bookRepository.save(book);
                return loanRepository.save(loan);
            });
        });
    }

    public Optional<Loan> returnLoan(String loanId) {
        return loanRepository.findById(loanId).map(loan -> {
            loan.setStatus("PENDING_RETURN");
            loan.setReturnDate(LocalDate.now());
            loanRepository.save(loan);
            // Book remains BORROWED until admin confirms return
            return loan;
        });
    }

    public Optional<Loan> confirmReturn(String loanId) {
        return loanRepository.findById(loanId).map(loan -> {
            if ("PENDING_RETURN".equals(loan.getStatus())) {
                loan.setStatus("RETURNED");
                loanRepository.save(loan);

                bookRepository.findById(loan.getBookId()).ifPresent(book -> {
                    book.setStatus("AVAILABLE");
                    bookRepository.save(book);
                });
            }
            return loan;
        });
    }

    public List<Loan> getPendingReturns() {
        return loanRepository.findByStatus("PENDING_RETURN");
    }

    public List<Loan> getReturnedLoans() {
        return loanRepository.findByStatus("RETURNED");
    }

    public long getTotalReturnedBooksCount() {
        return loanRepository.countByStatus("RETURNED");
    }

    public long getTotalBorrowedBooksCount() {
        return loanRepository.countByStatus("BORROWED");
    }

    public long getTotalActiveLoansCount() {
        return getTotalBorrowedBooksCount();
    }

    public List<Loan> getActiveLoansForMember(String memberId) {
        return loanRepository.findByMemberIdAndStatus(memberId, "BORROWED");
    }

    public int getActiveBorrowCount(String memberId) {
        return loanRepository.findByMemberIdAndStatus(memberId, "BORROWED").size();
    }

    public boolean canBorrow(String memberId) {
        return getActiveBorrowCount(memberId) < 5;
    }

    public List<Loan> getLoansForMember(String memberId) {
        return loanRepository.findByMemberId(memberId).stream()
            .sorted(Comparator.comparing(Loan::getBorrowDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .collect(Collectors.toList());
    }

    public Optional<Loan> getActiveLoanForBook(String bookId) {
        return loanRepository.findByBookIdAndStatus(bookId, "BORROWED").stream().findFirst();
    }

    public List<Loan> getAllActiveLoans() {
        return loanRepository.findByStatus("BORROWED");
    }

    /**
     * Get all overdue loans (due date passed, status still BORROWED)
     */
    public List<Loan> getOverdueLoans() {
        LocalDate today = LocalDate.now();
        List<Loan> activeLoans = getAllActiveLoans();
        
        return activeLoans.stream()
            .filter(loan -> loan.getDueDate() != null && loan.getDueDate().isBefore(today))
            .collect(Collectors.toList());
    }

    /**
     * Get overdue loans for a specific member
     */
    public List<Loan> getOverdueLoansForMember(String memberId) {
        LocalDate today = LocalDate.now();
        List<Loan> memberLoans = getActiveLoansForMember(memberId);
        
        return memberLoans.stream()
            .filter(loan -> loan.getDueDate() != null && loan.getDueDate().isBefore(today))
            .collect(Collectors.toList());
    }

    /**
     * Check if loan is overdue
     */
    public boolean isOverdue(Loan loan) {
        if (loan.getDueDate() == null) {
            return false;
        }
        if (!"BORROWED".equals(loan.getStatus())) {
            return false;
        }
        return loan.getDueDate().isBefore(LocalDate.now());
    }

    /**
     * Get days overdue (0 if not overdue)
     */
    public long getDaysOverdue(Loan loan) {
        if (!isOverdue(loan)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
    }
}
