package com.symphony.ps.quizbot.repository;

import com.symphony.ps.quizbot.model.QuizAnswer;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizAnswerRepository extends MongoRepository<QuizAnswer, String> {
    int countAllByQuizIdAndQuestionIndex(String quizId, int questionIndex);
    List<QuizAnswer> findAllByQuizIdAndQuestionIndex(String quizId, int questionIndex);
    QuizAnswer findTopByQuizIdAndUserIdAndQuestionIndex(String quizId, long userId, int questionIndex);
}
