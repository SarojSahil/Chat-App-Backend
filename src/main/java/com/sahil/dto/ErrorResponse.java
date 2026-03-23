package com.sahil.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
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
