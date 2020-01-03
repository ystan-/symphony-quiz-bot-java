package com.symphony.ps.quizbot.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class Leaderboard extends QuizData {
    private long creatorId;
    private String label;
    private List<LeaderboardEntry> results;
}
