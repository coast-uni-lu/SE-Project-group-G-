package com.group_g.demo.dto;
import java.util.Map;
import com.group_g.demo.model.QuizzCategory;

// data sent from back end to front end aafter assessment submission
public class AssessmentSubmit {   
    private String sessionId;
    private String nickname;
    private Map<QuizzCategory, Integer> categoryScore;

    public AssessmentSubmit(String sessionId, String nickname, Map<QuizzCategory, Integer> categoryScore) {
        this.sessionId = sessionId;
        this.nickname = nickname;
        this.categoryScore = categoryScore;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getNickname() {
        return nickname;
    }

    public Map<QuizzCategory, Integer> getCategoryScore() {
        return categoryScore;
    }
}
