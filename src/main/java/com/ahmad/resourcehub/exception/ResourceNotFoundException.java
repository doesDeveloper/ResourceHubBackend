package com.ahmad.resourcehub.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

// 404 Not Found
public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("Resource %s with identifier %s not found", resourceType, identifier),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                Map.of("resourceType", resourceType, "identifier", identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
