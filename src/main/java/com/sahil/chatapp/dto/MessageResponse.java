package com.sahil.chatapp.dto;

import com.sahil.chatapp.model.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
    private MessageType type;
}
