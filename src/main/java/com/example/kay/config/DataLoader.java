package com.example.kay.config;

import com.example.kay.model.Book;
import com.example.kay.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // This makes it a Spring bean
public class DataLoader implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        // Load sample data when application starts
        if (bookRepository.count() == 0) {

            Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", 15.99,
                    "A classic American novel about the Jazz Age", 10);

            Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", 12.99,
                    "A gripping tale of racial injustice and childhood innocence", 8);

            Book book3 = new Book("1984", "George Orwell", 13.99,
                    "A dystopian social science fiction novel", 15);

            Book book4 = new Book("Pride and Prejudice", "Jane Austen", 11.99,
                    "A romantic novel of manners", 12);

            Book book5 = new Book("The Catcher in the Rye", "J.D. Salinger", 14.99,
                    "A controversial novel about teenage rebellion", 6);

            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);
            bookRepository.save(book4);
            bookRepository.save(book5);

            System.out.println("✅ Sample books loaded into database!");
        }
    }
}

