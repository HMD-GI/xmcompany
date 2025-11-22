package com.xm.exception;


public class PasswordChangeException extends RuntimeException{
    public PasswordChangeException(String message) {
        super(message);
    }
}
