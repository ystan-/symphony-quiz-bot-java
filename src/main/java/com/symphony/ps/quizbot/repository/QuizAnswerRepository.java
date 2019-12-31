package com.symphony.ps.quizbot.repository;

import com.symphony.ps.quizbot.model.QuizAnswer;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizAnswerRepository extends MongoRepository<QuizAnswer, String> {
    List<QuizAnswer> findAllByQuizId(String quizId);
    QuizAnswer findTopByQuizIdAndUserId(String quizId, long userId);
}
