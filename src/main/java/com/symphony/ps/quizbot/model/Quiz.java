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
    private int timeLimit;
    private long creator;
    private String streamId;
    private String questionText;
    private List<String> answers;
    private String correctAnswer;
}
