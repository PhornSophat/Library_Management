package com.library.library_system.controller;

import com.library.library_system.model.Loan;
import com.library.library_system.service.LoanService;
import com.library.library_system.service.BookService;
import com.library.library_system.service.UserService;
import com.library.library_system.dto.BorrowRequest;
import com.library.library_system.dto.ReturnRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * BorrowController - Handles borrowing and returning books for members
 * 
 * LOGIC:
 * - Member can borrow an available book (status: AVAILABLE)
 * - System automatically sets book status to BORROWED
 * - Member can return a borrowed book (status: BORROWED)
 * - System automatically sets book status to AVAILABLE
 * - Due date defaults to 2 weeks from borrow date
 */
@Controller
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    /**
     * Display borrow page with available books and members
     */
    @GetMapping
    public String showBorrowPage(Model model) {
        model.addAttribute("availableBooks", bookService.getAvailableBooks());
        model.addAttribute("members", userService.getMembers());
        model.addAttribute("activePage", "borrow");
        return "borrow/BorrowBook";
    }

    /**
     * Process book borrowing for member
     * Member selects a book and optionally sets due date
     */
    @PostMapping("/submit")
    public String borrowBook(@Valid @ModelAttribute("borrow") BorrowRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Select a member and a book to borrow.");
            return "redirect:/borrow";
        }
        
        // Consolidated check: Can member borrow?
        if (!loanService.canBorrow(request.getMemberId())) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot borrow: You have already borrowed 5 books (maximum limit). Please return a book before borrowing another.");
            return "redirect:/borrow";
        }
        
        // Borrow logic: Check if book is available and member exists
        return loanService.borrowBook(request.getBookId(), request.getMemberId(), request.getDueDate())
            .map(loan -> {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Successfully borrowed '" + loan.getBookTitle() + "'. Due date: " + loan.getDueDate());
                return "redirect:/borrow";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot borrow: Book unavailable or member not found.");
                return "redirect:/borrow";
            });
    }

    /**
     * Display return page with active borrowings
     */
    @GetMapping("/return")
    public String showReturnPage(Model model) {
        model.addAttribute("activeLoans", loanService.getAllActiveLoans());
        model.addAttribute("activePage", "return");
        model.addAttribute("pendingReturnsCount", loanService.getPendingReturns().size());
        return "borrow/ReturnBook";
    }

    /**
     * Process book return for member
     * Member selects a borrowed book and returns it
     */
    @PostMapping("/return")
    public String returnBook(@Valid @ModelAttribute("returnReq") ReturnRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot return: No loan selected.");
            return "redirect:/borrow/return";
        }
        
        // Return logic: Mark loan as RETURNED and set book status to AVAILABLE
        return loanService.returnLoan(request.getLoanId())
            .map(loan -> {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Successfully returned '" + loan.getBookTitle() + "'.");
                return "redirect:/borrow/return";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot return: Loan not found.");
                return "redirect:/borrow/return";
            });
    }
}
