package com.courseshop.courseshop.service.error;

public class IdInvalidException extends RuntimeException {
    public IdInvalidException(String message){
        super(message);
    }
    
}
