package com.group_g.demo.dto;
import java.util.Map;
import com.group_g.demo.model.QuizzCategory;
// final quiz result sent back to the page
public class QuizSubmit {

    private String nickname;
    private int totalQuestions;
    private int correctAnswers;
    private int finalScore;
    private Map<QuizzCategory, Integer> assessmentScores;
    private Map<QuizzCategory, Integer> correctAnswersByCategory;

    public QuizSubmit(String nickname, int totalQuestions, int correctAnswers, int finalScore,
            Map<QuizzCategory, Integer> assessmentScores,
            Map<QuizzCategory, Integer> correctAnswersByCategory) {
        this.nickname = nickname;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.finalScore = finalScore;
        this.assessmentScores = assessmentScores;
        this.correctAnswersByCategory = correctAnswersByCategory;
    }

    public String getNickname() {
        return nickname;
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

    public Map<QuizzCategory, Integer> getAssessmentScores() {
        return assessmentScores;
    }

    public Map<QuizzCategory, Integer> getCorrectAnswersByCategory() {
        return correctAnswersByCategory;
    }
}
