package com.sahil.chatapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ContactResponse {
    private Long id;
    private String name;
    private ContactUser user;

    @Getter
    @Builder
    public static class ContactUser {
        private Long id;
        private String name;
        private String phoneNumber;
        private String profilePictureUrl;
    }
}
