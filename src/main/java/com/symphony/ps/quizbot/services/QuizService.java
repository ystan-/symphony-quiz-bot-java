package com.symphony.ps.quizbot.services;

import com.symphony.ps.quizbot.QuizBot;
import com.symphony.ps.quizbot.model.*;
import exceptions.ForbiddenException;
import exceptions.SymClientException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import model.*;
import model.events.SymphonyElementsAction;
import org.springframework.stereotype.Service;
import utils.MessageUtils;

@Slf4j
@Service
public class QuizService {
    private final DataService dataService;

    public QuizService(DataService dataService) {
        this.dataService = dataService;
    }

    private static final String helpML = "<ul>" +
        "<li><b>/quiz</b>: Get a create quiz form</li>" +
        "<li><b>/endquiz</b>: End your active quiz</li></ul>";

    public void handleIncomingMessage(InboundMessage msg) {
        long userId = msg.getUser().getUserId();
        String displayName = msg.getUser().getDisplayName();
        String streamId = msg.getStream().getStreamId();
        String msgText = msg.getMessageText();
        if (msgText == null) {
            log.info("Ignoring message as message text cannot be parsed");
            return;
        }
        String[] msgParts = msgText.trim().toLowerCase().split(" ", 2);

        switch (msgParts[0]) {
            case "/help":
                String helpMLForStream = helpML.replace("</li></ul>", " for this room</li></ul>");
                QuizBot.sendMessage(streamId, helpMLForStream);
                break;

            case "/quiz":
                handleSendCreateForm("", streamId, msg.getUser());
                break;

            case "/rigquiz":
                handleRigQuiz(streamId, userId, displayName);
                break;

            case "/next":
                handleNextQuestion(streamId, userId, displayName);
                break;

            default:
        }
    }

    private static QuizConfig parseConfigInput(String streamId, String[] inputs) {
        int options = 4;
        List<Integer> timeLimits = Arrays.asList(0, 2, 5);
        boolean targetStream = false;

        for (String input : inputs) {
            if (input.matches("^\\d+$")) {
                options = Integer.parseInt(input);
                if (options < 2 || options > 10) {
                    QuizBot.sendMessage(streamId, "Number of options must be between 2 and 10");
                    return null;
                }
            } else if (input.matches("^\\d+(,\\d+)+$")) {
                timeLimits = Arrays.stream(input.split(","))
                    .map(Integer::parseInt)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
                if (timeLimits.size() > 10) {
                    QuizBot.sendMessage(streamId, "Number of time limits should be 10 or lower");
                    return null;
                }
            } else if (input.equalsIgnoreCase("room")) {
                targetStream = true;
            }
        }
        return new QuizConfig(options, timeLimits, targetStream);
    }

    private boolean userHasConflictingQuiz(User user, String quizId) {
        Quiz quiz = dataService.getActiveQuiz(user.getUserId());
        if (quiz != null && !quiz.getId().equalsIgnoreCase(quizId)) {
            String msg = String.format("<mention uid=\"%d\"/> You are attempting to add to an invalid quiz. Use <b>/endquiz</b>" +
                " to end it before starting a new one", user.getUserId());
            QuizBot.sendMessage(QuizBot.getImStreamId(user.getUserId()), msg);
            log.info("User {} has an existing active quiz. Refusing to create a new one.", user.getDisplayName());
            return true;
        }
        return false;
    }

    private void handleSendCreateForm(String quizId, String streamId, User user) {
        log.info("Get new quiz form requested by {}", user.getDisplayName());
        QuizConfig quizConfig = new QuizConfig();
        String createML = MarkupService.createTemplate;
        String data = MarkupService.getCreateData(
            quizId,
            streamId,
            quizConfig.getOptions(),
            quizConfig.getTimeLimits()
        );
        String imStreamId = QuizBot.getImStreamId(user.getUserId());
        QuizBot.sendMessage(imStreamId, createML, data);

        log.info("New quiz form sent to stream {}", imStreamId);
    }

