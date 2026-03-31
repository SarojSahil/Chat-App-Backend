package com.sahil.controller;

import com.sahil.dto.ChatRequest;
import com.sahil.dto.MessageStatusReadRequest;
import com.sahil.dto.MessageStatusRequest;
import com.sahil.model.Message;
import com.sahil.model.User;
import com.sahil.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/message")
    public void getMethodName(UsernamePasswordAuthenticationToken authentication, @Payload ChatRequest request) {
        User user = (User) authentication.getPrincipal();
        if (user != null) {
            User receiver = messageService.sendMessage(user, request);
            log.info("User: {} Sent A Message To: {}", user.getUsername(), receiver.getUsername());
        }
    }

    @MessageMapping("/message/status")
    public void setMessageStatus(UsernamePasswordAuthenticationToken authentication, @Payload MessageStatusRequest request) {
        User user = (User) authentication.getPrincipal();
        if (user != null) {
            log.info("Setting Status: {} For Message: {}", request.getStatus(), request.getMessageId());
            messageService.setMessageStatus(user, request);
        }
    }

    @GetMapping("/messages")
    public List<Message> getUserMessages(@AuthenticationPrincipal User user) {
        log.info("Getting All The Messages For User: {}", user.getUsername());
        return messageService.getMessages(user.getId());
    }

    @PatchMapping("/messages/delivered")
    public ResponseEntity<?> setMessagesDelivered(@AuthenticationPrincipal User user) {
        messageService.setMessagesDelivered(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/messages/read")
    public ResponseEntity<?> setMessagesRead(@AuthenticationPrincipal User user,@RequestBody MessageStatusReadRequest request) {
        messageService.setMessagesRead(user, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
