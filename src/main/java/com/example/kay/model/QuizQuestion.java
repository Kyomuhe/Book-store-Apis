package com.example.kay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuizQuestion {
    private String question;
    private String[] options;
    private int correctAnswer; // optional


}

//container to hold one's question data