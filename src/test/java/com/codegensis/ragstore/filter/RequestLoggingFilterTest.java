package com.codegensis.ragstore.filter;

import com.codegensis.ragstore.util.PathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestLoggingFilterTest {

    private RequestLoggingFilter filter;
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
        filter = new RequestLoggingFilter(pathMatcher);
        MDC.clear();
    }

    @Test
    void doFilterInternal_SkipLoggingPath_ContinuesWithoutLogging() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(true);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("requestId"));
    }

    @Test
    void doFilterInternal_RegularPath_LogsAndContinues() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn("userId=test");
        when(request.getParameter("userId")).thenReturn("test");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(any(), any());
        assertNull(MDC.get("requestId")); // Cleared in finally
    }

    @Test
    void doFilterInternal_Exception_LogsErrorAndClearsMDC() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api/v1/sessions");
        when(pathMatcher.matchesAny(anyString(), any())).thenReturn(false);
        when(request.getMethod()).thenReturn("GET");
        doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(any(), any());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });
        
        assertNull(MDC.get("requestId")); // Cleared in finally
    }
}
