package com.symphony.ps.quizbot.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private int timeLimit;
    private String questionText;
    private List<String> answers;
    private String correctAnswer;
}
