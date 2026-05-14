package com.group_g.demo.dto;
import java.util.Map;

// data sent from frontend when user submits main quiz
public class QuizRequest {

    private String sessionId;

    private Map<String, Integer> answers;

    public String getSessionId() {
        return sessionId;
    }

    public Map<String, Integer> getAnswers() {
        return answers;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setAnswers(Map<String, Integer> answers) {
        this.answers = answers;
    }
}
