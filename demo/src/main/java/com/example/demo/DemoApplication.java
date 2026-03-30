package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("\n🚀 SPRINT 1 IS LIVE!");
	}

	@Bean
	CommandLineRunner runner(QuestionRepository repository) {
		return args -> {
			// This checks if the database is empty before adding a test question
			if (repository.count() == 0) {
				Question q = new Question();
				q.setText("Which language is used for Spring Boot?");
				q.setOptions(List.of("Java", "Python", "C++", "Ruby"));
				q.setCorrectAnswerIndex(0);
				repository.save(q);
				System.out.println("✅ Test question added to MongoDB!");
			}
		};
	}
}