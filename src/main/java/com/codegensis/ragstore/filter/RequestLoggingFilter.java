package com.codegensis.ragstore.filter;

import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.util.PathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(0)
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final int REQUEST_ID_LENGTH = 8;
    
    private final PathMatcher pathMatcher;
    
    public RequestLoggingFilter(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        if (pathMatcher.matchesAny(path, ApiConstants.SKIP_LOGGING_PATHS)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String requestId = UUID.randomUUID().toString().substring(0, REQUEST_ID_LENGTH);
        setMDC(requestId);
        
        long startTime = System.currentTimeMillis();
        
        try {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
            
            logRequest(request);
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Outgoing response - Status: {}, Duration: {}ms", 
                wrappedResponse.getStatus(), duration);
            
            wrappedResponse.copyBodyToResponse();
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Request processing failed - Duration: {}ms, Error: {}", 
                duration, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
    
    private void setMDC(String requestId) {
        MDC.put("requestId", requestId);
    }
    
    private void logRequest(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String queryString = request.getQueryString();
        
        logger.info("Incoming request - Method: {}, URI: {}, Query: {}, User ID: {}", 
            request.getMethod(), 
            request.getRequestURI(),
            queryString != null ? queryString : "none",
            userId != null ? userId : "none");
    }
    
}
