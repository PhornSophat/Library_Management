package com.library.library_system.controller;

import com.library.library_system.model.User;
import com.library.library_system.model.Book;
import com.library.library_system.service.UserService;
import com.library.library_system.service.BookService;
import com.library.library_system.service.LoanService;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @GetMapping("/")
    public String dashboardHome(Model model) {
        // 1. nav & Context Info
        model.addAttribute("userName", "Nisal Gunasekara");
        model.addAttribute("userRole", "Admin");
        model.addAttribute("activePage", "dashboard");

        // 2. Summary Stats (Using BookService)
        model.addAttribute("totalBooks", bookService.getTotalBooks());
        model.addAttribute("borrowedBooks", bookService.getCountByStatus("BORROWED"));
        model.addAttribute("returnedBooks", bookService.getCountByStatus("RETURNED"));
        model.addAttribute("notBorrowed", bookService.getCountByStatus("AVAILABLE"));
        
        // 3. User Stats (Using UserService)
        model.addAttribute("totalUsers", userService.getTotalMemberCount());

        // 4. Admin Component Data
        model.addAttribute("adminData", userService.getAdmins());
        model.addAttribute("adminTitle", "BookWorm Admins");

        // 5. Member Table Data
        List<User> recentMembers = userService.getRecentMembers();
        model.addAttribute("recentMembers", recentMembers);     

        // 6. Overdue Section (Action Required)
        model.addAttribute("overdueList", userService.getOverdueMembers());
        model.addAttribute("overdueTitle", "Overdue Borrowers");

        // 7. Recent Activities
        List<java.util.Map<String, String>> activities = new java.util.ArrayList<>();
        List<User> last5Members = userService.getLast5Members();
        for (User member : last5Members) {
            java.util.Map<String, String> activity = new java.util.HashMap<>();
            activity.put("type", "member");
            activity.put("description", "New member registered: " + member.getName());
            activity.put("timestamp", "Recently added");
            activities.add(activity);
        }
        model.addAttribute("recentActivities", activities);

        // 8. Top Borrowed Books
        model.addAttribute("topBooks", bookService.getTopBorrowedBooks());

        return "layout/dashboard";
    }

    @GetMapping("/members")
    public String memberDisplay(@RequestParam(required = false) String search,
                                @RequestParam(required = false) String status,
                                Model model) {
        model.addAttribute("userName", "Nisal Gunasekara");
        model.addAttribute("activePage", "members");
        
        List<User> membersList;
        
        // Apply search if present
        if (search != null && !search.trim().isEmpty()) {
            membersList = userService.searchMembers(search);
            model.addAttribute("searchQuery", search);
        }
        // Apply status filter if present
        else if (status != null && !status.isEmpty()) {
            try {
                User.Status filterStatus = User.Status.valueOf(status);
                membersList = userService.filterMembersByStatus(filterStatus);
                model.addAttribute("filterStatus", status);
            } catch (IllegalArgumentException e) {
                membersList = userService.getMembers();
            }
        }
        // Default: show all members
        else {
            membersList = userService.getMembers();
        }
        
        model.addAttribute("membersList", membersList);
        
        // Add statistics
        model.addAttribute("totalMembers", userService.getTotalMemberCount());
        model.addAttribute("activeMembers", userService.getActiveMemberCount());
        model.addAttribute("inactiveMembers", userService.getInactiveMemberCount());

        return "layout/member_dash"; 
    }

    @GetMapping("/members/{id}")
    public String memberDetail(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        return userService.getUserById(id)
            .map(user -> {
                model.addAttribute("userName", "Nisal Gunasekara");
                model.addAttribute("activePage", "members");
                model.addAttribute("member", user);
                model.addAttribute("loans", loanService.getLoansForMember(id));
                return "layout/member_detail";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Member not found");
                return "redirect:/members";
            });
    }

    @GetMapping("/members/add")
    public String showAddMemberForm(Model model) {
        model.addAttribute("userName", "Nisal Gunasekara");
        model.addAttribute("activePage", "members");
        model.addAttribute("user", new User());
        return "dashboard/add_member";
    }

    @PostMapping("/members/add")
    public String addMember(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(required = false) String phone,
                           RedirectAttributes redirectAttributes) {
        try {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole(User.Role.MEMBER);
            newUser.setStatus(User.Status.Active);
            
            userService.createUser(newUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "Member added successfully!");
            return "redirect:/members";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add member: " + e.getMessage());
            return "redirect:/members/add";
        }
    }

    @PostMapping("/members/{id}/update")
    public String updateMember(@PathVariable("id") String id,
                               @RequestParam String name,
                               @RequestParam String email,
                               @RequestParam(required = false) String password,
                               @RequestParam User.Role role,
                               @RequestParam User.Status status,
                               RedirectAttributes redirectAttributes) {
        return userService.getUserById(id)
            .map(user -> {
                user.setName(name);
                user.setEmail(email);
                user.setRole(role);
                user.setStatus(status);
                if (password != null && !password.isBlank()) {
                    user.setPassword(password);
                }
                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully");
                return "redirect:/members/" + id;
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Member not found");
                return "redirect:/members";
            });
    }

    @PostMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Member deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete member: " + e.getMessage());
        }
        return "redirect:/members";
    }

    // ===== BOOKS SECTION =====
    
    @GetMapping("/books")
    public String booksDisplay(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String status,
                              Model model) {
        model.addAttribute("userName", "Nisal Gunasekara");
        model.addAttribute("activePage", "books");
        
        List<Book> booksList;
        
        // Apply search if present
        if (search != null && !search.trim().isEmpty()) {
            booksList = bookService.searchBooks(search);
            model.addAttribute("searchQuery", search);
        }
        // Apply status filter if present
        else if (status != null && !status.isEmpty()) {
            booksList = bookService.filterBooksByStatus(status);
            model.addAttribute("filterStatus", status);
        }
        // Default: show all books
        else {
            booksList = bookService.getAllBooks();
        }
        
        model.addAttribute("booksList", booksList);
        
        // Add statistics
        model.addAttribute("totalBooks", bookService.getTotalBooks());
        model.addAttribute("availableBooks", bookService.getCountByStatus("AVAILABLE"));
        model.addAttribute("borrowedBooksCount", bookService.getCountByStatus("BORROWED"));

        return "layout/books_dash";
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("userName", "Nisal Gunasekara");
        model.addAttribute("activePage", "books");
        model.addAttribute("book", new Book());
        return "dashboard/add_book";
    }

    @PostMapping("/books/add")
    public String addBook(@RequestParam String title,
                         @RequestParam String author,
                         @RequestParam String category,
                         RedirectAttributes redirectAttributes) {
        try {
            Book newBook = new Book();
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setCategory(category);
            newBook.setStatus("AVAILABLE");
            newBook.setBorrowCount(0);
            
            bookService.createBook(newBook);
            
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
            return "redirect:/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add book: " + e.getMessage());
            return "redirect:/books/add";
        }
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        return bookService.getBookById(id)
            .map(book -> {
                model.addAttribute("userName", "Nisal Gunasekara");
                model.addAttribute("activePage", "books");
                model.addAttribute("book", book);
                model.addAttribute("members", userService.getMembers());
                model.addAttribute("activeLoan", loanService.getActiveLoanForBook(id).orElse(null));
                return "layout/book_detail";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
                return "redirect:/books";
            });
    }

    @PostMapping("/books/{id}/update")
    public String updateBook(@PathVariable("id") String id,
                            @RequestParam String title,
                            @RequestParam String author,
                            @RequestParam String category,
                            @RequestParam String status,
                            RedirectAttributes redirectAttributes) {
        return bookService.getBookById(id)
            .map(book -> {
                book.setTitle(title);
                book.setAuthor(author);
                book.setCategory(category);
                book.setStatus(status);
                bookService.updateBook(book);
                redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
                return "redirect:/books/" + id;
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
                return "redirect:/books";
            });
    }

    @PostMapping("/books/{id}/borrow")
    public String borrowBook(@PathVariable("id") String id,
                             @RequestParam(required = false) String memberId,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                             RedirectAttributes redirectAttributes) {
        if (memberId == null || memberId.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Select a member before borrowing.");
            return "redirect:/books/" + id;
        }

        return loanService.borrowBook(id, memberId, dueDate)
            .map(loan -> {
                redirectAttributes.addFlashAttribute("successMessage", "Loan created for " + loan.getMemberName());
                return "redirect:/books/" + id;
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Unable to borrow: book unavailable or member not found.");
                return "redirect:/books/" + id;
            });
    }

    @PostMapping("/loans/{id}/return")
    public String returnLoan(@PathVariable("id") String loanId,
                             @RequestParam(required = false) String bookId,
                             @RequestParam(required = false) String memberId,
                             RedirectAttributes redirectAttributes) {
        return loanService.returnLoan(loanId)
            .map(loan -> {
                redirectAttributes.addFlashAttribute("successMessage", "Book marked as returned.");
                if (bookId != null && !bookId.isBlank()) {
                    return "redirect:/books/" + bookId;
                }
                if (memberId != null && !memberId.isBlank()) {
                    return "redirect:/members/" + memberId;
                }
                return "redirect:/members";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Loan not found.");
                if (bookId != null && !bookId.isBlank()) {
                    return "redirect:/books/" + bookId;
                }
                if (memberId != null && !memberId.isBlank()) {
                    return "redirect:/members/" + memberId;
                }
                return "redirect:/members";
            });
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete book: " + e.getMessage());
        }
        return "redirect:/books";
    }

    @PostMapping("/admin/update-credentials")
    public String updateCredentials(@RequestParam String currentPassword,
                                    @RequestParam String newPassword,
                                    RedirectAttributes redirectAttributes) {
        
        boolean success = userService.updateAdminPassword("Nisal Gunasekara", currentPassword, newPassword);

        if (success) {
            // Change from /dashboard to /
            return "redirect:/?status=success"; 
        } else {
            // Change from /dashboard to /
            return "redirect:/?status=error";
        }
    }

}