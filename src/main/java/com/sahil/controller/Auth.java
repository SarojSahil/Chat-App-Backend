package com.sahil.controller;

import com.sahil.dto.LoginRequest;
import com.sahil.dto.LoginResponse;
import com.sahil.dto.RegisterRequest;
import com.sahil.dto.RegisterResponse;
import com.sahil.dto.UserDto;
import com.sahil.model.Authority;
import com.sahil.model.User;
import com.sahil.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class Auth {

    @Value("${jwt.secret}")
    String secret;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationContext context;

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/users")
    public List<UserDto> findAllUsers() {
        List<User> users = userService.findAllUsers();
        return users.stream().map(UserDto::new).toList();
    }

    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {

        boolean userExists = userService.userExistsByUsername(request.getUsername());

        if (userExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(null, request.getUsername(), hashedPassword, List.of(Authority.ROLE_USER));

        userService.saveUser(user);

        Date now = new Date();
        Date afterOneHour = new Date(now.getTime() + 1000 * 60 * 60);
        SecretKey key = new SecretKeySpec(Decoders.BASE64URL.decode(secret), "HmacSha256");

        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .claim("scope", user.getAuthorities().stream().map(auth -> auth.getAuthority()).toList())
                .issuedAt(now)
                .expiration(afterOneHour)
                .signWith(key)
                .compact();

        return ResponseEntity.ok(new RegisterResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken
                .unauthenticated(request.getUsername(), request.getPassword());
        Authentication authenticated = authenticationManager.authenticate(unauthenticated);

        Date now = new Date();
        Date afterOneHour = new Date(now.getTime() + 1000 * 60 * 60);
        SecretKey key = new SecretKeySpec(Decoders.BASE64URL.decode(secret), "HmacSha256");

        String token = Jwts
                .builder()
                .subject(authenticated.getPrincipal().toString())
                .claim("scope", authenticated.getAuthorities().stream().map(auth -> auth.getAuthority()).toList())
                .issuedAt(now)
                .expiration(afterOneHour)
                .signWith(key)
                .compact();

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