    @SuppressWarnings("unchecked")
    public void handleCreateQuiz(User initiator, SymphonyElementsAction action) {
        Map<String, Object> formValues = action.getFormValues();

        String quizId = action.getFormId().substring(17);

        // Reject quiz creation if an active one exists
        if (userHasConflictingQuiz(initiator, quizId)) {
            return;
        }

        String verb = quizId.isEmpty() ? "New quiz" : "Add question";
        log.info("{} by {} creation in progress: {}", verb, initiator.getDisplayName(), formValues.toString());

        // Collate options
        Map<String, String> answersMap = new HashMap<>();
        formValues.entrySet().stream()
            .filter(k -> k.getKey().startsWith("option"))
            .map(entry -> MessageUtils.escapeText(entry.getValue().toString().trim()))
            .filter(answer -> !answer.isEmpty())
            .forEach(answer -> answersMap.putIfAbsent(answer.toLowerCase(), answer));
        List<String> answers = new ArrayList<>(answersMap.values());
        String answer = MessageUtils.escapeText(formValues.get(formValues.get("answer").toString()).toString());

        if (answers.size() < 2) {
            String rejectMsg = String.format("<mention uid=\"%d\"/> Your quiz contains less than 2 valid options",
                initiator.getUserId());
            QuizBot.sendMessage(QuizBot.getImStreamId(initiator.getUserId()), rejectMsg);
            log.info("Create quiz by {} rejected as there are less than 2 valid options", initiator.getDisplayName());
            return;
        }

        // Validate stream id if provided
        String targetStreamId = action.getStreamId();
        if (formValues.containsKey("targetStreamId")) {
            StreamInfo streamInfo = null;
            String tryTargetStreamId = MessageUtils.escapeStreamId(formValues.get("targetStreamId").toString());

            try {
                tryTargetStreamId = URLEncoder.encode(tryTargetStreamId, StandardCharsets.UTF_8.name());
                log.info("Looking up stream id: {}", tryTargetStreamId);
                streamInfo = QuizBot.getBotClient().getStreamsClient().getStreamInfo(tryTargetStreamId);
            } catch (UnsupportedEncodingException e) {
                log.error("Unable to URI encode stream id: {}", tryTargetStreamId);
            } catch (SymClientException e) {
                log.info("Invalid stream id: {}", tryTargetStreamId);
            }

            boolean isMember = false;
            if (streamInfo != null && streamInfo.getStreamType().getType() == StreamTypes.ROOM) {
                try {
                    isMember = QuizBot.getBotClient().getStreamsClient().getRoomMembers(tryTargetStreamId) != null;
                    targetStreamId = tryTargetStreamId;
                    log.info("Using stream id for room: {}", streamInfo.getRoomAttributes().getName());
                } catch (ForbiddenException e) {
                    log.error("I am not a member of this room: {}", tryTargetStreamId);
                }
            }

            if (streamInfo == null || streamInfo.getStreamType().getType() != StreamTypes.ROOM || !isMember) {
                String rejectMsg = (streamInfo == null || streamInfo.getStreamType().getType() != StreamTypes.ROOM) ?
                    "Your room stream id is invalid" : "I am not a member in that room";

                QuizBot.sendMessage(
                    QuizBot.getImStreamId(initiator.getUserId()),
                    String.format("<mention uid=\"%d\"/> %s: <b>%s</b>", initiator.getUserId(), rejectMsg, tryTargetStreamId)
                );

                if (streamInfo != null) {
                    log.info("Stream id is not a room: {}", tryTargetStreamId);
                }
                return;
            }
        }

        int timeLimit = Integer.parseInt(formValues.get("timeLimit").toString());

        Quiz quiz;
        QuizQuestion question = QuizQuestion.builder()
            .questionText(MessageUtils.escapeText(formValues.get("question").toString()))
            .answers(answers)
            .correctAnswer(answer)
            .timeLimit(timeLimit)
            .build();

        if (quizId.isEmpty()) {
            // Create quiz object and persist to database
            List<QuizQuestion> questions = new ArrayList<>();
            questions.add(question);
            quiz = Quiz.builder()
                .creator(initiator.getUserId())
                .created(Instant.now())
                .streamId(targetStreamId)
                .questions(questions)
                .build();
        } else {
            // Add question to existing quiz object
            quiz = dataService.getQuiz(quizId);
            quiz.getQuestions().add(question);
        }
        quiz = dataService.saveQuiz(quiz);

        handleSendCreateForm(quiz.getId(), targetStreamId, initiator);
    }

