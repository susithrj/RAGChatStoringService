package com.codegensis.ragstore.validator;

import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class SessionValidator {
    
    public void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ValidationException("User ID is required");
        }
        if (!userId.matches(ApiConstants.USER_ID_PATTERN)) {
            throw new ValidationException("User ID must contain only alphanumeric characters and underscores");
        }
        if (userId.length() > ApiConstants.MAX_USER_ID_LENGTH) {
            throw new ValidationException("User ID must not exceed " + ApiConstants.MAX_USER_ID_LENGTH + " characters");
        }
    }
    
    public void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Title cannot be empty");
        }
        if (title.length() > ApiConstants.MAX_TITLE_LENGTH) {
            throw new ValidationException("Title must not exceed " + ApiConstants.MAX_TITLE_LENGTH + " characters");
        }
    }
}
