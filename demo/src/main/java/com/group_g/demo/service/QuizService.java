package com.group_g.demo.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.group_g.demo.dto.BookRecommendation;
import com.group_g.demo.dto.QuizRequest;
import com.group_g.demo.dto.QuizRound;
import com.group_g.demo.dto.QuizStartRequest;
import com.group_g.demo.dto.QuizSubmit;
import com.group_g.demo.model.FinalResult;
import com.group_g.demo.model.QuestionVariant;
import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.QuizQuestion;
import com.group_g.demo.model.QuizSession;
import com.group_g.demo.repository.LeaderboardRepository;
import com.group_g.demo.repository.QuizQuestionRepository;
import com.group_g.demo.repository.QuizSessionRepository;

//progressive logic for API call
@Service
public class QuizService {

    private static final int MAX_ROUNDS = 3; //just in case we wanted to scale up the quiz
    private static final int LEVEL_1_POINTS = 6; //here the points are higher for easier question because we want 
    private static final int LEVEL_2_POINTS = 3; //the users to feel ready for the cursus, we consider having the 
    private static final int LEVEL_3_POINTS = 1; //basic logic to pass the first rounds will already be a big plus

    private static final List<String> SUB_CATEGORY_ORDER = List.of(
            "M1", //M for Math (Calculus and math discreet)
            "M2",
            "M3",
            "M4",
            "M5",
            "M6",
            "P2", //P for Programming
            "P1",
            "P3",
            "C1", //C for Computer (informatique / algorithms)
            "C2",
            "C3");

    private final QuizSessionRepository quizSessionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final LeaderboardRepository attemptResultRepository;
    private final BookRecommendationService bookRecommendationService;
    private final ResultEmailService resultEmailService;

