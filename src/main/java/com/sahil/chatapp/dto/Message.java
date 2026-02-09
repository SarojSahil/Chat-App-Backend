package com.sahil.chatapp.dto;

public class Message {
    private String message;
    private int receiverId;

    public Message() {
    }

    public Message(String message, int receiverId) {
        this.message = message;
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
}
