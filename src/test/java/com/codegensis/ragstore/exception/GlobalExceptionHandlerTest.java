package com.codegensis.ragstore.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/sessions");
    }

    @Test
    void handleApiException_ResourceNotFoundException_Returns404() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Session", 1L);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleApiException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://ragchat.api/errors/not-found", response.getBody().type());
        assertEquals("Resource Not Found", response.getBody().title());
        assertNull(response.getBody().retryAfter());
    }

    @Test
    void handleApiException_RateLimitExceededException_Returns429WithRetryAfter() {
        // Given
        RateLimitExceededException ex = new RateLimitExceededException("Rate limit exceeded");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleApiException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(60, response.getBody().retryAfter());
    }

    @Test
    void handleApiException_ValidationException_Returns400() {
        // Given
        ValidationException ex = new ValidationException("Invalid input");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleApiException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://ragchat.api/errors/validation-error", response.getBody().type());
    }

    @Test
    void handleConstraintViolationException_Returns400() {
        // Given
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", 
            Collections.emptySet());

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolationException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://ragchat.api/errors/validation-error", response.getBody().type());
    }

    @Test
    void handleDataIntegrityViolationException_UniqueConstraint_Returns409() {
        // Given
        RuntimeException rootCause = new RuntimeException("duplicate key");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("uk_sessions_user_title violation", rootCause);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().detail().contains("same user ID and title"));
    }

    @Test
    void handleDataIntegrityViolationException_ForeignKeyConstraint_Returns409() {
        // Given
        RuntimeException rootCause = new RuntimeException("FOREIGN KEY constraint");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("foreign key violation", rootCause);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().detail().contains("Referenced resource does not exist"));
    }

    @Test
    void handleDataIntegrityViolationException_CheckConstraint_Returns409() {
        // Given
        RuntimeException rootCause = new RuntimeException("CHECK constraint sender");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("chk_messages_sender violation", rootCause);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().detail().contains("Invalid sender value"));
    }

    @Test
    void handleDataIntegrityViolationException_Generic_Returns409() {
        // Given
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Generic constraint violation");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().detail().contains("same identifier"));
    }

    @Test
    void handleGenericException_Returns500() {
        // Given
        Exception ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://ragchat.api/errors/internal-server-error", response.getBody().type());
        assertEquals("An unexpected error occurred", response.getBody().detail());
    }

    @Test
    void handleMethodArgumentNotValidException_Returns400() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValidException(ex, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().detail().contains("Validation failed"));
    }
}
