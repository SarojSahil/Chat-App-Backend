package com.sahil.chatapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageSendRequest {

    @NotBlank(message = "Message connot be blank.")
    private String content;
    private Long conversationId;
    private Long receiverId;
}