    public void handleLaunchQuiz(User initiator, SymphonyElementsAction action) {
        String quizId = action.getFormId().substring(17);
        Quiz quiz = dataService.getQuiz(quizId);
        QuizQuestion question = quiz.getCurrentQuestion();

        // Construct quiz form and blast to audience
        String blastML = MarkupService.blastTemplate;
        String label = String.format("Q%d of %d", quiz.getCurrentQuestionIndex() + 1, quiz.getQuestions().size());
        String blastData = MarkupService.getBlastData(quizId, question, label);

        QuizBot.sendMessage(quiz.getStreamId(), blastML, blastData);

        // Start timer
        String endByTimerNote = "";
        if (question.getTimeLimit() > 0) {
            Timer timer = new Timer("Timer" + quiz.getId() + quiz.getCurrentQuestionIndex());
            timer.schedule(new TimerTask() {
                public void run() {
                    handleNextQuestion(null, quiz.getCreator(), null);
                }
            }, 60000L * question.getTimeLimit());

            endByTimerNote = " or wait " + question.getTimeLimit() + " minute" + (question.getTimeLimit() > 1 ? "s" : "");
        }

        QuizBot.sendMessage(
            QuizBot.getImStreamId(initiator.getUserId()),
            String.format(
                "<mention uid=\"%d\"/> Your quiz has been started. You can use <b>/next</b>%s to advance",
                initiator.getUserId(), endByTimerNote
            )
        );
        log.info("New quiz by {} creation complete", initiator.getDisplayName());
    }

    public void handleSubmitVote(User initiator, SymphonyElementsAction action) {
        /*
        String answerIndexString = action.getFormValues().get("action").toString().replace("option-", "");
        int answerIndex = Integer.parseInt(answerIndexString);

        String quizId = action.getFormId().replace("quiz-blast-form-", "");
        QuizQuestion quizQuestion = dataService.getQuiz(quizId);

        if (quizQuestion == null) {
            QuizBot.sendMessage(
                QuizBot.getImStreamId(initiator.getUserId()),
                String.format(
                    "<mention uid=\"%d\"/> You have submitted a vote for an invalid quiz",
                    initiator.getUserId()
                )
            );
            log.info("Invalid vote cast by {} on stream {}",
                initiator.getDisplayName(), action.getStreamId());
            return;
        }

        String answer = quizQuestion.getAnswers().get(answerIndex);

        if (quizQuestion.getEnded() != null) {
            QuizBot.sendMessage(
                QuizBot.getImStreamId(initiator.getUserId()),
                String.format(
                    "<mention uid=\"%d\"/> This quiz has ended and no longer accepts votes: <i>%s</i>",
                    initiator.getUserId(), quizQuestion.getQuestionText()
                )
            );
            log.info("Rejected vote [{}] cast by {} in stream {} on expired quiz: {}",
                answer, initiator.getDisplayName(), action.getStreamId(), quizQuestion.getQuestionText());
            return;
        }

        String response, creatorNotification;
        if (dataService.hasVoted(initiator.getUserId(), quizId)) {
            dataService.changeVote(initiator.getUserId(), quizId, answer);
            response = String.format("Your vote has been updated to <b>%s</b> for the quiz: <i>%s</i>",
                answer, quizQuestion.getQuestionText());
            creatorNotification = String.format("has changed their vote to: <b>%s</b>", answer);
            log.info("Vote updated to [{}] on quiz {} by {}", answer, quizQuestion.getId(), initiator.getDisplayName());
        } else {
            QuizAnswer vote = QuizAnswer.builder()
                .quizId(quizId)
                .answer(answer)
                .userId(initiator.getUserId())
                .build();
            dataService.createVote(vote);
            response = String.format("Thanks for voting <b>%s</b> for the quiz: <i>%s</i>",
                answer, quizQuestion.getQuestionText());
            creatorNotification = String.format("has voted for: <b>%s</b>", answer);
            log.info("New vote [{}] cast on quiz {} by {}", answer, quizQuestion.getId(), initiator.getDisplayName());
        }

        QuizBot.sendMessage(QuizBot.getImStreamId(initiator.getUserId()),
            String.format("<mention uid=\"%d\"/> %s", initiator.getUserId(), response));
        QuizBot.sendMessage(QuizBot.getImStreamId(quizQuestion.getCreator()),
            String.format("<mention uid=\"%d\"/> %s", initiator.getUserId(), creatorNotification));
        */
    }