    public QuizService(QuizSessionRepository quizSessionRepository, QuizQuestionRepository quizQuestionRepository,
            LeaderboardRepository attemptResultRepository, BookRecommendationService bookRecommendationService,
            ResultEmailService resultEmailService) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.attemptResultRepository = attemptResultRepository;
        this.bookRecommendationService = bookRecommendationService;
        this.resultEmailService = resultEmailService;
    }

    public QuizRound startQuiz(QuizStartRequest request) {
        if (request == null || request.getNickname() == null || request.getNickname().isBlank()) {
            throw new IllegalArgumentException("For now, you are required to enter a Nickname");
        }

        QuizSession session = new QuizSession(
                UUID.randomUUID().toString(),
                request.getNickname().trim(),
                normalizeEmail(request.getEmail()),
                Instant.now(),
                1,
                initialLevels(),
                emptySectionScores(),
                List.of(),
                Map.of(),
                Map.of(),
                0,
                0,
                false);
        generateCurrentRound(session);
        quizSessionRepository.save(session);
        return roundResponse(session);
    }

    public QuizRound getRoundNB(String sessionId) {
        QuizSession session = getSession(sessionId);
        if (session.isSubmitted()) {
            throw new IllegalStateException("Quiz already submitted for this session");
        }
        if (session.getQuestionID() == null || session.getQuestionID().isEmpty()) {
            generateCurrentRound(session);
            quizSessionRepository.save(session);
        }
        return roundResponse(session);
    }

    public QuizSubmit submitRound(QuizRequest request) {
        if (request == null || request.getSessionId() == null || request.getSessionId().isBlank()) {
            throw new IllegalArgumentException("***SESSION ID ERROR***");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("You didn't answer all the questions");
        }

        QuizSession session = getSession(request.getSessionId());
        if (session.isSubmitted()) {
            throw new IllegalStateException("***SESSION SUBMIT BUG***");
        }

        List<String> questionID = session.getQuestionID();
        if (questionID == null || questionID.isEmpty()) {
            throw new IllegalArgumentException("***ROUND BUG***");
        }
        if (request.getAnswers().size() != questionID.size()) {
            throw new IllegalArgumentException("You didn't answer all the questions");
        }

        Map<String, QuizQuestion> questionById = findQuestionsById(questionID);
        //applyVariants reason explained at the function implementation
        applyVariants(questionById, session.getQuestionVariants(), session.getCorrectAnswer());

        Map<String, Integer> nextLevels = new LinkedHashMap<>();
        for (String subCategory : SUB_CATEGORY_ORDER) {
            Integer currentLevel = session.getNextLvl().get(subCategory);
            if (currentLevel != null && currentLevel == session.getRoundNB()) {
                nextLevels.put(subCategory, currentLevel);
            }
        }

        for (String questionId : questionID) {

            QuizQuestion question = questionById.get(questionId);

            if (question == null) {
                throw new IllegalArgumentException("***QUESTION ID BUG AT*** " + questionId);
            }
            Integer selectedIndex = request.getAnswers().get(questionId);
            session.setTotalQuestions(session.getTotalQuestions() + 1);

            if (selectedIndex == question.getCorrectIndex()) {

                session.setCorrectAnswers(session.getCorrectAnswers() + 1);
                addSectionPoints(session.getSectionScores(), question.getCategory(), questionPoints(question));

                if (question.getDifficulty() < MAX_ROUNDS) {
                    nextLevels.put(question.getSubCategory(), question.getDifficulty() + 1);
                } else {

                    nextLevels.remove(question.getSubCategory());
                }
            } else {

                nextLevels.remove(question.getSubCategory());
            }
        }

        session.setNextLvl(nextLevels);
        session.setRoundNB(session.getRoundNB() + 1); 
        session.setQuestionID(List.of()); //clears the list of ID so they don't carry over to the next round

        if (session.getRoundNB() > MAX_ROUNDS || session.getNextLvl().isEmpty()) {
            return completeQuiz(session);
        }

        generateCurrentRound(session);
        quizSessionRepository.save(session);
        return new QuizSubmit(
                session.getId(),
                session.getNickname(),
                false,
                roundResponse(session),
                session.getTotalQuestions(),
                session.getCorrectAnswers(),
                totalScore(session.getSectionScores()),
                session.getSectionScores(),
                sectionMaxScores(),
                List.of(),
                false);
    }

    private QuizSubmit completeQuiz(QuizSession session) {
        int finalScore = totalScore(session.getSectionScores());
        Map<QuizzCategory, Integer> sectionMaxScores = sectionMaxScores();
        List<BookRecommendation> recommendations = bookRecommendationService.recommendBooks(
                session.getSectionScores(), sectionMaxScores);

        QuizSubmit response = new QuizSubmit(
                session.getId(),
                session.getNickname(),
                true,
                null,
                session.getTotalQuestions(),
                session.getCorrectAnswers(),
                finalScore,
                session.getSectionScores(),
                sectionMaxScores,
                recommendations,
                false);

        attemptResultRepository.save(new FinalResult(
                session.getId(),
                session.getNickname(),
                Instant.now(),
                session.getSectionScores(),
                session.getTotalQuestions(),
                session.getCorrectAnswers(),
                finalScore));

        session.setSubmitted(true);
        quizSessionRepository.save(session);

        boolean emailSent = resultEmailService.sendResult(session.getEmail(), response);
        return new QuizSubmit(
                response.getSessionId(),
                response.getNickname(),
                true,
                null,
                response.getTotalQuestions(),
                response.getCorrectAnswers(),
                response.getFinalScore(),
                response.getSectionScores(),
                response.getSectionMaxScores(),
                response.getBookRecommendations(),
                emailSent);
    }

    private void generateCurrentRound(QuizSession session) {
        List<String> questionIds = new ArrayList<>();
        Map<String, String> questionVariants = new LinkedHashMap<>();
        Map<String, Integer> questionVariantCorrectIndices = new LinkedHashMap<>();

        for (String subCategory : SUB_CATEGORY_ORDER) {
            Integer level = session.getNextLvl().get(subCategory);
            if (level == null || level != session.getRoundNB()) {
                continue;
            }
            List<QuizQuestion> candidates = quizQuestionRepository //in case we want to scale the DB with more question per subcategory and difficulty level
                    .findBySubCategoryAndDifficulty(subCategory, level);
            if (candidates.isEmpty()) {
                throw new IllegalArgumentException(
                        "No question found for " + subCategory + " at level " + level);
            }

            QuizQuestion question = candidates.get(0); 
            // replace above line by QuizQuestion question = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size())); if we scale as per previous comment
            List<QuestionVariant> variants = question.getVariants();

            //pick a variant of the question
            QuestionVariant selected;
            if (variants == null || variants.isEmpty()) {
                selected = new QuestionVariant(question.getContent(), question.getCorrectIndex());
            } else {
                selected = variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
            }

            //store correct answer index and variant for the grading logic
            questionVariants.put(question.getId(), selected.getContent());
            questionVariantCorrectIndices.put(question.getId(), selected.getCorrectIndex());
            questionIds.add(question.getId());
        }
        session.setQuestionID(questionIds);
        session.setQuestionVariants(questionVariants);
        session.setCorrectAnswer(questionVariantCorrectIndices);
    }

    private QuizRound roundResponse(QuizSession session) {
        return new QuizRound(
                session.getId(),
                session.getRoundNB(),
                MAX_ROUNDS,
                questionsInSessionOrder(session.getQuestionID(), session.getQuestionVariants(),
                        session.getCorrectAnswer()),
                session.getSectionScores());
    }

    private List<QuizQuestion> questionsInSessionOrder(List<String> questionID,
            Map<String, String> questionVariants, Map<String, Integer> correctAnswer) {
        Map<String, QuizQuestion> questionById = findQuestionsById(questionID);
        applyVariants(questionById, questionVariants, correctAnswer); //overwrite content and correctIndex objects in memory,
        List<QuizQuestion> questions = new ArrayList<>();             //we lie to the front end basically, needs refactoring by creating separate 
        for (String questionId : questionID) {                        //documents in DB for each variant but didn't have time
            QuizQuestion question = questionById.get(questionId);
            if (question != null) {
                questions.add(question);
            }
        }
        return questions;
    }

    // apply the randomly choosen variant to the question, only exist because i didn't have time to create separate JSON objects for question variants
    private void applyVariants(Map<String, QuizQuestion> questionById,
            Map<String, String> questionVariants, Map<String, Integer> correctAnswer) {
        if (questionVariants == null) {
            return;
        }
        for (Map.Entry<String, String> entry : questionVariants.entrySet()) {
            QuizQuestion q = questionById.get(entry.getKey());
            if (q == null) {
                continue;
            }
            q.setContent(entry.getValue());
            if (correctAnswer != null) {
                Integer ci = correctAnswer.get(entry.getKey());
                if (ci != null) {
                    q.setCorrectIndex(ci);
                }
            }
        }
    }

    private Map<String, QuizQuestion> findQuestionsById(List<String> questionID) {
        Map<String, QuizQuestion> questionById = new LinkedHashMap<>();
        for (QuizQuestion question : quizQuestionRepository.findAllById(questionID)) {
            questionById.put(question.getId(), question);
        }
        return questionById;
    }

    private QuizSession getSession(String sessionId) {
        return quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    private Map<String, Integer> initialLevels() {
        Map<String, Integer> levels = new LinkedHashMap<>();
        for (String subCategory : SUB_CATEGORY_ORDER) {
            levels.put(subCategory, 1);
        }
        return levels;
    }

    private Map<QuizzCategory, Integer> emptySectionScores() {
        Map<QuizzCategory, Integer> scores = new EnumMap<>(QuizzCategory.class);
        for (QuizzCategory category : QuizzCategory.values()) {
            scores.put(category, 0);
        }
        return scores;
    }

    private Map<QuizzCategory, Integer> sectionMaxScores() {
        Map<QuizzCategory, Integer> maxScores = new EnumMap<>(QuizzCategory.class);
        maxScores.put(QuizzCategory.CALCULUS, 30);
        maxScores.put(QuizzCategory.DISCRETE_MATHEMATICS, 30);
        maxScores.put(QuizzCategory.PROGRAMMING_BASICS, 30);
        maxScores.put(QuizzCategory.INTRODUCTION_A_L_INFORMATIQUE, 30);
        return maxScores;
    }

    private int questionPoints(QuizQuestion question) {
        if (question.getDifficulty() == 1) {
            return LEVEL_1_POINTS;
        }
        if (question.getDifficulty() == 2) {
            return LEVEL_2_POINTS;
        }
        return LEVEL_3_POINTS;
    }

    private void addSectionPoints(Map<QuizzCategory, Integer> sectionScores, QuizzCategory category, int points) {
        sectionScores.put(category, sectionScores.getOrDefault(category, 0) + points);
    }

    private int totalScore(Map<QuizzCategory, Integer> sectionScores) {
        int total = 0;
        for (Integer score : sectionScores.values()) {
            total += score;
        }
        return total;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }
}
