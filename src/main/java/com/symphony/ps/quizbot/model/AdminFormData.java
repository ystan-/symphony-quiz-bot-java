package com.symphony.ps.quizbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class AdminFormData extends QuizData {
    private int timeLimit;
    private boolean finalQuestion;
    private String questionLabel;
}
