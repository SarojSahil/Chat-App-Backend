package com.sahil.dto;

import java.util.List;

import com.sahil.model.Authority;
import com.sahil.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String username;
    private List<Authority> authorities;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.authorities = user.getAuthorities().stream().map(auth -> Authority.valueOf(auth.getAuthority())).toList();
    }
}
