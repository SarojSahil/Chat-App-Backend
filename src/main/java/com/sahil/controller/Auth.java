package com.sahil.controller;

import com.sahil.dto.*;
import com.sahil.exception.UserAlreadyExistsException;
import com.sahil.model.Authority;
import com.sahil.model.User;
import com.sahil.service.JwtService;
import com.sahil.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDto> userDtos = users.stream().map(UserDto::new).toList();
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal String username) {
        User user = userService.loadUserByUsername(username);
        UserDto userDto = new UserDto(user);
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
                .authorities(List.of(Authority.ROLE_USER))
                .build();

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
