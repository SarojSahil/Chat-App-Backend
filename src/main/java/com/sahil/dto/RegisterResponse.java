package com.sahil.dto;

public class RegisterResponse {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RegisterResponse() {
    }

    public RegisterResponse(String token) {
        this.token = token;
    }
}
