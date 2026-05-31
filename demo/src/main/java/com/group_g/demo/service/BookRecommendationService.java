package com.group_g.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.group_g.demo.dto.BookRecommendation;
import com.group_g.demo.model.QuizzCategory;

@Service
public class BookRecommendationService {

    public List<BookRecommendation> recommendBooks(Map<QuizzCategory, Integer> sectionScores,
            Map<QuizzCategory, Integer> sectionMaxScores) {
        List<BookRecommendation> recommendations = new ArrayList<>();
        for (QuizzCategory category : QuizzCategory.values()) {
            int score = sectionScores.getOrDefault(category, 0);
            int maxScore = sectionMaxScores.getOrDefault(category, 30);
            double ratio = maxScore == 0 ? 0 : score / (double) maxScore;
            recommendations.add(recommendationFor(category, ratio));
        }
        return recommendations;
    }

    private BookRecommendation recommendationFor(QuizzCategory category, double ratio) {
        boolean needsFoundations = ratio < 0.5;

        switch (category) {
            case CALCULUS:
                return needsFoundations
                        ? new BookRecommendation(category, 
                                "How big is big? How fast is fast?: a hands on tutorial on mathematics of computation", "Franck Leprévost",
                                "Accessible at the LLC or on Amazon")
                        : new BookRecommendation(category, 
                                "Calculus", 
                                "James Stewart",
                                "The most famous Calculus notebook worldwide");
            case DISCRETE_MATHEMATICS:
                return needsFoundations
                        ? new BookRecommendation(category, 
                                "Discrete Mathematics with Ducks", 
                                "Sarah-Marie Belcastro",
                                "Approachable practice for logic, sets, and modular arithmetic.")
                        : new BookRecommendation(category, 
                                "Discrete Mathematics and Its Applications",
                                "Kenneth H. Rosen", 
                                "A standard reference for deeper discrete mathematics.");
            case PROGRAMMING_BASICS:
                return needsFoundations
                        ? new BookRecommendation(category, 
                                "Head First Java", 
                                "Kathy Sierra and Bert Bates",
                                "This books stands out because it is specifically engineered for absolute beginners using an interactive, highly visual approach.")
                        : new BookRecommendation(category, 
                                "How to Design Programs",
                                "Matthias Felleisen et al.",
                                "A deeper route into program design and recursion.");
            case INTRODUCTION_A_L_INFORMATIQUE:
                return needsFoundations
                        ? new BookRecommendation(category, 
                                "Digital fundamentals", 
                                "Thomas Floyd",
                                "Provides the core fundamentals of digital technology.")
                        : new BookRecommendation(category, 
                                "Computer Science: An Overview", 
                                "J. Glenn Brookshear",
                                "A stronger survey of the field after the basics.");
            default:
                throw new IllegalArgumentException("Unsupported category " + category);
        }
    }
}
