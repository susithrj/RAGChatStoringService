package com.codegensis.ragstore.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    protected ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected ApiException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getErrorType() {
        return errorCode.getType();
    }
    
    public String getErrorTitle() {
        return errorCode.getTitle();
    }
    
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
