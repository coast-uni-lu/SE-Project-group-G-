package com.group_g.demo.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.group_g.demo.dto.BookRecommendation;
import com.group_g.demo.model.QuizzCategory;

@Service
public class BookRecommendationService {

    public List<BookRecommendation> recommendBooks(int finalScore, Map<QuizzCategory, Integer> correctByCategory) {
        return correctByCategory.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .limit(2)
                .map(entry -> recommendationFor(entry.getKey(), finalScore))
                .toList();
    }

    private BookRecommendation recommendationFor(QuizzCategory category, int finalScore) {
        boolean beginner = finalScore < 60;
        boolean advanced = finalScore >= 80;

        return switch (category) {
            case LOGICAL_REASONING -> beginner
                    ? new BookRecommendation(category, "How to Solve It", "George Polya",
                            "Good next step for building clear problem-solving habits.")
                    : new BookRecommendation(category, "The Art and Craft of Problem Solving", "Paul Zeitz",
                            advanced ? "A stronger challenge for sharpening mathematical reasoning."
                                    : "Useful for practicing structured reasoning beyond basics.");
            case MATHS_BASICS -> beginner
                    ? new BookRecommendation(category, "Mathematics for Computer Science",
                            "Eric Lehman, F. Thomson Leighton, Albert R. Meyer",
                            "Covers the discrete maths foundations used across CS.")
                    : new BookRecommendation(category, "Concrete Mathematics",
                            "Ronald L. Graham, Donald E. Knuth, Oren Patashnik",
                            advanced ? "A deep bridge between maths and algorithmic thinking."
                                    : "Helps connect formulas, sums, and proof techniques to computing.");
            case PROGRAMMING_CONCEPTS -> beginner
                    ? new BookRecommendation(category, "Think Python", "Allen B. Downey",
                            "A friendly path through variables, functions, loops, and data structures.")
                    : new BookRecommendation(category, "Structure and Interpretation of Computer Programs",
                            "Harold Abelson, Gerald Jay Sussman, Julie Sussman",
                            advanced ? "A classic for deeper programming abstraction."
                                    : "Good for moving from syntax to computational ideas.");
            case PROBLEM_SOLVING_SKILLS -> beginner
                    ? new BookRecommendation(category, "Grokking Algorithms", "Aditya Y. Bhargava",
                            "Visual, approachable practice with common algorithm patterns.")
                    : new BookRecommendation(category, "Introduction to Algorithms",
                            "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein",
                            advanced ? "A comprehensive reference for serious algorithm study."
                                    : "Best when you are ready to formalize algorithm design and analysis.");
        };
    }
}
