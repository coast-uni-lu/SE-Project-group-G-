package com.group_g.demo.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;
import com.group_g.demo.model.QuizQuestion;

// question pick Logic

@Component
public class QuestionPicker {
    
    private final Random random;

    public QuestionPicker() {
        this(new Random());
    }

    QuestionPicker(Random random) {
        this.random = random;
    }

    // use assessment score to determine difficulty of questions
    public int targetDifficultyFromScore(int score) {
        int bounded = Math.max(0, Math.min(10, score));
        return Math.min(5, 1 + (bounded / 2));
    }

    public List<QuizQuestion> pickQuestions(List<QuizQuestion> candidates, int assessmentScore, int count) {
        // pick logic here, need to be revised to find a better way to pick questions that are close to the test taker level
        if (candidates.size() < count) {
            throw new IllegalArgumentException("Not enough questions in category");
        }
        int target = targetDifficultyFromScore(assessmentScore);
        List<QuizQuestion> nearTarget = new ArrayList<>();
        for (QuizQuestion question : candidates) {
            if (Math.abs(question.getDifficulty() - target) <= 1) {
                nearTarget.add(question);
            }
        }

        List<QuizQuestion> pool = nearTarget.size() >= count
                ? new ArrayList<>(nearTarget)
                : new ArrayList<>(candidates);
        List<QuizQuestion> selected = new ArrayList<>();

        while (selected.size() < count && !pool.isEmpty()) {
            // weighted random means better matching qurstions have more chance to be picked
            double totalWeight = 0;
            for (QuizQuestion question : pool) {
                totalWeight += computeQuestionWeight(question, target);
            }

            double draw = random.nextDouble() * totalWeight;
            double cumulative = 0;
            int chosenIndex = 0;
            for (int i = 0; i < pool.size(); i++) {
                cumulative += computeQuestionWeight(pool.get(i), target);
                if (draw <= cumulative) {
                    chosenIndex = i;
                    break;
                }
            }
            selected.add(pool.remove(chosenIndex));
        }

        if (selected.size() < count) {
            throw new IllegalStateException("Unable to select enough questions");
        }
        return selected;
    }

    // test to try to give less priority to question that were already used for fairness and resusability
    double computeQuestionWeight(QuizQuestion question, int targetDifficulty) {
        double baseWeight = question.getBaseWeight() <= 0 ? 1.0 : question.getBaseWeight();
        double closenessFactor = 1.0 / (1 + Math.abs(question.getDifficulty() - targetDifficulty));
        double freshnessFactor = 1.0 / (1 + Math.max(0, question.getTimesShown()));
        return baseWeight * closenessFactor * freshnessFactor;
    }
}
