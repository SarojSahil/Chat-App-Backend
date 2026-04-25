package com.sahil.chatapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ContactUpdateRequest {

    @NotNull
    private Long contactId;

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, max = 20, message = "Name should have minimum 2 and maximum 20 characters.")
    private String contactName;
}