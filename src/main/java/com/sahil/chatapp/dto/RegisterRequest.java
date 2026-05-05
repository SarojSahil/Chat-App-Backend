package com.sahil.chatapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
public class RegisterRequest {
    @NotBlank(message = "Phone number cannot be blank.")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits.")
    @Pattern(regexp = "^[0-9]+", message = "Phone number should only contain digits")
    private String phoneNumber;

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, max = 20, message = "Name should have minimum 2 and maximum 20 characters.")
    private String name;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, max = 20, message = "Password should have minimum 8 and maximum 20 characters.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).*$", message = "Password must consists of at least one digit, lower case, upper case and special symbol.")
    private String password;
}
