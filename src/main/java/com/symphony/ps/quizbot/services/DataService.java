package com.symphony.ps.quizbot.services;

import com.symphony.ps.quizbot.model.*;
import com.symphony.ps.quizbot.repository.QuizRepository;
import com.symphony.ps.quizbot.repository.QuizAnswerRepository;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

    Quiz saveQuiz(Quiz quiz) {
        quiz = quizRepository.save(quiz);
        log.info("Quiz added to database: {}", quiz.toString());
        return quiz;
    }

    Quiz nextQuestion(long userId) {
        Quiz quiz = getActiveQuiz(userId);
        if (quiz.getCurrentQuestionIndex() + 1 == quiz.getQuestions().size()) {
            quiz.setEnded(Instant.now());
        } else {
            quiz.nextQuestion();
        }
        return quizRepository.save(quiz);
    }

    Quiz getQuiz(String id) {
        return quizRepository.findById(id).orElse(null);
    }

    Quiz getActiveQuiz(long userId) {
        return quizRepository.findTopByCreatorAndEnded(userId, null);
    }

    List<QuizAnswer> getAnswers(String quizId, int questionIndex) {
        return quizAnswerRepository.findAllByQuizIdAndQuestionIndex(quizId, questionIndex);
    }

    int getAnswerCount(String quizId, int questionIndex) {
        return quizAnswerRepository.countAllByQuizIdAndQuestionIndex(quizId, questionIndex);
    }

    List<VoteEntry> getVotes(String quizId, int questionIndex) {
        return mongoTemplate
            .aggregate(newAggregation(
                match(Criteria.where("quizId").is(quizId).and("questionIndex").is(questionIndex)),
                group("answer").count().as("count"),
                project("count").and("answer").previousOperation(),
                sort(new Sort(Sort.Direction.DESC, "count"))
            ), "quizAnswer", VoteEntry.class)
            .getMappedResults();
    }

    List<LeaderboardEntry> getLeaderboard(String quizId) {
        return mongoTemplate
            .aggregate(newAggregation(
                match(Criteria.where("quizId").is(quizId).and("correct").is(true)),
                group("userId").count().as("count"),
                project("count").and("userId").previousOperation(),
                sort(new Sort(Sort.Direction.DESC, "count"))
            ), "quizAnswer", LeaderboardEntry.class)
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

    boolean hasVoted(long userId, String quizId, int questionIndex) {
        return quizAnswerRepository.findTopByQuizIdAndUserIdAndQuestionIndex(quizId, userId, questionIndex) != null;
    }

    void changeVote(long userId, String quizId, int questionIndex, String answer) {
        QuizAnswer vote = quizAnswerRepository.findTopByQuizIdAndUserIdAndQuestionIndex(quizId, userId, questionIndex);
        vote.setAnswer(answer);
        quizAnswerRepository.save(vote);
    }
}
