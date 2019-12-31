package com.symphony.ps.quizbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswer {
    private String id;
    private String quizId;
    private String answer;
    private long userId;
}
