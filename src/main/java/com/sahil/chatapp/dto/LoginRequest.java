package com.sahil.chatapp.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "Phone number cannot be blank.")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits.")
    @Pattern(regexp = "^[0-9]+", message = "Phone number should only contain digits")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, max = 20, message = "Password should have minimum 8 and maximum 20 characters.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).*$", message = "Password must consists of at least one digit, lower case, upper case and special symbol.")
    private String password;
}
