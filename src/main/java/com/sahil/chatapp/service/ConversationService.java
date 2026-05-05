package com.sahil.chatapp.service;

import com.sahil.chatapp.dto.ConversationResponse;
import com.sahil.chatapp.dto.MessageResponse;
import com.sahil.chatapp.dto.MessageSendRequest;
import com.sahil.chatapp.exception.ConversationNotFoundException;
import com.sahil.chatapp.exception.UserNotFoundException;
import com.sahil.chatapp.model.Conversation;
import com.sahil.chatapp.model.Message;
import com.sahil.chatapp.model.User;
import com.sahil.chatapp.repository.ConversationRepository;
import com.sahil.chatapp.repository.MessageRepository;
import com.sahil.chatapp.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ConversationService(MessageRepository messageRepository, ConversationRepository conversationRepository, UserRepository userRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public Slice<MessageRepository.MessageProjection> getMessages(User user, Long conversationId, Pageable pageable) {
        conversationRepository.findByIdAndUser(conversationId, user.getId())
                .orElseThrow(() -> new ConversationNotFoundException("Conversation does not exist."));
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }

    public List<ConversationResponse> getConversations(User user) {
        return conversationRepository.findByUser(user.getId())
                .stream()
                .map(c -> mapToConversationResponse(c, user))
                .toList();
    }

    @Transactional
    public ConversationResponse sendMessage(User sender, MessageSendRequest request) {
        Conversation conversation = null;
        boolean isNew = false;

        if (request.getConversationId() != null) {
            conversation = getExistingConversation(request.getConversationId(), sender.getId());
        } else if (request.getReceiverId() != null) {
            conversation = conversationRepository.findByUsers(Math.min(sender.getId(), request.getReceiverId()), Math.max(sender.getId(), request.getReceiverId()))
                    .orElseGet(() -> createNewConversation(sender, request.getReceiverId()));
            isNew = true;
        } else {
            throw new IllegalArgumentException("Conversation Id or Receiver Id must be provided.");
        }

        Message message = Message.builder()
                .createdAt(LocalDateTime.now())
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .build();

        messageRepository.save(message);

        MessageResponse messageResponse = MessageResponse
                .builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();

        simpMessagingTemplate.convertAndSendToUser(conversation.getUserOne().getId().toString(), "/queue/message", messageResponse);
        simpMessagingTemplate.convertAndSendToUser(conversation.getUserTwo().getId().toString(), "/queue/message", messageResponse);

        return isNew ? mapToConversationResponse(conversation, sender) : null;
    }

    private Conversation getExistingConversation(Long conversationId, Long userId) {
        return conversationRepository.findByIdAndUser(conversationId, userId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation does not exist."));
    }

    private Conversation createNewConversation(User sender, Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found."));

        Conversation conversation = Conversation.builder()
                .createdAt(LocalDateTime.now())
                .build();
        if (sender.getId() > receiver.getId()) {
            conversation.setUserOne(receiver);
            conversation.setUserTwo(sender);
        } else {
            conversation.setUserOne(sender);
            conversation.setUserTwo(receiver);
        }
        return conversationRepository.save(conversation);
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation, User user) {
        User otherPerson = null;

        if (conversation.getUserOne().getId() == user.getId()) {
            otherPerson = conversation.getUserTwo();
        } else {
            otherPerson = conversation.getUserOne();
        }

        return ConversationResponse
                .builder()
                .id(conversation.getId())
                .createdAt(conversation.getCreatedAt())
                .otherPerson(
                        ConversationResponse.UserResponse
                                .builder()
                                .id(otherPerson.getId())
                                .name(otherPerson.getName())
                                .phoneNumber(otherPerson.getPhoneNumber())
                                .profilePictureUrl(otherPerson.getProfilePictureUrl())
                                .build()
                )
                .build();
    }
}
