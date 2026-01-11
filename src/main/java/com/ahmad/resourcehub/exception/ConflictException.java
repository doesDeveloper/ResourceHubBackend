package com.ahmad.resourcehub.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

// 409 Conflict
public class ConflictException extends ApplicationException {
    public ConflictException(String message) {
        super(message, "CONFLICT", HttpStatus.CONFLICT);
    }

    public ConflictException(String resourceType, String field, String value) {
        super(
                String.format("%s with %s '%s' already exists", resourceType, field, value),
                "RESOURCE_ALREADY_EXISTS",
                HttpStatus.CONFLICT,
                Map.of(
                        "resourceType", resourceType,
                        "field", field,
                        "value", value
                )
        );
    }

}
