package com.group_g.demo.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import java.util.LinkedHashMap;
import com.group_g.demo.dto.Leaderboard;
import com.group_g.demo.dto.AssessmentRequest;
import com.group_g.demo.dto.AssessmentSubmit;
import com.group_g.demo.dto.QuizRequest;
import com.group_g.demo.dto.QuizSubmit;
import com.group_g.demo.model.FinalResult;
import com.group_g.demo.model.AssessmentItem;
import com.group_g.demo.model.QuizQuestion;
import com.group_g.demo.repository.LeaderboardRepository;
import com.group_g.demo.service.AssessmentService;
import com.group_g.demo.service.QuizService;

//this class has all the API link used by the .html file
//learned through use of LLM, youtube tutorials and  spring/RestAPI documentation
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class API {

    private static final int MAX_LEADERBOARD_LIMIT = 20;

    private final AssessmentService AssessmentService;
    private final QuizService quizService;
    private final LeaderboardRepository attemptResultRepository;

    public API(AssessmentService AssessmentService, QuizService quizService,
            LeaderboardRepository attemptResultRepository) {
        this.AssessmentService = AssessmentService;
        this.quizService = quizService;
        this.attemptResultRepository = attemptResultRepository;
    }

    @GetMapping("/assessment/questions")
    public ResponseEntity<?> AssessmentQuestions() {
        // sends assessment questions to the page
        try {
            return ResponseEntity.ok(AssessmentService.getQuestions());
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/assessment/submit")
    public ResponseEntity<?> submitAssessment(@RequestBody AssessmentRequest request) {
        // get assessment answers, compute score and create quiz 
        try {
            checkAssessmentRequest(request);
            AssessmentSubmit response = AssessmentService.submit(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping("/quiz")
    public ResponseEntity<?> quiz(@RequestParam String sessionId) {
        // gets quiz questions
        try {
            List<QuizQuestion> questions = quizService.getQuiz(sessionId);
            List<Map<String, Object>> pageQuestions = new ArrayList<>();
            for (QuizQuestion question : questions) {
                pageQuestions.add(quizQuestionFormat(question));
            }
            return ResponseEntity.ok(pageQuestions);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizRequest request) {
        // get answer, return scoee
        try {
            checkQuizRequest(request);
            QuizSubmit response = quizService.submitQuiz(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> leaderboard(@RequestParam(defaultValue = "10") int limit) {
        // gets best scores for the leaderboard table
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

    private void checkAssessmentRequest(AssessmentRequest request) {
        // to force entering a nickname, ***might refactor to offer choice or not to get ranked***
        if (request == null || request.getNickname() == null || request.getNickname().isBlank()) {
            throw new IllegalArgumentException("For now, you need to enter a Nickname, avoid using your Personnal name");
        }
        // basic check if all answers have been answered
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("You didn't answer all the questions");
        }
    }

    private void checkQuizRequest(QuizRequest request) {
        // session ID check, ***need to check if it is really needed?***
        if (request == null || request.getSessionId() == null || request.getSessionId().isBlank()) {
            throw new IllegalArgumentException("Session ID error. please refer to the IT guy");
        }
        // basic check if all answers have been answered
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("Answers are required");
        }
    }

    private Map<String, Object> quizQuestionFormat(QuizQuestion question) {
        // removes answers and extra fields before sending question to frontend
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", question.getId());
        view.put("content", question.getContent());
        view.put("category", question.getCategory());
        view.put("difficulty", question.getDifficulty());
        view.put("options", question.getOptions());
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
        // returns errors in the same format everywhere
        return ResponseEntity.badRequest().body(error(message));
    }

    private Map<String, String> error(String message) {
        return Map.of("error", message);
    }
}
