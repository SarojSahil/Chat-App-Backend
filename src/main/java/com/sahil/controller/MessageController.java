package com.sahil.controller;

import com.sahil.dto.MessageRequest;
import com.sahil.model.Message;
import com.sahil.model.User;
import com.sahil.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    public void getMethodName(UsernamePasswordAuthenticationToken authentication, @Payload MessageRequest message) {
        log.info("User: {} Sent A Message To: {}", authentication.getName(), message.getReceiverId());
        messageService.sendMessage((User) authentication.getPrincipal(), message);
    }

    @GetMapping("/messages")
    public List<Message> getUserMessages(@AuthenticationPrincipal User user) {
        log.info("Getting All The Messages For User: {}", user.getUsername());
        return messageService.getMessages(user.getId());
    }
}
