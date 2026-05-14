package com.group_g.demo.dto;
import java.time.Instant;

// object used to display ONE row in leaderboard
public class Leaderboard {

    private String nickname;
    private int finalScore;
    private int correctAnswers;
    private int totalQuestions;
    private Instant timestamp;

    public Leaderboard(String nickname, int finalScore, int correctAnswers, int totalQuestions, Instant timestamp) {
        this.nickname = nickname;
        this.finalScore = finalScore;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.timestamp = timestamp;
    }

    public String getNickname() {
        return nickname;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
