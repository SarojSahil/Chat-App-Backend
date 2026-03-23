package com.sahil.controller;

import com.sahil.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class Chat {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public Chat(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat")
    public void getMethodName(Principal principle, @Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
    }
}
