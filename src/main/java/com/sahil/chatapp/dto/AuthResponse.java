package com.sahil.chatapp.dto;

import com.sahil.chatapp.model.SystemRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {
    private String token;
    private Long userId;
    private String phoneNumber;
    private SystemRole role;
}
