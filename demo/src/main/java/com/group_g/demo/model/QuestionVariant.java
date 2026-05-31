package com.group_g.demo.model;

// one variant of a question: different phrasing/values with its own correct answer index
public class QuestionVariant {
    private String content;
    private int correctIndex;

    public QuestionVariant() {
    }

    public QuestionVariant(String content, int correctIndex) {
        this.content = content;
        this.correctIndex = correctIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }
}
