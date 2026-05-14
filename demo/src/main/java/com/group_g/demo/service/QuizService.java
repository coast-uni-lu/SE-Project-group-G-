package com.group_g.demo.service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.group_g.demo.dto.QuizRequest;
import com.group_g.demo.dto.QuizSubmit;
import com.group_g.demo.model.FinalResult;
import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.QuizQuestion;
import com.group_g.demo.model.QuizSession;
import com.group_g.demo.repository.LeaderboardRepository;
import com.group_g.demo.repository.QuizQuestionRepository;
import com.group_g.demo.repository.QuizSessionRepository;

// Quizz LOGIC

@Service
public class QuizService {
    
    private final QuizSessionRepository quizSessionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final LeaderboardRepository attemptResultRepository;
    private final QuestionPicker questionPicker;

    public QuizService(QuizSessionRepository quizSessionRepository, QuizQuestionRepository quizQuestionRepository,
            LeaderboardRepository attemptResultRepository, QuestionPicker questionPicker) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.attemptResultRepository = attemptResultRepository;
        this.questionPicker = questionPicker;
    }

    public List<QuizQuestion> getQuiz(String sessionId) {
        // creates a quiz for a session, or returns the same quiz if already created
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getSelectedQuestionIds() == null || session.getSelectedQuestionIds().isEmpty()) {
            List<QuizQuestion> selectedQuestions = new ArrayList<>();

            for (QuizzCategory category : QuizzCategory.values()) {
                // pick 5 questions from each category
                List<QuizQuestion> candidates = quizQuestionRepository.findByCategory(category);
                int assessmentScore = session.getAssessmentScores().getOrDefault(category, 0);
                List<QuizQuestion> chosen = questionPicker.pickQuestions(candidates, assessmentScore, 5);
                selectedQuestions.addAll(chosen);
            }

            selectedQuestions.sort(Comparator.comparing(QuizQuestion::getCategory));
            for (QuizQuestion question : selectedQuestions) {
                // count how many times this question has appeared
                question.setTimesShown(question.getTimesShown() + 1);
            }
            quizQuestionRepository.saveAll(selectedQuestions);
            List<String> selectedIds = new ArrayList<>();
            for (QuizQuestion question : selectedQuestions) {
                selectedIds.add(question.getId());
            }
            session.setSelectedQuestionIds(selectedIds);
            quizSessionRepository.save(session);
            return selectedQuestions;
        }

        List<QuizQuestion> questions = quizQuestionRepository.findAllById(session.getSelectedQuestionIds());
        // sort again so the page shows categories grouped together
        questions.sort(Comparator.comparing(QuizQuestion::getCategory));
        return questions;
    }

    public QuizSubmit submitQuiz(QuizRequest request) {
        // checks answers, saves final result, and returns score
        QuizSession session = quizSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.isSubmitted()) {
            throw new IllegalStateException("Quiz already submitted for this session");
        }

        List<String> questionIds = session.getSelectedQuestionIds();
        if (questionIds == null || questionIds.isEmpty()) {
            throw new IllegalArgumentException("Quiz not generated for this session");
        }

        Map<String, QuizQuestion> questionById = new HashMap<>();
        for (QuizQuestion question : quizQuestionRepository.findAllById(questionIds)) {
            // easier to find each question by its id later
            questionById.put(question.getId(), question);
        }

        int correct = 0;
        Map<QuizzCategory, Integer> correctByCategory = new EnumMap<>(QuizzCategory.class);
        for (QuizzCategory category : QuizzCategory.values()) {
            correctByCategory.put(category, 0);
        }

        for (String questionId : questionIds) {
            QuizQuestion question = questionById.get(questionId);
            Integer selectedIndex = request.getAnswers().get(questionId);
            if (selectedIndex != null && selectedIndex == question.getCorrectIndex()) {
                correct++;
                correctByCategory.put(question.getCategory(), correctByCategory.get(question.getCategory()) + 1);
            }
        }

        int total = questionIds.size();
        int finalScore = (int) Math.round((correct * 100.0) / total);

        // save final score for leaderboard
        attemptResultRepository.save(new FinalResult(
                session.getId(),
                session.getNickname(),
                Instant.now(),
                session.getAssessmentScores(),
                total,
                correct,
                finalScore));

        session.setSubmitted(true);
        quizSessionRepository.save(session);

        return new QuizSubmit(
                session.getNickname(),
                total,
                correct,
                finalScore,
                session.getAssessmentScores(),
                correctByCategory);
    }
}
