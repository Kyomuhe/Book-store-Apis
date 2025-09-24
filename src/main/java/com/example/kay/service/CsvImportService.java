package com.example.kay.service;

import com.example.kay.model.Book;
import com.example.kay.repository.BookRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CsvImportService {
    private final BookRepository bookRepository;

    //parsing csv file to database
    public void importCsv(MultipartFile file) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.readNext(); // skip header row

            List<Book> books = new ArrayList<>();

            while ((line = reader.readNext()) != null) {
                Book book = new Book();
                book.setTitle(line[0]);
                book.setDescription(line[1]);
                book.setPrice(new BigDecimal(line[2]));
                book.setAuthor(line[3]);
                book.setQuantity(Integer.parseInt(line[4]));
                books.add(book);
            }

            bookRepository.saveAll(books);
        }
    }

    //get excel
    public byte[] exportBooksToExcel() throws IOException {
        List<Book> books = bookRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Books");

            // Create header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Title", "Description", "Price", "Author", "Quantity"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // Add data
            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getTitle());
                row.createCell(1).setCellValue(book.getDescription());
                row.createCell(2).setCellValue(book.getPrice().doubleValue());
                row.createCell(3).setCellValue(book.getAuthor());
                row.createCell(4).setCellValue(book.getQuantity());
            }
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    //get pdf
    public byte[] exportBooksToPdf() throws IOException {
        List<Book> books = bookRepository.findAll();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(5);

            table.addCell("Title");
            table.addCell("Description");
            table.addCell("Price");
            table.addCell("Author");
            table.addCell("Quantity");

            for (Book book : books) {
                table.addCell(book.getTitle());
                table.addCell(book.getDescription());
                table.addCell(book.getPrice().toString());
                table.addCell(book.getAuthor());
                table.addCell(String.valueOf(book.getQuantity()));
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Error generating PDF", e);
        }
    }


}