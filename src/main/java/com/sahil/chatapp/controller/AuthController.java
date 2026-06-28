package com.sahil.chatapp.controller;

import com.sahil.chatapp.dto.AuthResponse;
import com.sahil.chatapp.dto.LoginRequest;
import com.sahil.chatapp.dto.RegisterRequest;
import com.sahil.chatapp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @ModelAttribute RegisterRequest request,
                                                 BindingResult result,
                                                 @RequestParam(value = "profilePicture", required = false) MultipartFile file) {

        AuthResponse response = authService.register(request, file);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}