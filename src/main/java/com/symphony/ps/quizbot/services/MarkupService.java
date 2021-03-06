package com.symphony.ps.quizbot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.ps.quizbot.model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MarkupService {
    private static ObjectMapper mapper = new ObjectMapper();
    static String createTemplate = loadTemplate("/create-form.ftl");
    static String blastTemplate = loadTemplate("/blast-form.ftl");
    static String resultsTemplate = loadTemplate("/vote-results-form.ftl");
    static String leaderboardTemplate = loadTemplate("/leaderboard-results-form.ftl");
    static String adminTemplate = loadTemplate("/admin-form.ftl");

    private static String loadTemplate(String fileName) {
        InputStream stream = MarkupService.class.getResourceAsStream(fileName);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        catch (IOException e) {
            log.error("Unable to load template for {}", fileName);
            return null;
        }
    }

    static String getCreateData(String quizId, String targetStreamId, int count, List<Integer> timeLimits) {
        return wrapData(new QuizCreateData(quizId, targetStreamId, count, timeLimits));
    }

    static String getBlastData(Quiz quiz, int questionIndex, String label) {
        QuizQuestion quizQuestion = quiz.getQuestions().get(questionIndex);
        return wrapData(new QuizBlastData(
            quiz.getId(),
            questionIndex,
            quizQuestion.getTimeLimit(),
            quizQuestion.getQuestionText(),
            quizQuestion.getAnswers(),
            label
        ));
    }

    static String wrapData(QuizData data) {
        Map<String, QuizData> map = new HashMap<>();
        map.put("quiz", data);

        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Unable to wrap data object: {}", e.getMessage());
            return null;
        }
    }
}
