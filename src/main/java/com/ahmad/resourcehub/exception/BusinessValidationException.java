package com.ahmad.resourcehub.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;


public class BusinessValidationException extends ApplicationException {
    public BusinessValidationException(String message, String error_code) {
        super(message, error_code, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    public BusinessValidationException(String message, String error_code, Exception ex) {
        super(message, error_code, HttpStatus.UNPROCESSABLE_ENTITY );
    }
    public BusinessValidationException(String message, String error_code, Map<String, Object> details) {
        super(message, error_code, HttpStatus.UNPROCESSABLE_ENTITY, details);
    }
}
