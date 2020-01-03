package com.symphony.ps.quizbot.model;

import java.util.Objects;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteEntry extends QuizData {
    private String quizId;
    private String answer;
    private long count;
    private int width;
    private boolean correct;

    public VoteEntry(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "{" + answer + "=" + count + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof VoteEntry)) { return false; }
        VoteEntry that = (VoteEntry) o;
        return answer.equals(that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer);
    }
}
