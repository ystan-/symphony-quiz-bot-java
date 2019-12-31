package com.symphony.ps.quizbot.services;

import com.symphony.ps.quizbot.model.*;
import com.symphony.ps.quizbot.repository.QuizRepository;
import com.symphony.ps.quizbot.repository.QuizAnswerRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
public class DataService {
    private final MongoTemplate mongoTemplate;
    private final QuizRepository quizRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    public DataService(MongoTemplate mongoTemplate, QuizRepository quizRepository, QuizAnswerRepository quizAnswerRepository) {
        this.mongoTemplate = mongoTemplate;
        this.quizRepository = quizRepository;
        this.quizAnswerRepository = quizAnswerRepository;
    }

    boolean hasActiveQuiz(long userId) {
        return 1L == quizRepository.countByCreatorAndEnded(userId, null);
    }

    void createQuiz(Quiz quiz) {
        quizRepository.save(quiz);
        log.info("Quiz added to database: {}", quiz.toString());
    }

    void nextQuestion(long userId) {
        Quiz quiz = getActiveQuiz(userId);
        quiz.setEnded(Instant.now());
        quizRepository.save(quiz);
    }

    Quiz getQuiz(String id) {
        return quizRepository.findById(id).orElse(null);
    }

    Quiz getActiveQuiz(long userId) {
        return quizRepository.findTopByCreatorAndEnded(userId, null);
    }

    private List<Quiz> getLastTenQuizzes(long userId) {
        List<Quiz> quizzes = quizRepository
            .findAllByCreatorOrderByCreatedDesc(userId, PageRequest.of(0, 10));
        quizzes.sort(Comparator.comparing(Quiz::getCreated));
        return quizzes;
    }

    private List<Quiz> getLastTenQuizzes(long userId, String streamId) {
        List<Quiz> quizzes = quizRepository
            .findAllByCreatorAndStreamIdOrderByCreatedDesc(userId, streamId, PageRequest.of(0, 10));
        quizzes.sort(Comparator.comparing(Quiz::getCreated));
        return quizzes;
    }

    List<QuizAnswer> getVotes(String quizId) {
        return quizAnswerRepository.findAllByQuizId(quizId);
    }

    List<QuizResult> getQuizResults(String quizId) {
        return mongoTemplate
            .aggregate(newAggregation(
                match(new Criteria("quizId").is(quizId)),
                group("answer").count().as("count"),
                project("count").and("answer").previousOperation(),
                sort(new Sort(Sort.Direction.DESC, "count"))
            ), "QuizAnswer", QuizResult.class)
            .getMappedResults();
    }

    void createVote(QuizAnswer vote) {
        quizAnswerRepository.save(vote);
        log.info("Vote added to database: {}", vote.toString());
    }

    void createVotes(List<QuizAnswer> votes) {
        quizAnswerRepository.saveAll(votes);
        log.info("Rigged votes added to database");
    }

    boolean hasVoted(long userId, String quizId) {
        return quizAnswerRepository.findTopByQuizIdAndUserId(quizId, userId) != null;
    }

    void changeVote(long userId, String quizId, String answer) {
        QuizAnswer vote = quizAnswerRepository.findTopByQuizIdAndUserId(quizId, userId);
        vote.setAnswer(answer);
        quizAnswerRepository.save(vote);
    }
}
