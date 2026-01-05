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
            loan.setStatus("RETURNED");
            loan.setReturnDate(LocalDate.now());
            loanRepository.save(loan);

            bookRepository.findById(loan.getBookId()).ifPresent(book -> {
                book.setStatus("AVAILABLE");
                bookRepository.save(book);
            });
            return loan;
        });
    }

    public List<Loan> getActiveLoansForMember(String memberId) {
        return loanRepository.findByMemberIdAndStatus(memberId, "BORROWED");
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
}
