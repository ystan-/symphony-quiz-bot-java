package com.symphony.ps.quizbot.listeners;

import com.symphony.ps.quizbot.services.QuizService;
import listeners.IMListener;
import lombok.extern.slf4j.Slf4j;
import model.InboundMessage;
import model.Stream;
import model.StreamTypes;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IMListenerImpl implements IMListener {
    private final QuizService quizService;

    public IMListenerImpl(QuizService quizService) {
        this.quizService = quizService;
    }

    public void onIMMessage(InboundMessage msg) {
        quizService.handleIncomingMessage(msg);
    }

    public void onIMCreated(Stream stream) {}
}
