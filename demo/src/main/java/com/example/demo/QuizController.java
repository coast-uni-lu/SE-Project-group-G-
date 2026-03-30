package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QuizController {

    @Autowired
    private QuestionRepository repository;

    @GetMapping("/questions")
    public List<Question> getQuestions() {
        return repository.findAll();
    }
}