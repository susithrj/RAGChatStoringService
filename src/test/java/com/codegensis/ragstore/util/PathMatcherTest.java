package com.codegensis.ragstore.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathMatcherTest {

    private PathMatcher pathMatcher;

    @BeforeEach
    void setUp() {
        pathMatcher = new PathMatcher();
    }

    @Test
    void matchesAny_ExactMatch_ReturnsTrue() {
        // Given
        String requestPath = "/actuator/health";
        String[] patterns = {"/actuator/health", "/swagger-ui"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_WildcardMatch_ReturnsTrue() {
        // Given
        String requestPath = "/actuator/health/db";
        String[] patterns = {"/actuator/health/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_NoMatch_ReturnsFalse() {
        // Given
        String requestPath = "/api/v1/sessions";
        String[] patterns = {"/actuator/health", "/swagger-ui/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertFalse(result);
    }

    @Test
    void matchesAny_EmptyPatterns_ReturnsFalse() {
        // Given
        String requestPath = "/api/v1/sessions";
        String[] patterns = {};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertFalse(result);
    }

    @Test
    void matchesAny_WildcardPrefixMatch_ReturnsTrue() {
        // Given
        String requestPath = "/swagger-ui/index.html";
        String[] patterns = {"/swagger-ui/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_WildcardExactPrefix_ReturnsTrue() {
        // Given
        String requestPath = "/swagger-ui";
        String[] patterns = {"/swagger-ui/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_WildcardPartialMatch_ReturnsFalse() {
        // Given
        String requestPath = "/api/swagger-ui";
        String[] patterns = {"/swagger-ui/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertFalse(result);
    }

    @Test
    void matchesAny_MultiplePatternsFirstMatches_ReturnsTrue() {
        // Given
        String requestPath = "/actuator/health";
        String[] patterns = {"/actuator/health", "/swagger-ui", "/api/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_MultiplePatternsLastMatches_ReturnsTrue() {
        // Given
        String requestPath = "/api/v1/sessions";
        String[] patterns = {"/actuator/health", "/swagger-ui", "/api/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_WildcardWithSlash_ReturnsTrue() {
        // Given
        String requestPath = "/h2-console/";
        String[] patterns = {"/h2-console/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_WildcardWithNestedPath_ReturnsTrue() {
        // Given
        String requestPath = "/actuator/health/db/status";
        String[] patterns = {"/actuator/health/**"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_ExactMatchCaseSensitive_ReturnsTrue() {
        // Given
        String requestPath = "/Actuator/Health";
        String[] patterns = {"/Actuator/Health"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertTrue(result);
    }

    @Test
    void matchesAny_ExactMatchCaseMismatch_ReturnsFalse() {
        // Given
        String requestPath = "/actuator/health";
        String[] patterns = {"/Actuator/Health"};

        // When
        boolean result = pathMatcher.matchesAny(requestPath, patterns);

        // Then
        assertFalse(result);
    }
}
