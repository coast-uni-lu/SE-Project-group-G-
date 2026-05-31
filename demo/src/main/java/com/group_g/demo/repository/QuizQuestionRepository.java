package com.group_g.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.QuizQuestion;

public interface QuizQuestionRepository extends MongoRepository<QuizQuestion, String> {
    List<QuizQuestion> findByCategory(QuizzCategory category);
    List<QuizQuestion> findBySubCategoryAndDifficulty(String subCategory, int difficulty);
}
