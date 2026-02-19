package com.sahil.controller;

import com.sahil.dto.LoginRequest;
import com.sahil.dto.LoginResponse;
import com.sahil.dto.RegisterRequest;
import com.sahil.dto.RegisterResponse;
import com.sahil.dto.UserDto;
import com.sahil.model.Authority;
import com.sahil.model.User;
import com.sahil.service.JwtService;
import com.sahil.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class Auth {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public Auth(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/users")
    public List<UserDto> findAllUsers() {
        List<User> users = userService.findAllUsers();
        return users.stream().map(UserDto::new).toList();
    }

    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {

        boolean userExists = userService.userExistsByUsername(request.getUsername());

        if (userExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(null, request.getUsername(), hashedPassword, List.of(Authority.ROLE_USER));

        userService.saveUser(user);

        String token = jwtService.grantToken(user.getUsername(), user.getAuthorities());

        return ResponseEntity.ok(new RegisterResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken
                .unauthenticated(request.getUsername(), request.getPassword());
        Authentication authenticated = authenticationManager.authenticate(unauthenticated);

        String token = jwtService.grantToken(authenticated.getName(), authenticated.getAuthorities());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