    private void handleRigQuiz(String streamId, long userId, String displayName) {
        /*
        log.info("Rig quiz requested by {}", displayName);

        QuizQuestion quizQuestionToRig = dataService.getActiveQuiz(userId);
        if (quizQuestionToRig == null) {
            QuizBot.sendMessage(streamId, "You have no active quiz to rig");
            log.info("User {} has no active quiz to rig", displayName);
            return;
        }

        List<QuizAnswer> votes = new ArrayList<>();
        List<String> randomAnswers = new ArrayList<>(quizQuestionToRig.getAnswers());
        Collections.shuffle(randomAnswers);
        int answersSize = quizQuestionToRig.getAnswers().size();
        int rigVolume = ThreadLocalRandom.current().nextInt(17, 77);
        for (int i=0; i < answersSize; i++) {
            for (int r = 0; r < rigVolume; r++) {
                votes.add(QuizAnswer.builder()
                    .quizId(quizQuestionToRig.getId())
                    .answer(randomAnswers.get(i))
                    .build());
            }
            rigVolume += (Math.random() * 387) - (Math.random() * 27);
        }
        dataService.createVotes(votes);

        QuizBot.sendMessage(streamId, "Your active quiz has been rigged");
        log.info("User {} has rigged active quiz", displayName);
        */
    }

    private void handleNextQuestion(String streamId, long userId, String displayName) {
        /*
        log.info("End quiz requested by {}", displayName != null ? displayName : "[Timer]");

        QuizQuestion quizQuestion = dataService.getActiveQuiz(userId);
        if (quizQuestion == null) {
            if (streamId != null) {
                QuizBot.sendMessage(streamId, "You have no active quiz to end");
                log.info("User {} has no active quiz to end", displayName);
            } else {
                log.info("Quiz by {} time limit reached but quiz was already ended", userId);
            }
            return;
        }

        List<QuizAnswer> votes = dataService.getVotes(quizQuestion.getId());
        String response, data = null;

        if (votes.isEmpty()) {
            response = String.format("<mention uid=\"%d\" /> Quiz ended but with no results to show", quizQuestion.getCreator());
            log.info("Quiz {} ended with no votes", quizQuestion.getId());
        } else {
            // Aggregate vote results
            List<QuizResult> quizResults = new ArrayList<>(dataService.getQuizResults(quizQuestion.getId()));

            // Add in widths
            long maxVal = Collections.max(quizResults, Comparator.comparingLong(QuizResult::getCount)).getCount();
            quizResults.forEach(r -> r.setWidth(Math.max(1, (int) (((float) r.getCount() / maxVal) * 200))));

            // Add in 0 votes for options nobody voted on
            quizQuestion.getAnswers().stream()
                .map(QuizResult::new)
                .filter(a -> !quizResults.contains(a))
                .forEach(quizResults::add);

            response = MarkupService.resultsTemplate;
            data = MarkupService.wrapData(QuizResultsData.builder()
                .creatorId(quizQuestion.getCreator())
                .question(quizQuestion.getQuestionText())
                .results(quizResults)
                .build());

            log.info("Quiz {} ended with results {}", quizQuestion.getId(), quizResults.toString());
        }

        dataService.nextQuestion(quizQuestion.getCreator());
        QuizBot.sendMessage(quizQuestion.getStreamId(), response, data);
        */
    }
}
