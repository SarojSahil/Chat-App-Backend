package com.sahil.service;

import com.sahil.dto.MessageRequest;
import com.sahil.model.Message;
import com.sahil.model.User;
import com.sahil.repository.MessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;

    public MessageService(UserService userService, SimpMessagingTemplate simpMessagingTemplate, MessageRepository messageRepository) {
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.messageRepository = messageRepository;
    }

    public void sendMessage(User sender, MessageRequest messageRequest) {
        User receiver = userService.findUserById(messageRequest.getReceiverId());
        Message messageToBeSaved = Message.builder()
                .message(messageRequest.getMessage())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .timestamp(LocalDateTime.now())
                .build();
        Message savedMessage = messageRepository.save(messageToBeSaved);

        simpMessagingTemplate.convertAndSendToUser(receiver.getUsername(), "/queue/messages", savedMessage);
        simpMessagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/messages", savedMessage);
    }

    public List<Message> getMessages(Long userId) {
        return messageRepository.findBySenderIdOrReceiverId(userId, userId);
    }
}
