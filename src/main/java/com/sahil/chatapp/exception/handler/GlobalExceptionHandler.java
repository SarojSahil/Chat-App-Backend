package com.sahil.chatapp.exception.handler;

import com.sahil.chatapp.dto.ErrorResponse;
import com.sahil.chatapp.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class, ContactAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflict(HttpServletRequest req, Exception ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler({UserNotFoundException.class, ContactNotFoundException.class, ConversationNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(HttpServletRequest req, Exception ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpServletRequest req, Exception ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpServletRequest req, Exception ex) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidation(HttpServletRequest req, MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        List<ErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> ErrorResponse.builder()
                        .message(e.getDefaultMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(LocalDateTime.now())
                        .path(req.getRequestURL().toString())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest req) {
        log.error("Exception: {}", message);
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .path(req.getRequestURL().toString())
                .build());
    }
}