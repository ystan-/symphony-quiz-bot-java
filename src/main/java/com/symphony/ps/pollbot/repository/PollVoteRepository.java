package com.symphony.ps.pollbot.repository;

import com.symphony.ps.pollbot.model.PollVote;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PollVoteRepository extends MongoRepository<PollVote, String> {
    List<PollVote> findAllByPollId(String pollId);
    PollVote findTopByPollIdAndUserId(String pollId, long userId);
}
