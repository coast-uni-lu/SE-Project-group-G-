package com.group_g.demo.model;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;



// one multiple choice quiz question stored in MongoDB
@Document(collection = "quiz_questions")
public class QuizQuestion {
    @Id
    private String id;
    private String content;
    private QuizzCategory category;
    private int difficulty;
    private List<String> options;
    private int correctIndex;
    // baseWeight and timesShown are used by QuestionPicker when choosing questions
    private double baseWeight;
    private List<String> tags;
    private int timesShown;

    public QuizQuestion() {
    }

    public QuizQuestion(String content, QuizzCategory category, int difficulty, List<String> options, int correctIndex, double baseWeight,
            List<String> tags, int timesShown) {
        this.content = content;
        this.category = category;
        this.difficulty = difficulty;
        this.options = options;
        this.correctIndex = correctIndex;
        this.baseWeight = baseWeight;
        this.tags = tags;
        this.timesShown = timesShown;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public QuizzCategory getCategory() {
        return category;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getTimesShown() {
        return timesShown;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(QuizzCategory category) {
        this.category = category;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public void setBaseWeight(double baseWeight) {
        this.baseWeight = baseWeight;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setTimesShown(int timesShown) {
        this.timesShown = timesShown;
    }
}
