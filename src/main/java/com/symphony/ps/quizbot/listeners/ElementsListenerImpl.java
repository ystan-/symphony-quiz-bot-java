package com.symphony.ps.quizbot.listeners;

import com.symphony.ps.quizbot.QuizBot;
import com.symphony.ps.quizbot.model.Quiz;
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
            Quiz quiz = null;
            if (!action.getFormValues().get("question").toString().trim().isEmpty()) {
                quiz = quizService.handleCreateQuiz(initiator, action);
            }
            if (action.getFormValues().get("action").toString().equals("launchQuiz")) {
                String quizId = quiz != null ? quiz.getId() : action.getFormId().substring(17);
                quizService.handleLaunchQuiz(initiator.getUserId(), initiator.getDisplayName(), quizId);
            } else if (quiz != null) {
                quizService.handleSendCreateForm(quiz.getId(), quiz.getStreamId(), initiator);
            }
        } else if (formId.startsWith("quiz-blast-form")) {
            quizService.handleSubmitVote(initiator, action);
        } else {
            QuizBot.sendMessage(action.getStreamId(), "Sorry, I do not understand this form submission");
        }
    }
}
