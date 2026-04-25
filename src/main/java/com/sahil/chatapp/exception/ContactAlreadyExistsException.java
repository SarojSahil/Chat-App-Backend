package com.sahil.chatapp.exception;

public class ContactAlreadyExistsException extends RuntimeException {
    public ContactAlreadyExistsException(String s) {
        super(s);
    }
}
