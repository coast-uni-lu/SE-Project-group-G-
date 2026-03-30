package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.List;

@Data
@Document(collection = "questions")
public class Question {
    @Id
    private String id;
    private String text;
    private List<String> options;
    private int correctAnswerIndex;
}