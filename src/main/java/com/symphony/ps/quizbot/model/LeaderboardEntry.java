package com.symphony.ps.quizbot.model;

import java.util.Objects;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry extends QuizData {
    private String quizId;
    private long userId;
    private String displayName;
    private long count;
    private int width;

    @Override
    public String toString() {
        return "{" + userId + "=" + count + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LeaderboardEntry that = (LeaderboardEntry) o;
        return userId == that.userId &&
            Objects.equals(quizId, that.quizId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, userId);
    }
}
