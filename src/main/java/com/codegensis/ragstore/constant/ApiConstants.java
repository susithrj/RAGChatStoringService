package com.codegensis.ragstore.constant;

public final class ApiConstants {
    
    private ApiConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static final String API_KEY_HEADER = "X-API-Key";
    
    public static final String[] PUBLIC_ENDPOINTS = {
        "/actuator/health",
        "/actuator/health/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/h2-console/**"
    };
    
    public static final String[] SKIP_LOGGING_PATHS = {
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs",
        "/h2-console",
        "/favicon.ico"
    };
    
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_USER_ID_LENGTH = 255;
    public static final int MAX_CONTENT_LENGTH = 10000;
    public static final int MAX_CONTEXT_LENGTH = 51200;
    public static final int MAX_PAGE_SIZE = 100;
    
    public static final String USER_ID_PATTERN = "^[a-zA-Z0-9_]+$";
}
