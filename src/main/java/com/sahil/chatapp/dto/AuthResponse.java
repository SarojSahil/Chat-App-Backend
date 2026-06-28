package com.sahil.chatapp.dto;

import com.sahil.chatapp.model.SystemRole;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String phoneNumber;
    private SystemRole role;
}
