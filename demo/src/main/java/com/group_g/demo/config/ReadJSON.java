package com.group_g.demo.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.AssessmentItem;
import com.group_g.demo.model.QuizQuestion;
import com.group_g.demo.model.SeedMetadata;
import com.group_g.demo.repository.LeaderboardRepository;
import com.group_g.demo.repository.AssessmentItemRepository;
import com.group_g.demo.repository.QuizQuestionRepository;
import com.group_g.demo.repository.QuizSessionRepository;
import com.group_g.demo.repository.SeedMetadataRepository;

// loads questions from JSON files into MongoDB when app starts
@Configuration
public class ReadJSON {

    private static final String BANKS_METADATA_ID = "banks";
    private static final List<String> AGREEMENT_OPTIONS_3 = List.of(
            "Disagree",
            "Neutral",
            "Agree");
    private static final List<String> AGREEMENT_OPTIONS_5 = List.of(
            "Strongly disagree",
            "Disagree",
            "Neutral",
            "Agree",
            "Strongly agree");

    private final ObjectMapper objectMapper;

    public ReadJSON(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    CommandLineRunner seedData(AssessmentItemRepository AssessmentItemRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizSessionRepository quizSessionRepository,
            LeaderboardRepository attemptResultRepository,
            SeedMetadataRepository seedMetadataRepository) {
        return args -> {
            // load JSON at the start
            AssessmentBank assessmentBank = readBank("banks/assessment.json", AssessmentBank.class);
            McqBank mcqBank = readBank("banks/mcq.json", McqBank.class);
            int targetVersion = checkSeedVersion(assessmentBank, mcqBank);

            int currentVersion = seedMetadataRepository.findById(BANKS_METADATA_ID)
                    .map(SeedMetadata::getVersion)
                    .orElse(-1);
            if (currentVersion == targetVersion) {
                return;
            }

            // wipes previously saved JSON data if version changed and save the old one
            AssessmentItemRepository.deleteAll();
            quizQuestionRepository.deleteAll();
            quizSessionRepository.deleteAll();
            attemptResultRepository.deleteAll();

            AssessmentItemRepository.saveAll(AssessmentItemsFromBank(assessmentBank));
            quizQuestionRepository.saveAll(quizQuestionsFromBank(mcqBank));
            seedMetadataRepository.save(new SeedMetadata(BANKS_METADATA_ID, targetVersion));
        };
    }

    private List<AssessmentItem> AssessmentItemsFromBank(AssessmentBank bank) {
        // convert JSON items to database objects
        List<AssessmentItem> list = new ArrayList<>();
        for (AssessmentItemInput input : bank.items()) {
            AssessmentItem item = new AssessmentItem(
                    input.content(),
                    input.category(),
                    input.orderIndex(),
                    answerAmount(input.amount()));
            list.add(item);
        }
        return list;
    }

    private List<QuizQuestion> quizQuestionsFromBank(McqBank bank) {
        // convert JSON items to database objects
        List<QuizQuestion> list = new ArrayList<>();
        for (McqQuestionInput input : bank.questions()) {
            List<String> tags = input.tags();
            if (tags == null) {
                tags = List.of();
            }
            QuizQuestion question = new QuizQuestion(
                    input.content(),
                    input.category(),
                    input.difficulty(),
                    input.options(),
                    input.correctIndex(),
                    1.0,
                    tags,
                    0);
            list.add(question);
        }
        return list;
    }

    private int checkSeedVersion(AssessmentBank assessmentBank, McqBank mcqBank) {
        // both JSON files need same version so we know they belong together
        int assessmentVersion = assessmentBank.version();
        int mcqVersion = mcqBank.version();
        if (assessmentVersion != mcqVersion) {
            throw new IllegalStateException(
                    "seed match error between JSON files, assessment= " + assessmentVersion + ", mcq="
                            + mcqVersion);
        }
        return assessmentVersion;
    }

    private <T> T readBank(String path, Class<T> type) {
        // reads one JSON file from resources folder
        try {
            return objectMapper.readValue(new ClassPathResource(path).getInputStream(), type);
        } catch (IOException e) {
            throw new IllegalStateException("bank resource error " + path, e);
        }
    }

    private List<String> answerAmount(Integer amount) {
        // logic for amount of answer listed
        if (amount == null || amount == 5) {
            return AGREEMENT_OPTIONS_5;
        }
        if (amount == 3) {
            return AGREEMENT_OPTIONS_3;
        }
        throw new IllegalStateException(
                "answer amount error");
    }

    private record AssessmentBank(int version, List<AssessmentItemInput> items) {
    }

    private record AssessmentItemInput(int orderIndex, QuizzCategory category, String content, Integer amount) {
    }

    private record McqBank(int version, List<McqQuestionInput> questions) {
    }

    private record McqQuestionInput(
            QuizzCategory category,
            int difficulty,
            String content,
            List<String> options,
            int correctIndex,
            List<String> tags) {
    }
}
