package com.codegensis.ragstore.exception;

public class ResourceNotFoundException extends ApiException {
    
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("%s with id %d not found", resource, id));
    }
}
