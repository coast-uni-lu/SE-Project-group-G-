package com.group_g.demo.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group_g.demo.dto.Leaderboard;
import com.group_g.demo.dto.QuizRequest;
import com.group_g.demo.dto.QuizRound;
import com.group_g.demo.dto.QuizStartRequest;
import com.group_g.demo.dto.QuizSubmit;
import com.group_g.demo.model.FinalResult;
import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.QuizQuestion;
import com.group_g.demo.repository.LeaderboardRepository;
import com.group_g.demo.service.QuizService;

// API links used by the static HTML page
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class API {

    private static final int MAX_LEADERBOARD_LIMIT = 20;

    private final QuizService quizService;
    private final LeaderboardRepository attemptResultRepository;

    public API(QuizService quizService, LeaderboardRepository attemptResultRepository) {
        this.quizService = quizService;
        this.attemptResultRepository = attemptResultRepository;
    }

    @PostMapping("/quiz/start")
    public ResponseEntity<?> startQuiz(@RequestBody QuizStartRequest request) {
        try {
            return ResponseEntity.ok(quizRoundFormat(quizService.startQuiz(request)));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping("/quiz/round")
    public ResponseEntity<?> currentRound(@RequestParam String sessionId) {
        try {
            return ResponseEntity.ok(quizRoundFormat(quizService.getRoundNB(sessionId)));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/quiz/round/submit")
    public ResponseEntity<?> submitRound(@RequestBody QuizRequest request) {
        try {
            return ResponseEntity.ok(quizSubmitFormat(quizService.submitRound(request)));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> leaderboard(@RequestParam(defaultValue = "10") int limit) {
        try {
            int safeLimit = Math.max(1, Math.min(limit, MAX_LEADERBOARD_LIMIT));
            List<FinalResult> results = attemptResultRepository
                    .findByOrderByFinalScoreDescTimestampAsc(PageRequest.of(0, safeLimit));
            List<Leaderboard> entries = new ArrayList<>();
            for (FinalResult result : results) {
                entries.add(leaderboardRow(result));
            }
            return ResponseEntity.ok(entries);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    private Map<String, Object> quizSubmitFormat(QuizSubmit submit) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("sessionId", submit.getSessionId());
        view.put("nickname", submit.getNickname());
        view.put("completed", submit.isCompleted());
        view.put("nextRound", submit.getNextRound() == null ? null : quizRoundFormat(submit.getNextRound()));
        view.put("totalQuestions", submit.getTotalQuestions());
        view.put("correctAnswers", submit.getCorrectAnswers());
        view.put("finalScore", submit.getFinalScore());
        view.put("sectionScores", categoryMapFormat(submit.getSectionScores()));
        view.put("sectionMaxScores", categoryMapFormat(submit.getSectionMaxScores()));
        view.put("bookRecommendations", submit.getBookRecommendations());
        view.put("emailSent", submit.isEmailSent());
        return view;
    }

    private Map<String, Object> quizRoundFormat(QuizRound round) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("sessionId", round.getSessionId());
        view.put("round", round.getRound());
        view.put("maxRounds", round.getMaxRounds());
        view.put("sectionScores", categoryMapFormat(round.getSectionScores()));
        List<Map<String, Object>> questions = new ArrayList<>();
        for (QuizQuestion question : round.getQuestions()) {
            questions.add(quizQuestionFormat(question));
        }
        view.put("questions", questions);
        return view;
    }

    private Map<String, Object> quizQuestionFormat(QuizQuestion question) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", question.getId());
        view.put("content", question.getContent());
        view.put("category", question.getCategory());
        view.put("categoryLabel", question.getCategory().getDisplayName());
        view.put("subCategory", question.getSubCategory());
        view.put("difficulty", question.getDifficulty());
        view.put("options", question.getOptions());
        return view;
    }

    private Map<String, Integer> categoryMapFormat(Map<QuizzCategory, Integer> categoryMap) {
        Map<String, Integer> view = new LinkedHashMap<>();
        if (categoryMap == null) {
            return view;
        }
        for (QuizzCategory category : QuizzCategory.values()) {
            view.put(category.getDisplayName(), categoryMap.getOrDefault(category, 0));
        }
        return view;
    }

    private Leaderboard leaderboardRow(FinalResult result) {
        return new Leaderboard(
                result.getNickname(),
                result.getFinalScore(),
                result.getCorrectAnswers(),
                result.getTotalQuestions(),
                result.getTimestamp());
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(error(message));
    }

    private Map<String, String> error(String message) {
        return Map.of("error", message);
    }
}
