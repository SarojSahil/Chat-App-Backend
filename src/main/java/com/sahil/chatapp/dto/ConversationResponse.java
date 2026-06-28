package com.sahil.chatapp.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ConversationResponse {
    private Long id;
    private UserResponse otherPerson;
    private LocalDateTime createdAt;

    @Builder
    @Getter
    public static class UserResponse {
        private Long id;
        private String name;
        private String phoneNumber;
        private String profilePictureUrl;
    }
}
