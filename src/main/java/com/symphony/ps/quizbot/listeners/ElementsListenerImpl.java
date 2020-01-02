package com.symphony.ps.quizbot.listeners;

import com.symphony.ps.quizbot.QuizBot;
import com.symphony.ps.quizbot.services.QuizService;
import listeners.ElementsListener;
import lombok.extern.slf4j.Slf4j;
import model.User;
import model.events.SymphonyElementsAction;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ElementsListenerImpl implements ElementsListener {
    private final QuizService quizService;

    public ElementsListenerImpl(QuizService quizService) {
        this.quizService = quizService;
    }

    public void onElementsAction(User initiator, SymphonyElementsAction action) {
        String formId = action.getFormId();
        if (formId.startsWith("quiz-create-form")) {
            quizService.handleCreateQuiz(initiator, action);
        } else if (formId.startsWith("quiz-launch-form")) {
            quizService.handleLaunchQuiz(initiator, action);
        } else if (formId.matches("quiz\\-blast\\-form\\-[\\w\\d]+")) {
            quizService.handleSubmitVote(initiator, action);
        } else {
            QuizBot.sendMessage(action.getStreamId(), "Sorry, I do not understand this form submission");
        }
    }
}
