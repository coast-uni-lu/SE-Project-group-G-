package com.group_g.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.QuizQuestion;

public interface QuizQuestionRepository extends MongoRepository<QuizQuestion, String> {
    List<QuizQuestion> findByCategory(QuizzCategory category);
    // spring reads the method's namee and automatically builds the querry based on it
    // gets only the quiz questions from one category, so quizz looks more organized
}
