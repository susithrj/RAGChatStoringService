package com.codegensis.ragstore.exception;

public class RateLimitExceededException extends ApiException {
    
    public RateLimitExceededException(String message) {
        super(ErrorCode.RATE_LIMIT_EXCEEDED, message);
    }
}
