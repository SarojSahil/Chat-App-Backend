package com.sahil.dto;

import java.util.List;

import com.sahil.model.Authority;
import com.sahil.model.User;

public class UserDto {
    private Long id;
    private String username;
    private List<Authority> authorities;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.authorities = user.getAuthorities().stream().map(auth -> Authority.valueOf(auth.getAuthority())).toList();
    }

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
