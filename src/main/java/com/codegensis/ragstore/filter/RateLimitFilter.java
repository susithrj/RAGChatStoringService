package com.codegensis.ragstore.filter;

import com.codegensis.ragstore.config.RateLimitConfig;
import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.exception.ErrorCode;
import com.codegensis.ragstore.util.PathMatcher;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    private static final int RETRY_AFTER_SECONDS = 60;
    
    private final RateLimitConfig rateLimitConfig;
    private final PathMatcher pathMatcher;
    
    public RateLimitFilter(RateLimitConfig rateLimitConfig, PathMatcher pathMatcher) {
        this.rateLimitConfig = rateLimitConfig;
        this.pathMatcher = pathMatcher;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        if (pathMatcher.matchesAny(path, ApiConstants.SKIP_LOGGING_PATHS)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String apiKey = request.getHeader(ApiConstants.API_KEY_HEADER);
        
        if (apiKey != null && !apiKey.isBlank()) {
            Bucket apiKeyBucket = rateLimitConfig.getApiKeyBucket(apiKey);
            if (!apiKeyBucket.tryConsume(1)) {
                logger.warn("Rate limit exceeded - API key limit for URI: {}", request.getRequestURI());
                handleRateLimitExceeded(response, "API key rate limit exceeded");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void handleRateLimitExceeded(HttpServletResponse response, String message) throws IOException {
        ErrorCode errorCode = ErrorCode.RATE_LIMIT_EXCEEDED;
        HttpStatus status = errorCode.getHttpStatus();
        
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setHeader("Retry-After", String.valueOf(RETRY_AFTER_SECONDS));
        response.getWriter().write(String.format(
            "{\"type\":\"%s\"," +
            "\"title\":\"%s\"," +
            "\"status\":%d," +
            "\"detail\":\"%s\"," +
            "\"retryAfter\":%d}",
            errorCode.getType(),
            errorCode.getTitle(),
            status.value(),
            message,
            RETRY_AFTER_SECONDS));
    }
}
