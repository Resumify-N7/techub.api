package com.techub.api.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException() {
        super("Email já está em uso");
    }
}
