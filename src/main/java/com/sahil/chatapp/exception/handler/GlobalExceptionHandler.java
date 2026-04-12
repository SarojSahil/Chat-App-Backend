package com.sahil.chatapp.exception.handler;

import com.sahil.chatapp.dto.ErrorResponse;
import com.sahil.chatapp.exception.UserAlreadyExistsException;
import com.sahil.chatapp.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExists(HttpServletRequest request, UserAlreadyExistsException ex) {
        ErrorResponse error = ErrorResponse
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURL().toString())
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFound(HttpServletRequest request, UserNotFoundException ex) {
        ErrorResponse error = ErrorResponse
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURL().toString())
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgument(HttpServletRequest request, IllegalArgumentException ex) {
        ErrorResponse error = ErrorResponse
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURL().toString())
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> methodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors = new ArrayList<>();

        for (FieldError e : ex.getBindingResult().getFieldErrors()) {
            ErrorResponse error = ErrorResponse
                    .builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getDefaultMessage())
                    .path(request.getRequestURL().toString())
                    .build();
            errors.add(error);
        }
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> requestMethodNotSupported(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURL().toString())
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadable(HttpServletRequest request, HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURL().toString())
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missingRequestParameter(HttpServletRequest request, MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURL().toString())
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @MessageExceptionHandler
    public void handleException(Exception ex) {
        log.error(ex.getMessage());
    }
}
