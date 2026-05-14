package com.group_g.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.group_g.demo.model.AssessmentItem;

public interface AssessmentItemRepository extends MongoRepository<AssessmentItem, String> {
    List<AssessmentItem> findAllByOrderByOrderIndexAsc();
    // spring reads the method's namee and automatically builds the querry based on it
    // gets all starter questions in the right order for the page
    // OrderIndex = number of the question
}
