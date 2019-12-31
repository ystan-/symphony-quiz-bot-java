package com.symphony.ps.quizbot.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class QuizCreateData extends QuizData {
    private boolean showPersonSelector;
    private String targetStreamId;
    private int count;
    private List<Integer> timeLimits;
}
