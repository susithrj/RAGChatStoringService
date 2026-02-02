package com.codegensis.ragstore.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final int RETRY_AFTER_SECONDS = 60;
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
        logger.error("API exception: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            ex.getErrorType(),
            ex.getErrorTitle(),
            ex.getHttpStatus(),
            ex.getMessage(),
            request
        ).getBody();
        
        if (ex instanceof RateLimitExceededException && error != null) {
            error = error.withRetryAfter(RETRY_AFTER_SECONDS);
        }
        
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        logger.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String detail = "Validation failed: " + errors;
        return buildErrorResponse(
            ErrorCode.VALIDATION_ERROR.getType(),
            ErrorCode.VALIDATION_ERROR.getTitle(),
            ErrorCode.VALIDATION_ERROR.getHttpStatus(),
            detail,
            request
        );
    }
    
    /** Handles JPA entity constraint violations (e.g., @NotNull, @Size failures). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        logger.error("Constraint violation: {}", ex.getMessage());
        return buildErrorResponse(
            ErrorCode.VALIDATION_ERROR.getType(),
            ErrorCode.VALIDATION_ERROR.getTitle(),
            ErrorCode.VALIDATION_ERROR.getHttpStatus(),
            ex.getMessage(),
            request
        );
    }
    
    /** Handles database constraint violations (UNIQUE, FOREIGN KEY, CHECK constraints). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        logger.error("Data integrity violation: {}", ex.getMessage());
        
        String detail = extractConstraintDetail(ex);
        return buildErrorResponse(
            ErrorCode.CONFLICT.getType(),
            ErrorCode.CONFLICT.getTitle(),
            ErrorCode.CONFLICT.getHttpStatus(),
            detail,
            request
        );
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error", ex);
        return buildErrorResponse(
            ErrorCode.INTERNAL_SERVER_ERROR.getType(),
            ErrorCode.INTERNAL_SERVER_ERROR.getTitle(),
            ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
            "An unexpected error occurred",
            request
        );
    }
    
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String type, String title, HttpStatus status, String detail, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            type,
            title,
            status.value(),
            detail,
            extractInstancePath(request)
        );
        return ResponseEntity.status(status).body(error);
    }
    
    private String extractInstancePath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
    
    private String extractConstraintDetail(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMessage();
        String rootCauseMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        String fullMessage = (errorMessage != null ? errorMessage : "") + 
                           " " + (rootCauseMessage != null ? rootCauseMessage : "");
        
        if (fullMessage.contains("uk_sessions_user_title") || 
            fullMessage.contains("UNIQUE constraint") ||
            fullMessage.contains("duplicate key")) {
            return "A session with the same user ID and title already exists. Please use a different title or update the existing session.";
        }
        if (fullMessage.contains("chk_messages_sender") || 
            (fullMessage.contains("CHECK constraint") && fullMessage.contains("sender"))) {
            return "Invalid sender value. Sender must be one of: 'user', 'assistant', or 'system' (case-insensitive).";
        }
        if (fullMessage.contains("foreign key") || fullMessage.contains("FOREIGN KEY constraint")) {
            return "Referenced resource does not exist";
        }
        return "A resource with the same identifier already exists";
    }
}
