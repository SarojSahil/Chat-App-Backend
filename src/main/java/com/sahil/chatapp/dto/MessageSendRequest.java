package com.sahil.chatapp.dto;

import com.sahil.chatapp.model.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageSendRequest {

    @NotBlank(message = "Message connot be blank.")
    private String content;
    private Long conversationId;
    private Long receiverId;
    @NotNull
    private MessageType type;
}
