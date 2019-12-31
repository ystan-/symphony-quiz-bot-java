package com.symphony.ps.quizbot.listeners;

import com.symphony.ps.quizbot.services.QuizService;
import listeners.RoomListener;
import model.InboundMessage;
import model.Stream;
import model.StreamTypes;
import model.events.*;
import org.springframework.stereotype.Service;

@Service
public class RoomListenerImpl implements RoomListener {
    private final QuizService quizService;

    public RoomListenerImpl(QuizService quizService) {
        this.quizService = quizService;
    }

    public void onRoomMessage(InboundMessage message) {
        quizService.handleIncomingMessage(message, StreamTypes.ROOM);
    }

    public void onRoomCreated(RoomCreated roomCreated) {}
    public void onRoomDeactivated(RoomDeactivated roomDeactivated) {}
    public void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner) {}
    public void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner) {}
    public void onRoomReactivated(Stream stream) {}
    public void onRoomUpdated(RoomUpdated roomUpdated) {}
    public void onUserJoinedRoom(UserJoinedRoom userJoinedRoom) {}
    public void onUserLeftRoom(UserLeftRoom userLeftRoom) {}
}
