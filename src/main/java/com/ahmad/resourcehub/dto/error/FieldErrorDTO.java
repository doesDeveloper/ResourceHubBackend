package com.ahmad.resourcehub.dto.error;

public record FieldErrorDTO(
        String field,
        Object rejectedValue,
        String message
) {}
