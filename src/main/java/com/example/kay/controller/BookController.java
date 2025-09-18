package com.example.kay.controller;
import com.example.kay.model.Book;
import com.example.kay.service.BookService;
import com.example.kay.service.CsvImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;


import java.util.*;

@RestController
@RequestMapping("api/books")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CsvImportService csvImportService;


    @Value("${app.name.message}")
    private String message;

    @GetMapping("/welcome")
    public String welcome() {
        return message;
    }

//returning all books
    @GetMapping("/display")
    public ResponseEntity<List<Book>> getAllBooks(){
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);

    }

    //searching using title
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String title) {
        List<Book> books = bookService.searchBookByTitle(title);
        return ResponseEntity.ok(books);
    }

    //deleting a book
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    //Create a new book
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

//update book
    @PutMapping("/update/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Book updatedBook = bookService.updateBook(id, bookDetails);
        if (updatedBook != null) {
            return ResponseEntity.ok(updatedBook);
        } else {
            return ResponseEntity.notFound().build();
    }

}

//displaying books using pages
    @GetMapping("/pages")
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        Page<Book> bookPage = bookService.getAllBooksWithPagination(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("books", bookPage.getContent());
        response.put("totalPages", bookPage.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            csvImportService.importCsv(file);
            return "CSV data imported successfully!";
        } catch (Exception e) {
            return "Error importing CSV: " + e.getMessage();
        }
    }
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportToExcel() {
        try {
            byte[] excelData = csvImportService.exportBooksToExcel();
            String filename = "books_export.xlsx";

            ByteArrayResource resource = new ByteArrayResource(excelData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
@GetMapping("/pdf")
    public ResponseEntity<byte[]> exportBooksToPdf() {
        try {
            byte[] pdfData = csvImportService.exportBooksToPdf();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "books.pdf");

            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}