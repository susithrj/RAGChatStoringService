package com.codegensis.ragstore.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String type,
    String title,
    Integer status,
    String detail,
    String instance,
    Integer retryAfter,
    LocalDateTime timestamp
) {
    public ErrorResponse(String type, String title, Integer status, String detail, String instance) {
        this(type, title, status, detail, instance, null, LocalDateTime.now());
    }
    
    public ErrorResponse withRetryAfter(Integer retryAfter) {
        return new ErrorResponse(type, title, status, detail, instance, retryAfter, timestamp);
    }
}
