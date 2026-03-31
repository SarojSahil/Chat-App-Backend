package com.sahil.dto;

import com.sahil.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageStatusRequest {
    private Long messageId;
    private MessageStatus status;
}
