package com.group_g.demo.service;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.group_g.demo.dto.AssessmentRequest;
import com.group_g.demo.dto.AssessmentSubmit;
import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.AssessmentItem;
import com.group_g.demo.model.QuizSession;
import com.group_g.demo.repository.AssessmentItemRepository;
import com.group_g.demo.repository.QuizSessionRepository;

// assessment logic
@Service
public class AssessmentService {

    private final AssessmentItemRepository assessmentItemRepository;
    private final QuizSessionRepository quizSessionRepository;

    public AssessmentService(AssessmentItemRepository assessmentItemRepository,
            QuizSessionRepository quizSessionRepository) {
        this.assessmentItemRepository = assessmentItemRepository;
        this.quizSessionRepository = quizSessionRepository;
    }

    public List<AssessmentItem> getQuestions() {
        // gets assessment questions from db in order
        return assessmentItemRepository.findAllByOrderByOrderIndexAsc();
    }

    public AssessmentSubmit submit(AssessmentRequest request) {
        // adds up category scores and creates a session for the user
        String nickname = request.getNickname().trim();
        List<AssessmentItem> items = getQuestions();
        if (request.getAnswers().size() != items.size()) {
            throw new IllegalArgumentException("All assessment items must be answered");
        }

        Map<QuizzCategory, Integer> scores = new EnumMap<>(QuizzCategory.class);
        for (QuizzCategory category : QuizzCategory.values()) {
            // start each category at 0 before adding answers
            scores.put(category, 0);
        }

        for (AssessmentItem item : items) {
            Integer answer = request.getAnswers().get(item.getId());
            int maxIndex = item.getOptions().size() - 1;
            if (answer == null || answer < 0 || answer > maxIndex) {
                throw new IllegalArgumentException("Invalid assessment answer value");
            }
            scores.put(item.getCategory(), scores.get(item.getCategory()) + answer);
        }

        String sessionId = UUID.randomUUID().toString();
        // save this user's quiz session so the next API call can find it
        quizSessionRepository.save(new QuizSession(
                sessionId,
                nickname,
                Instant.now(),
                scores,
                null,
                false));

        return new AssessmentSubmit(sessionId, nickname, scores);
    }
}
