package com.library.library_system.controller;

import com.library.library_system.model.Book;
import com.library.library_system.model.BorrowRecord;
import com.library.library_system.model.Member;
import com.library.library_system.repository.BookRepository;
import com.library.library_system.repository.MemberRepository;
import com.library.library_system.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Display borrowing book page
     */
    @GetMapping("/book")
    public String borrowBookPage(Model model) {
        List<Book> books = bookRepository.findAll();
        List<Member> members = memberRepository.findAll();
        
        model.addAttribute("books", books);
        model.addAttribute("members", members);
        return "borrow/borrow-book";
    }

    /**
     * Check if member can borrow
     */
    @PostMapping("/check")
    @ResponseBody
    public BorrowService.BorrowCheckResponse checkCanBorrow(@RequestParam String memberId, @RequestParam String bookId) {
        return borrowService.checkIfCanBorrow(memberId, bookId);
    }

    /**
     * Process borrow request
     */
    @PostMapping("/process")
    public String processBorrow(@RequestParam String memberId, @RequestParam String bookId, RedirectAttributes redirectAttributes) {
        try {
            BorrowRecord record = borrowService.borrowBook(memberId, bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully!");
            return "redirect:/borrow/book";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/borrow/book";
        }
    }

    /**
     * Display return/overdue book page
     */
    @GetMapping("/return")
    public String returnBookPage(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "borrow/return-book";
    }

    /**
     * Get member's current borrowed books
     */
    @GetMapping("/current-borrows/{memberId}")
    @ResponseBody
    public List<BorrowRecord> getMemberCurrentBorrows(@PathVariable String memberId) {
        return borrowService.getMemberCurrentBorrows(memberId);
    }

    /**
     * Process return request
     */
    @PostMapping("/return-process")
    public String processReturn(@RequestParam String borrowRecordId, RedirectAttributes redirectAttributes) {
        try {
            borrowService.returnBook(borrowRecordId);
            // Update member suspension status
            BorrowRecord record = borrowService.getMemberBorrowHistory("").stream()
                    .filter(r -> r.getId().equals(borrowRecordId))
                    .findFirst()
                    .orElse(null);
            if (record != null) {
                borrowService.updateMemberSuspensionStatus(record.getMemberId());
            }
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
            return "redirect:/borrow/return";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/borrow/return";
        }
    }

    /**
     * Get borrow history for a member
     */
    @GetMapping("/history/{memberId}")
    public String getBorrowHistory(@PathVariable String memberId, Model model) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            return "redirect:/borrow/book";
        }

        List<BorrowRecord> borrowHistory = borrowService.getMemberBorrowHistory(memberId);
        model.addAttribute("member", memberOpt.get());
        model.addAttribute("borrowHistory", borrowHistory);
        return "borrow/borrow-history";
    }
}
