package com.sahil.service;

import com.sahil.dto.ChatRequest;
import com.sahil.dto.MessageStatusReadRequest;
import com.sahil.dto.MessageStatusRequest;
import com.sahil.dto.MessageStatusResponse;
import com.sahil.exception.MessageNotFoundException;
import com.sahil.model.Message;
import com.sahil.model.MessageStatus;
import com.sahil.model.User;
import com.sahil.repository.MessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public User sendMessage(User sender, ChatRequest request) {
        User receiver = userService.findUserById(request.getReceiverId());
        Message messageToBeSaved = Message.builder()
                .message(request.getMessage())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();
        Message savedMessage = messageRepository.save(messageToBeSaved);

        simpMessagingTemplate.convertAndSendToUser(receiver.getUsername(), "/queue/message", savedMessage);
        simpMessagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/message", savedMessage);
        return receiver;
    }

    public void setMessageStatus(User receiver, MessageStatusRequest request) {
        Message message = getMessageById(request.getMessageId());
        if (message.getStatus().ordinal() < request.getStatus().ordinal()) {
            message.setStatus(request.getStatus());
            messageRepository.save(message);
            User sender = userService.findUserById(message.getSenderId());
            simpMessagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/message/status", List.of(new MessageStatusResponse(message.getId(), message.getStatus())));
        }
    }

    public void setMessagesDelivered(User user) {
        List<Message> messages = messageRepository.findByReceiverIdAndStatus(user.getId(), MessageStatus.SENT);
        List<Message> deliveredMessages = messages.stream()
                .map(msg -> {
                    return new Message(msg.getId(), msg.getMessage(), msg.getSenderId(), msg.getReceiverId(), msg.getTimestamp(), MessageStatus.DELIVERED);
                })
                .toList();
        messageRepository.saveAll(deliveredMessages);
        Map<Long, List<Message>> messagesBySenderId = deliveredMessages.stream()
                .collect(Collectors.groupingBy(Message::getSenderId, Collectors.toList()));
        List<Long> senderIds = messagesBySenderId.keySet().stream().toList();
        List<User> senders = userService.findAllUsersWhereIdIn(senderIds);

        for (Long senderId : senderIds) {
            List<MessageStatusResponse> messageStatuses = messagesBySenderId.get(senderId).stream().map((m) -> new MessageStatusResponse(m.getId(), m.getStatus())).toList();
            senders.stream()
                    .filter((s) -> s.getId().equals(senderId))
                    .findFirst()
                    .ifPresent((u) -> {
                        simpMessagingTemplate.convertAndSendToUser(u.getUsername(), "/queue/message/status", messageStatuses);
                    });
        }
    }

    public void setMessagesRead(User user, MessageStatusReadRequest request) {
        User sender = userService.findUserById(request.getSenderId());
        List<Message> messages = messageRepository.findBySenderIdAndReceiverIdAndStatusNot(request.getSenderId(), user.getId(), MessageStatus.READ);
        List<Message> readMessages = messages.stream()
                .map(msg -> {
                    return new Message(msg.getId(), msg.getMessage(), msg.getSenderId(), msg.getReceiverId(), msg.getTimestamp(), MessageStatus.READ);
                })
                .toList();
        messageRepository.saveAll(readMessages);
        List<MessageStatusResponse> messageStatuses = readMessages.stream().map((m) -> new MessageStatusResponse(m.getId(), m.getStatus())).toList();
        simpMessagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/message/status", messageStatuses);
    }

    public List<Message> getMessages(Long userId) {
        return messageRepository.findBySenderIdOrReceiverId(userId, userId);
    }

    public Message getMessageById(Long id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get();
        }
        throw new MessageNotFoundException("Message Not Found.");
    }
}
