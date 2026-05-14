package com.group_g.demo.model;
import java.time.Instant;

import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;


// keeps track of one user's quiz attempt before/after submitting
@Document(collection = "quiz_sessions")
public class QuizSession {
    @Id
    private String id;
    private String nickname;
    private Instant createdAt;
    private Map<QuizzCategory, Integer> assessmentScores;
    // keeps same questions if user refreshes instead of making a new quiz
    private List<String> selectedQuestionIds;
    private boolean submitted;

    public QuizSession() {
    }

    public QuizSession(String id, String nickname, Instant createdAt, Map<QuizzCategory, Integer> assessmentScores,
            List<String> selectedQuestionIds, boolean submitted) {
        this.id = id;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.assessmentScores = assessmentScores;
        this.selectedQuestionIds = selectedQuestionIds;
        this.submitted = submitted;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Map<QuizzCategory, Integer> getAssessmentScores() {
        return assessmentScores;
    }

    public List<String> getSelectedQuestionIds() {
        return selectedQuestionIds;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setAssessmentScores(Map<QuizzCategory, Integer> assessmentScores) {
        this.assessmentScores = assessmentScores;
    }

    public void setSelectedQuestionIds(List<String> selectedQuestionIds) {
        this.selectedQuestionIds = selectedQuestionIds;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
