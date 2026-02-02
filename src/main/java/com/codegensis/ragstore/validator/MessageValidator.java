package com.codegensis.ragstore.validator;

import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.entity.Message;
import com.codegensis.ragstore.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class MessageValidator {
    
    public void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ValidationException("Content is required");
        }
        if (content.length() > ApiConstants.MAX_CONTENT_LENGTH) {
            throw new ValidationException("Content must not exceed " + ApiConstants.MAX_CONTENT_LENGTH + " characters");
        }
    }
    
    public void validateContext(String context) {
        if (context != null && context.length() > ApiConstants.MAX_CONTEXT_LENGTH) {
            throw new ValidationException("Context must not exceed 50KB");
        }
    }
    
    public Message.Sender validateAndParseSender(String sender) {
        if (sender == null || sender.isBlank()) {
            throw new ValidationException("Sender is required");
        }
        try {
            return Message.Sender.valueOf(sender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Sender must be one of: user, assistant, system");
        }
    }
    
    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ValidationException("Page must be >= 0");
        }
        if (size < 1 || size > ApiConstants.MAX_PAGE_SIZE) {
            throw new ValidationException("Size must be between 1 and " + ApiConstants.MAX_PAGE_SIZE);
        }
    }
}
