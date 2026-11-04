package com.courseshop.courseshop.service.error;

public class EmailAlreadyExistsException extends IllegalArgumentException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
