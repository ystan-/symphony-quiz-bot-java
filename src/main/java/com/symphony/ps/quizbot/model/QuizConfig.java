package com.symphony.ps.quizbot.model;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizConfig {
    private int options;
    private List<Integer> timeLimits;
    private boolean targetStream;

    public QuizConfig() {
        options = 4;
        timeLimits = Arrays.asList(0, 30, 60);
    }
}
