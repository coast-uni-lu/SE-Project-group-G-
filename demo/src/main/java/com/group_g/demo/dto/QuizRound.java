package com.group_g.demo.dto;

import java.util.List;
import java.util.Map;

import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.QuizQuestion;

public class QuizRound {

    private String sessionId;
    private int round;
    private int maxRounds;
    private List<QuizQuestion> questions;
    private Map<QuizzCategory, Integer> sectionScores;


    public QuizRound(String sessionId, int round, int maxRounds, List<QuizQuestion> questions,
            Map<QuizzCategory, Integer> sectionScores) {
        this.sessionId = sessionId;
        this.round = round;
        this.maxRounds = maxRounds;
        this.questions = questions;
        this.sectionScores = sectionScores;
    }
    //getters
    public String getSessionId() {
        return sessionId;
    }

    public int getRound() {
        return round;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public Map<QuizzCategory, Integer> getSectionScores() {
        return sectionScores;
    }
}
