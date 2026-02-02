package com.codegensis.ragstore.validator;

import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionValidatorTest {

    private SessionValidator sessionValidator;

    @BeforeEach
    void setUp() {
        sessionValidator = new SessionValidator();
    }

    // validateUserId tests
    @Test
    void validateUserId_ValidUserId_NoException() {
        // Given
        String validUserId = "user123";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateUserId(validUserId));
    }

    @Test
    void validateUserId_ValidUserIdWithUnderscore_NoException() {
        // Given
        String validUserId = "user_123";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateUserId(validUserId));
    }

    @Test
    void validateUserId_ValidUserIdAllAlphanumeric_NoException() {
        // Given
        String validUserId = "abc123XYZ";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateUserId(validUserId));
    }

    @Test
    void validateUserId_ValidUserIdSingleCharacter_NoException() {
        // Given
        String validUserId = "a";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateUserId(validUserId));
    }

    @Test
    void validateUserId_ValidUserIdMaxLength_NoException() {
        // Given
        String validUserId = "a".repeat(ApiConstants.MAX_USER_ID_LENGTH);

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateUserId(validUserId));
    }

    @Test
    void validateUserId_Null_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId(null);
        });

        assertEquals("User ID is required", exception.getMessage());
    }

    @Test
    void validateUserId_Blank_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId("   ");
        });

        assertEquals("User ID is required", exception.getMessage());
    }

    @Test
    void validateUserId_Empty_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId("");
        });

        assertEquals("User ID is required", exception.getMessage());
    }

    @Test
    void validateUserId_ContainsSpace_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId("user 123");
        });

        assertEquals("User ID must contain only alphanumeric characters and underscores", 
                     exception.getMessage());
    }

    @Test
    void validateUserId_ContainsSpecialCharacters_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId("user@123");
        });

        assertEquals("User ID must contain only alphanumeric characters and underscores", 
                     exception.getMessage());
    }

    @Test
    void validateUserId_ContainsHyphen_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId("user-123");
        });

        assertEquals("User ID must contain only alphanumeric characters and underscores", 
                     exception.getMessage());
    }

    @Test
    void validateUserId_ContainsDot_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId("user.123");
        });

        assertEquals("User ID must contain only alphanumeric characters and underscores", 
                     exception.getMessage());
    }

    @Test
    void validateUserId_ExceedsMaxLength_ThrowsException() {
        // Given
        String longUserId = "a".repeat(ApiConstants.MAX_USER_ID_LENGTH + 1);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateUserId(longUserId);
        });

        assertEquals("User ID must not exceed " + ApiConstants.MAX_USER_ID_LENGTH + " characters", 
                     exception.getMessage());
    }

    // validateTitle tests
    @Test
    void validateTitle_ValidTitle_NoException() {
        // Given
        String validTitle = "My Chat Session";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateTitle(validTitle));
    }

    @Test
    void validateTitle_ValidTitleWithSpecialCharacters_NoException() {
        // Given
        String validTitle = "Chat Session #1 - Discussion";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateTitle(validTitle));
    }

    @Test
    void validateTitle_ValidTitleMaxLength_NoException() {
        // Given
        String maxLengthTitle = "a".repeat(ApiConstants.MAX_TITLE_LENGTH);

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateTitle(maxLengthTitle));
    }

    @Test
    void validateTitle_ValidTitleSingleCharacter_NoException() {
        // Given
        String validTitle = "A";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateTitle(validTitle));
    }

    @Test
    void validateTitle_Null_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateTitle(null);
        });

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void validateTitle_Blank_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateTitle("   ");
        });

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void validateTitle_Empty_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateTitle("");
        });

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void validateTitle_ExceedsMaxLength_ThrowsException() {
        // Given
        String longTitle = "a".repeat(ApiConstants.MAX_TITLE_LENGTH + 1);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            sessionValidator.validateTitle(longTitle);
        });

        assertEquals("Title must not exceed " + ApiConstants.MAX_TITLE_LENGTH + " characters", 
                     exception.getMessage());
    }

    @Test
    void validateTitle_ValidTitleWithUnicode_NoException() {
        // Given
        String validTitle = "Chat Session 会话";

        // When & Then
        assertDoesNotThrow(() -> sessionValidator.validateTitle(validTitle));
    }
}
