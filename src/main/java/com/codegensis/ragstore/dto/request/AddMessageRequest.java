package com.codegensis.ragstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddMessageRequest(
    @NotBlank(message = "Sender is required")
    String sender,
    
    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10,000 characters")
    String content,
    
    @Size(max = 51200, message = "Context must not exceed 50KB")
    String context
) {
}
