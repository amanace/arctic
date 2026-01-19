package com.acrtic.chat.controller;

import com.acrtic.chat.model.ChatMessage;
import com.acrtic.chat.model.MessageEntity;
import com.acrtic.chat.repository.MessageRepository;
import com.acrtic.chat.service.PresenceService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatController extends TextWebSocketHandler {

    private final PresenceService presenceService;

public ChatController(PresenceService presenceService) {
    this.presenceService = presenceService;
}


    @Autowired
    private MessageRepository messageRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    // username -> session
    private static final ConcurrentHashMap<String, WebSocketSession> users = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = session.getUri().getQuery().split("=")[1];
    presenceService.userOnline(username);
    System.out.println(username + " is online");
    }

    @Override
public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    String username = session.getUri().getQuery().split("=")[1];
    presenceService.userOffline(username);
    System.out.println(username + " is offline");
}


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        ChatMessage msg = mapper.readValue(message.getPayload(), ChatMessage.class);

        // Register user on first message
        users.putIfAbsent(msg.from, session);

        if ("message".equals(msg.type)) {

    // Save message as SENT
    MessageEntity entity = new MessageEntity();
    entity.sender = msg.from;
    entity.receiver = msg.to;
    entity.content = msg.content;
    entity.status = MessageEntity.Status.SENT;

    entity = messageRepository.save(entity);

    // Send back messageId + SENT status to sender
    session.sendMessage(new TextMessage(
        mapper.writeValueAsString(
            Map.of(
                "type", "status",
                "messageId", entity.id,
                "status", "SENT"
            )
        )
    ));

    // Forward message to receiver
    WebSocketSession target = users.get(msg.to);
    if (target != null && target.isOpen()) {
        target.sendMessage(new TextMessage(
            mapper.writeValueAsString(
                Map.of(
                    "type", "message",
                    "messageId", entity.id,
                    "from", msg.from,
                    "content", msg.content
                )
            )
        ));

        // Update status to DELIVERED
        entity.status = MessageEntity.Status.DELIVERED;
        messageRepository.save(entity);
    }
}

  else if ("seen".equals(msg.type)) {

        var messageEntity = messageRepository.findById(msg.messageId).orElseThrow();

        messageEntity.status = MessageEntity.Status.SEEN;
        messageRepository.save(messageEntity);

        WebSocketSession senderSession = users.get(messageEntity.sender);
        if (senderSession != null && senderSession.isOpen()) {
            senderSession.sendMessage(new TextMessage(
                mapper.writeValueAsString(
                    Map.of(
                        "type", "status",
                        "messageId", messageEntity.id,
                        "status", "SEEN"
                    )
                )
            ));
        }
    }

    // 3. Typing indicator
    else if ("typing".equals(msg.type)) {
        WebSocketSession target = users.get(msg.to);
        if (target != null && target.isOpen()) {
            target.sendMessage(new TextMessage(message.getPayload()));
        }
    }
        System.out.println("Message: " + message.getPayload());
    }
}

