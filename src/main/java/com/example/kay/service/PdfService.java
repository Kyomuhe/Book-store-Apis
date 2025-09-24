package com.example.kay.service;

import com.example.kay.model.QuizQuestion;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateQuizPdf(String title, List<QuizQuestion> questions) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Creating fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font questionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            // Title
            Paragraph titlePara = new Paragraph(title != null ? title : "Quiz", titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            document.add(titlePara);

            // Adding some space
            document.add(new Paragraph(" ", normalFont));

            // Adding instructions
            Paragraph instructions = new Paragraph("Instructions: Choose the best answer for each question.", normalFont);
            document.add(instructions);
            document.add(new Paragraph(" ", normalFont));

            // Adding questions
            for (int i = 0; i < questions.size(); i++) {
                QuizQuestion q = questions.get(i);

                // Question number and text
                String questionText = (i + 1) + ". " + q.getQuestion();
                Paragraph questionPara = new Paragraph(questionText, questionFont);
                document.add(questionPara);

                // Options
                if (q.getOptions() != null && q.getOptions().length > 0) {
                    for (int j = 0; j < q.getOptions().length; j++) {
                        String optionLetter = String.valueOf((char) ('A' + j));
                        String optionText = "   " + optionLetter + ") " + q.getOptions()[j];
                        Paragraph optionPara = new Paragraph(optionText, normalFont);
                        document.add(optionPara);
                    }
                }

                // Add space between questions
                document.add(new Paragraph(" ", normalFont));
            }

            document.close();
            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
