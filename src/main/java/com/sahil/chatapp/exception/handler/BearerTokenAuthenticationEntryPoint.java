package com.sahil.chatapp.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahil.chatapp.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class BearerTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public BearerTokenAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED)
                .path(request.getRequestURL().toString())
                .message("Authentication Required.")
                .build();

        if (authException instanceof BadCredentialsException) {
            errorResponse.setStatus(HttpStatus.BAD_REQUEST);
            errorResponse.setMessage("Invalid Phone Number Or Password.");
        }
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
