package com.group_g.demo.service;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.group_g.demo.dto.BookRecommendation;
import com.group_g.demo.dto.QuizSubmit;

@Service
public class ResultEmailService {
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean enabled;
    private final String fromAddress;

    public ResultEmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${pathfinder.results.email.enabled:false}") boolean enabled,
            @Value("${spring.mail.username:no-reply@pathfinder.local}") String fromAddress) {
        this.mailSenderProvider = mailSenderProvider;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
    }

    public boolean sendResult(String recipient, QuizSubmit result) {
        if (!enabled || recipient == null || recipient.isBlank()) {
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipient);
        message.setSubject("Your CS Quiz result");
        message.setText(buildEmailBody(result));
        try {
            mailSender.send(message);
            return true;
        } catch (MailException | IllegalStateException ex) {
            return false;
        }
    }

    private String buildEmailBody(QuizSubmit result) {
        StringBuilder body = new StringBuilder();
        body.append("Hi ").append(result.getNickname()).append(",\n\n");
        body.append("Your quiz score is ")
                .append(result.getFinalScore())
                .append("% (")
                .append(result.getCorrectAnswers())
                .append("/")
                .append(result.getTotalQuestions())
                .append(").\n\n");
        body.append("Recommended books:\n");
        appendRecommendations(body, result.getBookRecommendations());
        return body.toString();
    }

    private void appendRecommendations(StringBuilder body, List<BookRecommendation> recommendations) {
        for (BookRecommendation recommendation : recommendations) {
            body.append("- ")
                    .append(recommendation.getTitle())
                    .append(" by ")
                    .append(recommendation.getAuthor())
                    .append(": ")
                    .append(recommendation.getReason())
                    .append("\n");
        }
    }
}
