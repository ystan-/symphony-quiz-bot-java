package com.symphony.ps.quizbot.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class Votes extends QuizData {
    private String question;
    private long creatorId;
    private String label;
    private List<VoteEntry> results;

    public String getCreatorId() {
        return creatorId + "";
    }
}
