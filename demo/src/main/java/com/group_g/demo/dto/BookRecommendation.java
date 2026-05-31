package com.group_g.demo.dto;

import com.group_g.demo.model.QuizzCategory;

public class BookRecommendation {
    private QuizzCategory category;
    private String title;
    private String author;
    private String reason;

    public BookRecommendation(QuizzCategory category, String title, String author, String reason) {
        this.category = category;
        this.title = title;
        this.author = author;
        this.reason = reason;
    }
    //getters
    public QuizzCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getReason() {
        return reason;
    }
}
