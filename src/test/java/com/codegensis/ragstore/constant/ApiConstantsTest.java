package com.codegensis.ragstore.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiConstantsTest {

    @Test
    void apiKeyHeader_HasCorrectValue() {
        assertEquals("X-API-Key", ApiConstants.API_KEY_HEADER);
    }

    @Test
    void publicEndpoints_ContainsExpectedPaths() {
        assertNotNull(ApiConstants.PUBLIC_ENDPOINTS);
        assertTrue(ApiConstants.PUBLIC_ENDPOINTS.length > 0);
        assertTrue(java.util.Arrays.asList(ApiConstants.PUBLIC_ENDPOINTS).contains("/actuator/health"));
    }

    @Test
    void skipLoggingPaths_ContainsExpectedPaths() {
        assertNotNull(ApiConstants.SKIP_LOGGING_PATHS);
        assertTrue(ApiConstants.SKIP_LOGGING_PATHS.length > 0);
    }

    @Test
    void maxLengthConstants_HavePositiveValues() {
        assertTrue(ApiConstants.MAX_TITLE_LENGTH > 0);
        assertTrue(ApiConstants.MAX_USER_ID_LENGTH > 0);
        assertTrue(ApiConstants.MAX_CONTENT_LENGTH > 0);
        assertTrue(ApiConstants.MAX_CONTEXT_LENGTH > 0);
        assertTrue(ApiConstants.MAX_PAGE_SIZE > 0);
    }

    @Test
    void userIdPattern_IsNotNull() {
        assertNotNull(ApiConstants.USER_ID_PATTERN);
        assertFalse(ApiConstants.USER_ID_PATTERN.isEmpty());
    }
}
