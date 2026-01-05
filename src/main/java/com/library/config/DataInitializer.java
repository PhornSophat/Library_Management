package com.library.config;

import com.library.model.Book;
import com.library.model.Book.BookStatus;
import com.library.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    public DataInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no books exist
        if (bookRepository.count() == 0) {
            initializeSampleBooks();
        }
    }

    private void initializeSampleBooks() {
        List<Book> sampleBooks = Arrays.asList(
            createBook("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", 
                Arrays.asList("Fiction", "Classic", "American Literature")),
            
            createBook("To Kill a Mockingbird", "Harper Lee", "978-0061120084", 
                Arrays.asList("Fiction", "Classic", "Southern Gothic")),
            
            createBook("1984", "George Orwell", "978-0451524935", 
                Arrays.asList("Fiction", "Dystopian", "Political")),
            
            createBook("Pride and Prejudice", "Jane Austen", "978-0141439518", 
                Arrays.asList("Fiction", "Romance", "Classic")),
            
            createBook("The Catcher in the Rye", "J.D. Salinger", "978-0316769488", 
                Arrays.asList("Fiction", "Coming-of-age", "Classic")),
            
            createBook("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "978-0590353427", 
                Arrays.asList("Fantasy", "Young Adult", "Magic")),
            
            createBook("The Hobbit", "J.R.R. Tolkien", "978-0547928227", 
                Arrays.asList("Fantasy", "Adventure", "Classic")),
            
            createBook("The Lord of the Rings", "J.R.R. Tolkien", "978-0618640157", 
                Arrays.asList("Fantasy", "Epic", "Adventure")),
            
            createBook("Clean Code", "Robert C. Martin", "978-0132350884", 
                Arrays.asList("Programming", "Software Development", "Best Practices")),
            
            createBook("The Pragmatic Programmer", "David Thomas & Andrew Hunt", "978-0135957059", 
                Arrays.asList("Programming", "Software Development", "Career")),
            
            createBook("Introduction to Algorithms", "Thomas H. Cormen", "978-0262033848", 
                Arrays.asList("Computer Science", "Algorithms", "Textbook")),
            
            createBook("Design Patterns", "Gang of Four", "978-0201633610", 
                Arrays.asList("Programming", "Software Architecture", "Patterns")),
            
            createBook("The Art of War", "Sun Tzu", "978-1599869773", 
                Arrays.asList("Philosophy", "Strategy", "Classic")),
            
            createBook("Atomic Habits", "James Clear", "978-0735211292", 
                Arrays.asList("Self-Help", "Productivity", "Psychology")),
            
            createBook("Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "978-0062316097", 
                Arrays.asList("History", "Anthropology", "Non-Fiction"))
        );

        bookRepository.saveAll(sampleBooks);
        System.out.println("✅ Initialized " + sampleBooks.size() + " sample books in the database.");
    }

    private Book createBook(String title, String author, String isbn, List<String> tags) {
        Book book = new Book(title, author, isbn);
        book.setTags(tags);
        book.setStatus(BookStatus.AVAILABLE);
        return book;
    }
}
