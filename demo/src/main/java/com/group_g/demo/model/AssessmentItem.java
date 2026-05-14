package com.group_g.demo.model;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// one starter assessment question stored in MongoDB
@Document(collection = "assessment_items")
public class AssessmentItem {

    @Id
    private String id;
    private String content;
    private QuizzCategory category;
    private int orderIndex;
    private List<String> options;

    public AssessmentItem() {
    }

    public AssessmentItem(String content, QuizzCategory category, int orderIndex, List<String> options) {
        this.content = content;
        this.category = category;
        this.orderIndex = orderIndex;
        this.options = options;
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

    public int getOrderIndex() {
        return orderIndex;
    }

    public List<String> getOptions() {
        return options;
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

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
