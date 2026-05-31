package com.group_g.demo.dto;

import java.util.List;
import java.util.Map;

import com.group_g.demo.model.QuizzCategory;

// round or final quiz result sent back to the page
public class QuizSubmit {

    private String sessionId;
    private String nickname;
    private boolean completed;
    private QuizRound nextRound;
    private int totalQuestions;
    private int correctAnswers;
    private int finalScore;
    private Map<QuizzCategory, Integer> sectionScores;
    private Map<QuizzCategory, Integer> sectionMaxScores;
    private List<BookRecommendation> bookRecommendations;
    private boolean emailSent;
    
    public QuizSubmit(String sessionId, String nickname, boolean completed, QuizRound nextRound, int totalQuestions,
            int correctAnswers, int finalScore, Map<QuizzCategory, Integer> sectionScores,
            Map<QuizzCategory, Integer> sectionMaxScores, List<BookRecommendation> bookRecommendations,
            boolean emailSent) {
        this.sessionId = sessionId;
        this.nickname = nickname;
        this.completed = completed;
        this.nextRound = nextRound;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.finalScore = finalScore;
        this.sectionScores = sectionScores;
        this.sectionMaxScores = sectionMaxScores;
        this.bookRecommendations = bookRecommendations;
        this.emailSent = emailSent;
    }
    //getters
    public String getSessionId() {
        return sessionId;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isCompleted() {
        return completed;
    }

    public QuizRound getNextRound() {
        return nextRound;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public Map<QuizzCategory, Integer> getSectionScores() {
        return sectionScores;
    }

    public Map<QuizzCategory, Integer> getSectionMaxScores() {
        return sectionMaxScores;
    }

    public List<BookRecommendation> getBookRecommendations() {
        return bookRecommendations;
    }

    public boolean isEmailSent() {
        return emailSent;
    }
}
