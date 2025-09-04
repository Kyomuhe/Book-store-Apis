package com.example.kay.repository;
import com.example.kay.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    //Page<Book> getAllBooks(Pageable pageable);




    }
