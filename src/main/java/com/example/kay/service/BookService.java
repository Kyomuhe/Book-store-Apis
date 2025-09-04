package com.example.kay.service;

import com.example.kay.model.Book;
import com.example.kay.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    //constructor injection
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    //displaying all books
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }


//creating a new book
    public Book saveBook(Book book){
        return bookRepository.save(book);
    }
    //updating an existing book
    public Book updateBook(Long id, Book bookDetails){
        Optional<Book> optionalBook = bookRepository.findById(id);

        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setPrice(bookDetails.getPrice());
            book.setQuantity(bookDetails.getQuantity());
            return bookRepository.save(book);
        }
        return null;
    }

    //delete a book
    public boolean deleteBook(Long id){
        if(bookRepository.existsById(id)){
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
    //searching books by title
    public List<Book> searchBookByTitle(String title){

        return bookRepository.findByTitleContainingIgnoreCase(title);
    }


}
