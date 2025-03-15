package com.example.game;

public class Question {
    private String question;
    private String optionA, optionB, optionC, optionD;
    private String correctAnswer;
    private String explanation;  // New field for the explanation

    public Question(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String explanation) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;  // Assigning explanation
    }

    // Getters
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; } // Getter for explanation
}
