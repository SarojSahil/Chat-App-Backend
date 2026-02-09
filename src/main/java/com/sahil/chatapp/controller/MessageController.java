package com.sahil.chatapp.controller;

import com.sahil.chatapp.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send")
    public void sendMessage(@RequestBody Message message) {
        messagingTemplate.convertAndSend("/queue/user/" + message.getReceiverId(), message.getMessage());
    }
}
