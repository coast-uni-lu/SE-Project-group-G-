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
    private String subCategory;
    private int difficulty;
    private List<String> options;
    private int correctIndex;
    private double baseWeight;
    private List<String> tags;
    private List<QuestionVariant> variants;

    public QuizQuestion() { //needed for Spring, basically we need an empty constructor to 
                            // create an object before filling it's fields, run time error otherwise
    }                       //only for when we build an object based on the DB documents
    

    public QuizQuestion(String content, QuizzCategory category, String subCategory, int difficulty,
            List<String> options, int correctIndex, double baseWeight,
            List<String> tags, List<QuestionVariant> variants) {
        this.content = content;
        this.category = category;
        this.subCategory = subCategory;
        this.difficulty = difficulty;
        this.options = options;
        this.correctIndex = correctIndex;
        this.baseWeight = baseWeight;
        this.tags = tags;
        this.variants = variants;
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

    public String getSubCategory() {
        return subCategory;
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

    public List<QuestionVariant> getVariants() {
        return variants;
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

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
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

    public void setVariants(List<QuestionVariant> variants) {
        this.variants = variants;
    }
}
