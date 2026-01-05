package com.library.library_system.controller;

import com.library.library_system.model.Book;
import com.library.library_system.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * BookAdminController - Admin operations for book management
 * 
 * LOGIC:
 * - Admin can add new books (creates book with AVAILABLE status)
 * - Admin can delete books from inventory
 * - Admin can update book details
 * - Display all books with search and filter capabilities
 */
@Controller
@RequestMapping("/admin/books")
public class BookAdminController {

    @Autowired
    private BookService bookService;

    /**
     * Display all books with search and filter
     */
    @GetMapping
    public String listBooks(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String status,
                            Model model) {
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
        model.addAttribute("totalBooks", bookService.getTotalBooks());
        model.addAttribute("availableBooks", bookService.getCountByStatus("AVAILABLE"));
        model.addAttribute("borrowedBooks", bookService.getCountByStatus("BORROWED"));
        
        return "layout/books_dash";
    }

    /**
     * Show add book form
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "dashboard/add_book";
    }

    /**
     * Add new book to inventory
     * ADMIN ONLY: Creates book with AVAILABLE status and borrowCount = 0
     */
    @PostMapping("/add")
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
            
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add book: " + e.getMessage());
            return "redirect:/admin/books/add";
        }
    }

    /**
     * Show book details
     */
    @GetMapping("/{id}")
    public String viewBook(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        return bookService.getBookById(id)
            .map(book -> {
                model.addAttribute("book", book);
                return "layout/book_detail";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
                return "redirect:/admin/books";
            });
    }

    /**
     * Update book details
     */
    @PostMapping("/{id}/update")
    public String updateBook(@PathVariable String id,
                             @RequestParam String title,
                             @RequestParam String author,
                             @RequestParam String category,
                             @RequestParam String status,
                             RedirectAttributes redirectAttributes) {
        try {
            return bookService.getBookById(id)
                .map(book -> {
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setCategory(category);
                    book.setStatus(status);
                    bookService.updateBook(book);
                    redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
                    return "redirect:/admin/books/" + id;
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
                    return "redirect:/admin/books";
                });
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update book: " + e.getMessage());
            return "redirect:/admin/books/" + id;
        }
    }

    /**
     * Delete book from inventory
     * ADMIN ONLY: Removes book completely from system
     */
    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            // Check if book is not currently borrowed before deletion
            return bookService.getBookById(id)
                .map(book -> {
                    if ("BORROWED".equals(book.getStatus())) {
                        redirectAttributes.addFlashAttribute("errorMessage", 
                            "Cannot delete: Book is currently borrowed. Please wait for return.");
                        return "redirect:/admin/books/" + id;
                    }
                    
                    bookService.deleteBook(id);
                    redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully");
                    return "redirect:/admin/books";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
                    return "redirect:/admin/books";
                });
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete book: " + e.getMessage());
            return "redirect:/admin/books";
        }
    }
}
