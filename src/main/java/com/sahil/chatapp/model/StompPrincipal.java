package com.sahil.chatapp.model;


import lombok.Builder;
import lombok.Getter;

import java.security.Principal;

@Builder
@Getter
public class StompPrincipal implements Principal {

    private Long userId;
    private String username;
    private String phoneNumber;

    @Override
    public String getName() {
        return userId.toString();
    }
}
