package com.group_g.demo.model;
import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
// final saved result used later for leaderboard
@Document(collection = "attempt_results")
public class FinalResult {

    @Id
    private String id;
    private String sessionId;
    private String nickname;
    private Instant timestamp;
    private Map<QuizzCategory, Integer> assessmentScores;
    private int totalQuestions;
    private int correctAnswers;
    private int finalScore;

    public FinalResult() {
    }

    public FinalResult(String sessionId, String nickname, Instant timestamp, Map<QuizzCategory, Integer> assessmentScores,
            int totalQuestions, int correctAnswers, int finalScore) {
        this.sessionId = sessionId;
        this.nickname = nickname;
        this.timestamp = timestamp;
        this.assessmentScores = assessmentScores;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.finalScore = finalScore;
    }

    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getNickname() {
        return nickname;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<QuizzCategory, Integer> getAssessmentScores() {
        return assessmentScores;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setAssessmentScores(Map<QuizzCategory, Integer> assessmentScores) {
        this.assessmentScores = assessmentScores;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }
}
