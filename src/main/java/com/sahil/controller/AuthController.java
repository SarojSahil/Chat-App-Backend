package com.sahil.controller;

import com.sahil.dto.*;
import com.sahil.exception.UserAlreadyExistsException;
import com.sahil.model.Authority;
import com.sahil.model.User;
import com.sahil.service.JwtService;
import com.sahil.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<User> users = userService.findAllUsers();

        List<UserDto> userDto = users.stream().map(UserDto::new).toList();

        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal User user) {

        UserDto userDto = new UserDto(user);

        log.info("Profile Fetched By: {}", user.getUsername());

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {

        boolean exists = userService.userExistsByUsername(request.getUsername());

        if (exists) {
            throw new UserAlreadyExistsException("User Already Exists.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User
                .builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .authorities(Set.of(Authority.ROLE_USER))
                .build();

        User userCreated = userService.saveUser(user);

        String token = jwtService.grantToken(userCreated);

        log.info("User Registered With Username: {}", user.getUsername());

        return ResponseEntity.ok(new RegisterResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken
                .unauthenticated(request.getUsername(), request.getPassword());

        Authentication result = authenticationManager.authenticate(unauthenticated);

        User user = (User) result.getPrincipal();

        String token = jwtService.grantToken(user);

        log.info("User Logged In With Username: {}", user.getUsername());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
