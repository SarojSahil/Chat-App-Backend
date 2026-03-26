package com.sahil.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private String message;
    private Long receiverId;
}
