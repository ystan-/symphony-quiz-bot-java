package com.symphony.ps.quizbot.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class QuizBlastData extends QuizData {
    private String quizId;
    private int questionIndex;
    private int timeLimit;
    private String question;
    private List<String> answers;
    private String label;
}
