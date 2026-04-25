package com.sahil.chatapp.service;

import com.sahil.chatapp.dto.LoginRequest;
import com.sahil.chatapp.dto.AuthResponse;
import com.sahil.chatapp.dto.RegisterRequest;
import com.sahil.chatapp.exception.UserAlreadyExistsException;
import com.sahil.chatapp.model.SystemRole;
import com.sahil.chatapp.model.User;
import com.sahil.chatapp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(LoginRequest request) {
        User user = (User) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword().trim()))
                .getPrincipal();

        String token = jwtService.grantToken(user);

        return AuthResponse
                .builder()
                .userId(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        boolean exists = userRepository.existsByPhoneNumber(request.getPhoneNumber());

        if (exists) {
            throw new UserAlreadyExistsException("User Already Exists.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword().trim());

        User userToBeSaved = User
                .builder()
                .phoneNumber(request.getPhoneNumber())
                .name(request.getName())
                .password(encodedPassword)
                .role(SystemRole.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        User user = userRepository.save(userToBeSaved);

        String token = jwtService.grantToken(user);

        return AuthResponse
                .builder()
                .userId(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .token(token)
                .build();
    }
}
