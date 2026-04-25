package com.sahil.chatapp.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConversationResponse {
    private Long id;
    private UserResponse otherPerson;

    @Builder
    @Getter
    public static class UserResponse {
        private Long id;
        private String name;
        private String phoneNumber;
    }
}
