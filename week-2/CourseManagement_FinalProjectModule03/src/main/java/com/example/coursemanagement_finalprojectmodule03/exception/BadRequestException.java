package com.example.coursemanagement_finalprojectmodule03.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
