package com.sahil.chatapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactDeleteRequest {
    @NotNull
    private Long id;
}
