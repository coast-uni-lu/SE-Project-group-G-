package com.group_g.demo.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group_g.demo.model.QuestionVariant;
import com.group_g.demo.model.QuizzCategory;
import com.group_g.demo.model.QuizQuestion;
import com.group_g.demo.model.SeedMetadata;
import com.group_g.demo.repository.LeaderboardRepository;
import com.group_g.demo.repository.QuizQuestionRepository;
import com.group_g.demo.repository.QuizSessionRepository;
import com.group_g.demo.repository.SeedMetadataRepository;

// loads question banks from JSON into MongoDB when app starts
@Configuration
public class ReadJSON {

    private static final String BANKS_METADATA_ID = "banks";

    private final ObjectMapper objectMapper;

    public ReadJSON(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    CommandLineRunner seedData(QuizQuestionRepository quizQuestionRepository,
            QuizSessionRepository quizSessionRepository,
            LeaderboardRepository attemptResultRepository,
            SeedMetadataRepository seedMetadataRepository) {
        return args -> {
            McqBank mcqBank = readBank("banks/mcq.json", McqBank.class);
            int targetVersion = mcqBank.version();

            int currentVersion = seedMetadataRepository.findById(BANKS_METADATA_ID)
                    .map(SeedMetadata::getVersion)
                    .orElse(-1);
            if (currentVersion == targetVersion) {
                return;
            }

            quizQuestionRepository.deleteAll();
            quizSessionRepository.deleteAll();
            attemptResultRepository.deleteAll();

            quizQuestionRepository.saveAll(quizQuestionsFromBank(mcqBank));
            seedMetadataRepository.save(new SeedMetadata(BANKS_METADATA_ID, targetVersion));
        };
    }

    private List<QuizQuestion> quizQuestionsFromBank(McqBank bank) {
        List<QuizQuestion> list = new ArrayList<>();
        for (McqQuestionInput input : bank.questions()) {
            List<String> tags = input.tags() == null ? List.of() : input.tags();
            List<QuestionVariant> variants = new ArrayList<>();
            if (input.variants() != null) {
                for (McqVariantInput v : input.variants()) {
                    variants.add(new QuestionVariant(v.content(), v.correctIndex()));
                }
            }
            //set values to 0, weird workarround to get it to work, we override the set values when choosing a question from the pool
            String content = variants.isEmpty() ? "" : variants.get(0).getContent();
            int correctIndex = variants.isEmpty() ? 0 : variants.get(0).getCorrectIndex();
            QuizQuestion question = new QuizQuestion(
                    content,
                    input.category(),
                    input.subCategory(),
                    input.difficulty(),
                    input.options(),
                    correctIndex,
                    1.0,
                    tags,
                    variants);
            list.add(question);
        }
        return list;
    }

    private <T> T readBank(String path, Class<T> type) {
        try {
            return objectMapper.readValue(new ClassPathResource(path).getInputStream(), type);
        } catch (IOException e) {
            throw new IllegalStateException("bank resource error " + path, e);
        }
    }

    private record McqBank(int version, List<McqQuestionInput> questions) {
    }

    //mirrors a variant question, because the index for the correct answer is different for each variants
    private record McqVariantInput(String content, int correctIndex) {
    }

    //correctIndex stored inside each variant instead now
    private record McqQuestionInput(
            QuizzCategory category,
            String subCategory,
            int difficulty,
            List<McqVariantInput> variants,
            List<String> options,
            List<String> tags) {
    }
}
