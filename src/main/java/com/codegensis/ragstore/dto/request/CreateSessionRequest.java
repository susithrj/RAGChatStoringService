package com.codegensis.ragstore.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateSessionRequest(
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "User ID must contain only alphanumeric characters and underscores")
    @Size(max = 255, message = "User ID must not exceed 255 characters")
    String userId,
    
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title
) {
}
