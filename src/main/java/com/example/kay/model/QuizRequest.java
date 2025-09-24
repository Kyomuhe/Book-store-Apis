package com.example.kay.model;

import java.util.List;

public class QuizRequest {
    private String title;
    private List<QuizQuestion> questions;

    public QuizRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<QuizQuestion> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }
}

//Container for the entire request. Holds the quiz title + all questions.