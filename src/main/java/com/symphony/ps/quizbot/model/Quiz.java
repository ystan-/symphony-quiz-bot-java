package com.symphony.ps.quizbot.model;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    private String id;
    private Instant created;
    private Instant ended;
    private long creator;
    private String streamId;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex;

    public void nextQuestion() {
        this.currentQuestionIndex++;
    }

    public QuizQuestion getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }
}
