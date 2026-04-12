package com.sahil.chatapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContactResponse {
    private Long id;
    private String name;
    private String phoneNumber;
}
