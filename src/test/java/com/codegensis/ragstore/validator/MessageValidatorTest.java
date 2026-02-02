package com.codegensis.ragstore.validator;

import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.entity.Message;
import com.codegensis.ragstore.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageValidatorTest {

    private MessageValidator messageValidator;

    @BeforeEach
    void setUp() {
        messageValidator = new MessageValidator();
    }

    // validateContent tests
    @Test
    void validateContent_ValidContent_NoException() {
        // Given
        String validContent = "This is a valid message content";

        // When & Then
        assertDoesNotThrow(() -> messageValidator.validateContent(validContent));
    }

    @Test
    void validateContent_NullContent_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateContent(null);
        });

        assertEquals("Content is required", exception.getMessage());
    }

    @Test
    void validateContent_BlankContent_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateContent("   ");
        });

        assertEquals("Content is required", exception.getMessage());
    }

    @Test
    void validateContent_EmptyContent_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateContent("");
        });

        assertEquals("Content is required", exception.getMessage());
    }

    @Test
    void validateContent_ExceedsMaxLength_ThrowsException() {
        // Given
        String longContent = "a".repeat(ApiConstants.MAX_CONTENT_LENGTH + 1);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateContent(longContent);
        });

        assertEquals("Content must not exceed " + ApiConstants.MAX_CONTENT_LENGTH + " characters", 
                     exception.getMessage());
    }

    @Test
    void validateContent_ExactlyMaxLength_NoException() {
        // Given
        String maxLengthContent = "a".repeat(ApiConstants.MAX_CONTENT_LENGTH);

        // When & Then
        assertDoesNotThrow(() -> messageValidator.validateContent(maxLengthContent));
    }

    // validateContext tests
    @Test
    void validateContext_NullContext_NoException() {
        // When & Then
        assertDoesNotThrow(() -> messageValidator.validateContext(null));
    }

    @Test
    void validateContext_ValidContext_NoException() {
        // Given
        String validContext = "{\"key\": \"value\"}";

        // When & Then
        assertDoesNotThrow(() -> messageValidator.validateContext(validContext));
    }

    @Test
    void validateContext_ExceedsMaxLength_ThrowsException() {
        // Given
        String longContext = "a".repeat(ApiConstants.MAX_CONTEXT_LENGTH + 1);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateContext(longContext);
        });

        assertEquals("Context must not exceed 50KB", exception.getMessage());
    }

    @Test
    void validateContext_ExactlyMaxLength_NoException() {
        // Given
        String maxLengthContext = "a".repeat(ApiConstants.MAX_CONTEXT_LENGTH);

        // When & Then
        assertDoesNotThrow(() -> messageValidator.validateContext(maxLengthContext));
    }

    @Test
    void validateContext_EmptyString_NoException() {
        // When & Then
        assertDoesNotThrow(() -> messageValidator.validateContext(""));
    }

    // validateAndParseSender tests
    @Test
    void validateAndParseSender_ValidUser_ReturnsSender() {
        // When
        Message.Sender result = messageValidator.validateAndParseSender("user");

        // Then
        assertEquals(Message.Sender.USER, result);
    }

    @Test
    void validateAndParseSender_ValidUserUpperCase_ReturnsSender() {
        // When
        Message.Sender result = messageValidator.validateAndParseSender("USER");

        // Then
        assertEquals(Message.Sender.USER, result);
    }

    @Test
    void validateAndParseSender_ValidAssistant_ReturnsSender() {
        // When
        Message.Sender result = messageValidator.validateAndParseSender("assistant");

        // Then
        assertEquals(Message.Sender.ASSISTANT, result);
    }

    @Test
    void validateAndParseSender_ValidSystem_ReturnsSender() {
        // When
        Message.Sender result = messageValidator.validateAndParseSender("system");

        // Then
        assertEquals(Message.Sender.SYSTEM, result);
    }

    @Test
    void validateAndParseSender_MixedCase_ReturnsSender() {
        // When
        Message.Sender result = messageValidator.validateAndParseSender("UsEr");

        // Then
        assertEquals(Message.Sender.USER, result);
    }

    @Test
    void validateAndParseSender_Null_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateAndParseSender(null);
        });

        assertEquals("Sender is required", exception.getMessage());
    }

    @Test
    void validateAndParseSender_Blank_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateAndParseSender("   ");
        });

        assertEquals("Sender is required", exception.getMessage());
    }

    @Test
    void validateAndParseSender_Empty_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateAndParseSender("");
        });

        assertEquals("Sender is required", exception.getMessage());
    }

    @Test
    void validateAndParseSender_InvalidValue_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateAndParseSender("invalid");
        });

        assertEquals("Sender must be one of: user, assistant, system", exception.getMessage());
    }

    @Test
    void validateAndParseSender_InvalidValueWithNumbers_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validateAndParseSender("user123");
        });

        assertEquals("Sender must be one of: user, assistant, system", exception.getMessage());
    }

    // validatePagination tests
    @Test
    void validatePagination_ValidPageAndSize_NoException() {
        // When & Then
        assertDoesNotThrow(() -> messageValidator.validatePagination(0, 20));
    }

    @Test
    void validatePagination_ValidMaxSize_NoException() {
        // When & Then
        assertDoesNotThrow(() -> messageValidator.validatePagination(0, ApiConstants.MAX_PAGE_SIZE));
    }

    @Test
    void validatePagination_ValidMinSize_NoException() {
        // When & Then
        assertDoesNotThrow(() -> messageValidator.validatePagination(0, 1));
    }

    @Test
    void validatePagination_NegativePage_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validatePagination(-1, 20);
        });

        assertEquals("Page must be >= 0", exception.getMessage());
    }

    @Test
    void validatePagination_ZeroSize_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validatePagination(0, 0);
        });

        assertEquals("Size must be between 1 and " + ApiConstants.MAX_PAGE_SIZE, exception.getMessage());
    }

    @Test
    void validatePagination_NegativeSize_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validatePagination(0, -1);
        });

        assertEquals("Size must be between 1 and " + ApiConstants.MAX_PAGE_SIZE, exception.getMessage());
    }

    @Test
    void validatePagination_ExceedsMaxSize_ThrowsException() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            messageValidator.validatePagination(0, ApiConstants.MAX_PAGE_SIZE + 1);
        });

        assertEquals("Size must be between 1 and " + ApiConstants.MAX_PAGE_SIZE, exception.getMessage());
    }

    @Test
    void validatePagination_LargePageNumber_NoException() {
        // When & Then
        assertDoesNotThrow(() -> messageValidator.validatePagination(100, 20));
    }
}
