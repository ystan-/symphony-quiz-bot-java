package com.symphony.ps.quizbot.repository;

import com.symphony.ps.quizbot.model.Quiz;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<Quiz, String> {
    long countByCreatorAndEnded(long creator, Instant ended);
    Quiz findTopByCreatorAndEnded(long creator, Instant ended);
    List<Quiz> findAllByCreatorOrderByCreatedDesc(long creator, Pageable pageable);
    List<Quiz> findAllByCreatorAndStreamIdOrderByCreatedDesc(long creator, String streamId, Pageable pageable);
}
