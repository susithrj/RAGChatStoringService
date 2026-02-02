package com.codegensis.ragstore.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    @Test
    void resourceNotFoundException_WithMessage_CreatesException() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Session not found");

        // Then
        assertNotNull(exception);
        assertEquals("Session not found", exception.getMessage());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void resourceNotFoundException_WithResourceAndId_CreatesException() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Session", 123L);

        // Then
        assertNotNull(exception);
        assertEquals("Session with id 123 not found", exception.getMessage());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void validationException_WithMessage_CreatesException() {
        // When
        ValidationException exception = new ValidationException("Invalid input");

        // Then
        assertNotNull(exception);
        assertEquals("Invalid input", exception.getMessage());
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void rateLimitExceededException_WithMessage_CreatesException() {
        // When
        RateLimitExceededException exception = new RateLimitExceededException("Rate limit exceeded");

        // Then
        assertNotNull(exception);
        assertEquals("Rate limit exceeded", exception.getMessage());
        assertEquals(ErrorCode.RATE_LIMIT_EXCEEDED, exception.getErrorCode());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getHttpStatus());
    }

    @Test
    void apiException_GetErrorType_ReturnsCorrectType() {
        // Given
        ValidationException exception = new ValidationException("Test error");

        // When
        String errorType = exception.getErrorType();

        // Then
        assertEquals("https://ragchat.api/errors/validation-error", errorType);
    }

    @Test
    void apiException_GetErrorTitle_ReturnsCorrectTitle() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

        // When
        String errorTitle = exception.getErrorTitle();

        // Then
        assertEquals("Resource Not Found", errorTitle);
    }

    @Test
    void apiException_GetHttpStatus_ReturnsCorrectStatus() {
        // Given
        RateLimitExceededException exception = new RateLimitExceededException("Test");

        // When
        HttpStatus status = exception.getHttpStatus();

        // Then
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, status);
    }

    @Test
    void errorResponse_WithRetryAfter_CreatesResponse() {
        // Given
        ErrorResponse error = new ErrorResponse(
            "https://ragchat.api/errors/rate-limit-exceeded",
            "Rate Limit Exceeded",
            429,
            "Too many requests",
            "/api/v1/sessions"
        );

        // When
        ErrorResponse withRetryAfter = error.withRetryAfter(60);

        // Then
        assertNotNull(withRetryAfter);
        assertEquals(60, withRetryAfter.retryAfter());
        assertEquals(error.type(), withRetryAfter.type());
        assertEquals(error.title(), withRetryAfter.title());
    }

    @Test
    void errorResponse_ConvenienceConstructor_SetsTimestamp() {
        // When
        ErrorResponse error = new ErrorResponse(
            "type",
            "title",
            400,
            "detail",
            "instance"
        );

        // Then
        assertNotNull(error);
        assertNotNull(error.timestamp());
        assertNull(error.retryAfter());
    }
}
