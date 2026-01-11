package com.ahmad.resourcehub.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

// 400 Bad Request
public class BadRequestException extends ApplicationException{
    public BadRequestException(String message){
        super(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
    public BadRequestException(String message, Map<String, Object> details){
        super(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST, details);
    }
}
