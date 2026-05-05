package com.sahil.chatapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ContactSaveRequest {

    @NotBlank(message = "Phone number cannot be blank.")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number should only contain digits")
    private String phoneNumber;

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, max = 20, message = "Name should have minimum 2 and maximum 20 characters.")
    private String contactName;
}
