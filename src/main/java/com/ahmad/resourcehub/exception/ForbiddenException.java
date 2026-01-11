package com.ahmad.resourcehub.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ForbiddenException extends ApplicationException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String action, String resource) {
        super(String.format("You don't have %s permission for %s", action, resource), "FORBIDDEN", HttpStatus.FORBIDDEN, Map.of("action", action, "resource", resource));
    }
}
