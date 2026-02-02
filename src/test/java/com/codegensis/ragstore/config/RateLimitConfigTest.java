package com.codegensis.ragstore.config;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitConfigTest {

    private RateLimitConfig rateLimitConfig;

    @BeforeEach
    void setUp() {
        rateLimitConfig = new RateLimitConfig(60);
    }

    @Test
    void getApiKeyBucket_SameKey_ReturnsSameBucket() {
        // When
        Bucket bucket1 = rateLimitConfig.getApiKeyBucket("key1");
        Bucket bucket2 = rateLimitConfig.getApiKeyBucket("key1");

        // Then
        assertNotNull(bucket1);
        assertNotNull(bucket2);
        assertEquals(bucket1, bucket2);
    }

    @Test
    void getApiKeyBucket_DifferentKeys_ReturnsDifferentBuckets() {
        // When
        Bucket bucket1 = rateLimitConfig.getApiKeyBucket("key1");
        Bucket bucket2 = rateLimitConfig.getApiKeyBucket("key2");

        // Then
        assertNotNull(bucket1);
        assertNotNull(bucket2);
        assertNotEquals(bucket1, bucket2);
    }

    @Test
    void getApiKeyBucket_ConsumesTokens() {
        // Given
        Bucket bucket = rateLimitConfig.getApiKeyBucket("test-key");

        // When & Then
        assertTrue(bucket.tryConsume(1));
        assertTrue(bucket.tryConsume(1));
    }

    @Test
    void resetBuckets_ClearsAllBuckets() {
        // Given
        Bucket bucket1 = rateLimitConfig.getApiKeyBucket("key1");
        Bucket bucket2 = rateLimitConfig.getApiKeyBucket("key2");

        // When
        rateLimitConfig.resetBuckets();
        Bucket bucket1After = rateLimitConfig.getApiKeyBucket("key1");
        Bucket bucket2After = rateLimitConfig.getApiKeyBucket("key2");

        // Then
        assertNotEquals(bucket1, bucket1After);
        assertNotEquals(bucket2, bucket2After);
    }
}
