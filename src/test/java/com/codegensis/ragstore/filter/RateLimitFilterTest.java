package com.codegensis.ragstore.filter;

import com.codegensis.ragstore.config.RateLimitConfig;
import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.util.PathMatcher;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter filter;
    private RateLimitConfig rateLimitConfig;
    private PathMatcher pathMatcher;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitConfig = mock(RateLimitConfig.class);
        pathMatcher = mock(PathMatcher.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        filter = new RateLimitFilter(rateLimitConfig, pathMatcher);
    }

    @Test
    void doFilterInternal_SkipLoggingPath_ContinuesFilterChain() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(true);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(rateLimitConfig, never()).getApiKeyBucket(anyString());
    }

    @Test
    void doFilterInternal_NoApiKey_ContinuesFilterChain() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(rateLimitConfig, never()).getApiKeyBucket(anyString());
    }

    @Test
    void doFilterInternal_ValidApiKeyWithinLimit_ContinuesFilterChain() throws Exception {
        // Given
        Bucket bucket = mock(Bucket.class);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn("test-key");
        when(rateLimitConfig.getApiKeyBucket("test-key")).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_RateLimitExceeded_Returns429() throws Exception {
        // Given
        Bucket bucket = mock(Bucket.class);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn("test-key");
        when(rateLimitConfig.getApiKeyBucket("test-key")).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(false);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(429);
        verify(response).setContentType("application/json");
        verify(response).setHeader("Retry-After", "60");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_BlankApiKey_ContinuesFilterChain() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn("   ");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(rateLimitConfig, never()).getApiKeyBucket(anyString());
    }
}
