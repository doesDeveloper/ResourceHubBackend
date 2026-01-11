package com.ahmad.resourcehub.config;

import com.ahmad.resourcehub.dto.error.ApiErrorDTO;
import com.ahmad.resourcehub.dto.error.FieldErrorDTO;
import com.ahmad.resourcehub.exception.ApplicationException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    //Base application exception handler.
    @ExceptionHandler(ApplicationException.class)
    @Order(1)
    public ResponseEntity<ApiErrorDTO> handleApplicationException(ApplicationException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("Application exception [traceId={}]: {} - {}", traceId, ex.getErrorCode(), ex.getMessage());
        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(ex.getHttpStatus())
                .detail(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .instance(request.getRequestURI())
                .traceId(traceId)
                .details(ex.getDetails())
                .build();
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String traceId = generateTraceId();

        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_REQUEST_PARAMETER")
                .detail("Invalid value for parameter '" + ex.getName() + "'")
                .traceId(traceId)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String traceId = generateTraceId();
        List<FieldErrorDTO> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> new FieldErrorDTO(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage()
        )).toList();
        log.warn("Validation failed [traceId={}]: {} fieldErrors", traceId, fieldErrors.size());
        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail("Validation failed. Check field errors for details.")
                .errorCode("VALIDATION_FAILED")
                .traceId(traceId)
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        List<FieldErrorDTO> fieldErrors = ex.getConstraintViolations().stream().map(constraintViolation -> new FieldErrorDTO(
                constraintViolation.getPropertyPath().toString(),
                constraintViolation.getInvalidValue(),
                constraintViolation.getMessage()
        )).toList();
        log.warn("Constraint violation [traceId={}]: {}", traceId, ex.getMessage());
        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail("Constraint validation failed. Check field errors for details.")
                .errorCode("CONSTRAINT_VIOLATION")
                .traceId(traceId)
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDTO> handleAuthException(AuthenticationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Authentication failed [traceId={}]: {}", traceId, ex.getMessage());
        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .detail("Invalid username or password")
                .errorCode("AUTH_FAILED")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Access denied [traceId={}]: {}", traceId, ex.getMessage());
        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(HttpStatus.FORBIDDEN)
                .detail("Access denied to this resource")
                .errorCode("ACCESS_DENIED")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorDTO> handleJWTExpired(ExpiredJwtException ex) {
        String traceId = generateTraceId();
        log.warn("JWT expired [traceId={}]: {}", traceId, ex.getMessage());
        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(HttpStatus.FORBIDDEN)
                .detail("JWT token provided is expired.")
                .errorCode("JWT_TOKEN_EXPIRED")
                .traceId(traceId)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    //All other exceptions handling
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Unexpected error [traceId={}]: {}", traceId, ex.getMessage());
        ApiErrorDTO error = ApiErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .detail("Unexpected internal error")
                .errorCode("INTERNAL_ERROR")
                .instance(request.getRequestURI())
                .traceId(traceId)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException ex) {
//        ErrorDTO errorMap =
//                new ErrorDTO(ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
//        return ResponseEntity.badRequest().body(errorMap);
//    }
}
