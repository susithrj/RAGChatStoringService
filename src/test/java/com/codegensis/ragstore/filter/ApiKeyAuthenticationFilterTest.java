package com.codegensis.ragstore.filter;

import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.util.PathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiKeyAuthenticationFilterTest {

    private ApiKeyAuthenticationFilter filter;
    private PathMatcher pathMatcher;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        pathMatcher = mock(PathMatcher.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_PublicEndpoint_SkipsAuthentication() throws Exception {
        // Given
        filter = new ApiKeyAuthenticationFilter("key1", "key2", pathMatcher);
        when(request.getRequestURI()).thenReturn("/actuator/health");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(true);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void doFilterInternal_MissingApiKey_ReturnsUnauthorized() throws Exception {
        // Given
        filter = new ApiKeyAuthenticationFilter("key1", "key2", pathMatcher);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn(null);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_ValidPrimaryApiKey_Authenticates() throws Exception {
        // Given
        filter = new ApiKeyAuthenticationFilter("key1", "key2", pathMatcher);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn("key1");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidSecondaryApiKey_Authenticates() throws Exception {
        // Given
        filter = new ApiKeyAuthenticationFilter("key1", "key2", pathMatcher);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn("key2");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_InvalidApiKey_ReturnsUnauthorized() throws Exception {
        // Given
        filter = new ApiKeyAuthenticationFilter("key1", "key2", pathMatcher);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn("invalid-key");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_BlankApiKey_ReturnsUnauthorized() throws Exception {
        // Given
        filter = new ApiKeyAuthenticationFilter("key1", "key2", pathMatcher);
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getHeader(ApiConstants.API_KEY_HEADER)).thenReturn("   ");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
