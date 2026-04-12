package com.sahil.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

@AllArgsConstructor
@Data
public class StompPrincipal implements Principal {

    private Long userId;

    @Override
    public String getName() {
        return userId.toString();
    }
}
