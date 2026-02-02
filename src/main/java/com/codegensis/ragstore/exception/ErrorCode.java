package com.codegensis.ragstore.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    RESOURCE_NOT_FOUND("not-found", "Resource Not Found", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("validation-error", "Validation Error", HttpStatus.BAD_REQUEST),
    RATE_LIMIT_EXCEEDED("rate-limit-exceeded", "Rate Limit Exceeded", HttpStatus.TOO_MANY_REQUESTS),
    CONFLICT("conflict", "Resource Conflict", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR("internal-server-error", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String type;
    private final String title;
    private final HttpStatus httpStatus;
    
    ErrorCode(String type, String title, HttpStatus httpStatus) {
        this.type = type;
        this.title = title;
        this.httpStatus = httpStatus;
    }
    
    public String getType() {
        return "https://ragchat.api/errors/" + type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
