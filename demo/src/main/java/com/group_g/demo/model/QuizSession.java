package com.group_g.demo.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// keeps track of one user's progressive quiz attempt
@Document(collection = "quiz_sessions")
public class QuizSession {
    @Id
    private String id;
    private String nickname;
    private String email; 
    private Instant createdAt; //used to record the time of the session for the leaderboard
    private int roundNB;
    private Map<String, Integer> nextLVL;
    private Map<QuizzCategory, Integer> sectionScores;
    private List<String> questionID;
    private Map<String, String> questionVariants;  //Get the question variant to grade
    private Map<String, Integer> correctAnswer; //here we get the index for the correct answer to grade
    private int totalQuestions;
    private int correctAnswers;
    private boolean submitted;

    public QuizSession() { //needed for Spring, basically we need an empty constructor to 
                           // create an object before filling it's fields, run time error otherwise
    }                      //only for when we build an object based on the DB documents

    public QuizSession(String id, String nickname, String email, Instant createdAt, int roundNB,
            Map<String, Integer> nextLVL, Map<QuizzCategory, Integer> sectionScores,
            List<String> questionID, Map<String, String> questionVariants,
            Map<String, Integer> correctAnswer,
            int totalQuestions, int correctAnswers, boolean submitted) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.createdAt = createdAt;
        this.roundNB = roundNB;
        this.nextLVL = nextLVL;
        this.sectionScores = sectionScores;
        this.questionID = questionID;
        this.questionVariants = questionVariants;
        this.correctAnswer = correctAnswer;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.submitted = submitted;
    }
    //getters
    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getRoundNB() {
        return roundNB;
    }

    public Map<String, Integer> getNextLvl() {
        return nextLVL;
    }

    public Map<QuizzCategory, Integer> getSectionScores() {
        return sectionScores;
    }

    public List<String> getQuestionID() {
        return questionID;
    }

    public Map<String, String> getQuestionVariants() {
        return questionVariants;
    }

    public Map<String, Integer> getCorrectAnswer() {
        return correctAnswer;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    //setters
    public void setRoundNB(int roundNB) {
        this.roundNB = roundNB;
    }

    public void setNextLvl(Map<String, Integer> nextLVL) {
        this.nextLVL = nextLVL;
    }

    public void setSectionScores(Map<QuizzCategory, Integer> sectionScores) {
        this.sectionScores = sectionScores;
    }

    public void setQuestionID(List<String> questionID) {
        this.questionID = questionID;
    }

    public void setQuestionVariants(Map<String, String> questionVariants) {
        this.questionVariants = questionVariants;
    }

    public void setCorrectAnswer(Map<String, Integer> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
