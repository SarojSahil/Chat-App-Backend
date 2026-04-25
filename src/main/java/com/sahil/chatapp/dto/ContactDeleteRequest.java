package com.sahil.chatapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ContactDeleteRequest {
    @NotNull
    private Long id;
}
