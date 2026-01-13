package com.library.library_system.controller;

import com.library.library_system.model.User;
import com.library.library_system.service.UserService;
import com.library.library_system.service.LoanService;
import com.library.library_system.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @GetMapping("/home")
    public String memberHome(Authentication authentication, Model model) {
        String email = authentication.getName();
        
        return userService.findByEmail(email)
            .map(member -> {
                model.addAttribute("member", member);
                // Only get active (BORROWED) loans for the member portal
                model.addAttribute("loans", loanService.getActiveLoansForMember(member.getId()));
                model.addAttribute("loanCount", loanService.getActiveLoansForMember(member.getId()).size());
                // Add available books for quick browsing
                model.addAttribute("availableBooks", bookService.getAvailableBooks());
                model.addAttribute("totalAvailableBooks", bookService.getAvailableBooks().size());
                return "layout/member_home";
            })
            .orElse("redirect:/login");
    }

    @GetMapping("/library")
    public String memberLibrary(Authentication authentication,
                                @RequestParam(required = false) String search,
                                @RequestParam(required = false) String category,
                                Model model) {
        String email = authentication.getName();
        
        return userService.findByEmail(email)
            .map(member -> {
                model.addAttribute("member", member);
                
                // Get books based on search/filter
                if (search != null && !search.trim().isEmpty()) {
                    model.addAttribute("books", bookService.searchBooks(search));
                    model.addAttribute("searchQuery", search);
                } else {
                    model.addAttribute("books", bookService.getAvailableBooks());
                }
                
                model.addAttribute("totalBooks", bookService.getTotalBooks());
                model.addAttribute("availableCount", bookService.getAvailableBooks().size());
                return "layout/member_library";
            })
            .orElse("redirect:/login");
    }

    @GetMapping("/book/{id}")
    public String viewBookDetail(@PathVariable String id,
                                 Authentication authentication,
                                 Model model) {
        String email = authentication.getName();
        
        return userService.findByEmail(email)
            .flatMap(member -> {
                return bookService.getBookById(id)
                    .map(book -> {
                        model.addAttribute("member", member);
                        model.addAttribute("book", book);
                        
                        // Check if member has an active loan for this book
                        loanService.getActiveLoanForBook(id).ifPresent(loan -> {
                            if (loan.getMemberId().equals(member.getId())) {
                                model.addAttribute("activeLoan", loan);
                            }
                        });
                        
                        return "layout/member_book_detail";
                    });
            })
            .orElse("redirect:/member/library");
    }

    @PostMapping("/book/{id}/borrow")
    public String borrowBook(@PathVariable String id,
                            Authentication authentication,
                            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dueDate,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        
        return userService.findByEmail(email)
            .flatMap(member -> {
                // Set default due date if not provided (2 weeks from now)
                java.time.LocalDate borrowDueDate = dueDate != null ? dueDate : java.time.LocalDate.now().plusWeeks(2);
                
                return loanService.borrowBook(id, member.getId(), borrowDueDate)
                    .map(loan -> {
                        redirectAttributes.addFlashAttribute("successMessage", 
                            "Successfully borrowed '" + loan.getBookTitle() + "'. Due date: " + loan.getDueDate());
                        return "redirect:/member/book/" + id;
                    });
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot borrow book. Please try again.");
                return "redirect:/member/library";
            });
    }

    @GetMapping("/borrow")
    public String memberBorrow(Authentication authentication, Model model) {
        String email = authentication.getName();
        
        return userService.findByEmail(email)
            .map(member -> {
                model.addAttribute("member", member);
                model.addAttribute("availableBooks", bookService.getAvailableBooks());
                model.addAttribute("activeLoans", loanService.getActiveLoansForMember(member.getId()));
                model.addAttribute("loanCount", loanService.getActiveLoansForMember(member.getId()).size());
                return "borrow/BorrowBook";
            })
            .orElse("redirect:/login");
    }

    @GetMapping("/return")
    public String memberReturn(Authentication authentication, Model model) {
        String email = authentication.getName();
        
        return userService.findByEmail(email)
            .map(member -> {
                model.addAttribute("member", member);
                model.addAttribute("memberLoans", loanService.getActiveLoansForMember(member.getId()));
                model.addAttribute("loanCount", loanService.getActiveLoansForMember(member.getId()).size());
                return "borrow/ReturnBook";
            })
            .orElse("redirect:/login");
    }

    @PostMapping("/borrow/submit")
    public String submitBorrow(@RequestParam String bookId,
                              Authentication authentication,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        
        return userService.findByEmail(email)
            .flatMap(member -> {
                java.time.LocalDate dueDate = java.time.LocalDate.now().plusWeeks(2);
                return loanService.borrowBook(bookId, member.getId(), dueDate)
                    .map(loan -> {
                        redirectAttributes.addFlashAttribute("successMessage", 
                            "Successfully borrowed '" + loan.getBookTitle() + "'. Due date: " + loan.getDueDate());
                        return "redirect:/member/borrow";
                    });
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot borrow book. Please try again.");
                return "redirect:/member/borrow";
            });
    }

    @PostMapping("/return/submit")
    public String submitReturn(@RequestParam String loanId,
                              Authentication authentication,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        return loanService.returnLoan(loanId)
            .map(loan -> {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Successfully returned '" + loan.getBookTitle() + "'.");
                return "redirect:/member/return";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot return book. Please try again.");
                return "redirect:/member/return";
            });
    }
}
