package com.codegensis.ragstore.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {
    
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);
    
    private final int apiKeyPerMinute;
    private final Map<String, Bucket> apiKeyBuckets = new ConcurrentHashMap<>();
    
    public RateLimitConfig(@Value("${app.rate-limit.api-key-per-minute}") int apiKeyPerMinute) {
        this.apiKeyPerMinute = apiKeyPerMinute;
    }
    
    public Bucket getApiKeyBucket(String apiKey) {
        return apiKeyBuckets.computeIfAbsent(apiKey, key -> createBucket(apiKeyPerMinute));
    }
    
    private Bucket createBucket(int capacity) {
        Refill refill = Refill.intervally(capacity, REFILL_DURATION);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    public void resetBuckets() {
        apiKeyBuckets.clear();
    }
}
