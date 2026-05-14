package com.group_g.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.group_g.demo.model.QuizSession;

public interface QuizSessionRepository extends MongoRepository<QuizSession, String> {
    // no query needed, but need for class to exist and extend MongoRepository to access commands like save(), deleteAll()
}
