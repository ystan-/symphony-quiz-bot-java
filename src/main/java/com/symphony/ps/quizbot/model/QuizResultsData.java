package com.symphony.ps.quizbot.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizResultsData extends QuizData {
    private String question;
    private long creatorId;
    private List<QuizResult> results;

    public String getCreatorId() {
        return creatorId + "";
    }
}
