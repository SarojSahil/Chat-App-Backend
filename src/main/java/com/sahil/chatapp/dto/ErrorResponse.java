package com.sahil.chatapp.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
    private String path;
}
