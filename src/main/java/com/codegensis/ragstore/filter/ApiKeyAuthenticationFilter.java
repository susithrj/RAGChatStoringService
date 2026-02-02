package com.codegensis.ragstore.filter;

import com.codegensis.ragstore.constant.ApiConstants;
import com.codegensis.ragstore.util.PathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Order(2)
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
    
    private final String primaryApiKey;
    private final String secondaryApiKey;
    private final PathMatcher pathMatcher;
    
    public ApiKeyAuthenticationFilter(
            @Value("${app.api.key.primary:}") String primaryApiKey,
            @Value("${app.api.key.secondary:}") String secondaryApiKey,
            PathMatcher pathMatcher) {
        this.primaryApiKey = primaryApiKey;
        this.secondaryApiKey = secondaryApiKey;
        this.pathMatcher = pathMatcher;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        if (pathMatcher.matchesAny(requestPath, ApiConstants.PUBLIC_ENDPOINTS)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String apiKey = request.getHeader(ApiConstants.API_KEY_HEADER);
        
        if (apiKey == null || apiKey.isBlank()) {
            sendUnauthorizedResponse(response, "Missing API key");
            return;
        }
        
        if (isValidApiKey(apiKey)) {
            Authentication authentication = new ApiKeyAuthentication(apiKey);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            logger.warn("Authentication failed - Invalid API key for URI: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "Invalid API key");
        }
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\":\"%s\"}", message));
    }
    
    private boolean isValidApiKey(String apiKey) {
        if (primaryApiKey == null || primaryApiKey.isBlank()) {
            return false;
        }
        
        boolean matchesPrimary = Objects.equals(apiKey, primaryApiKey);
        boolean matchesSecondary = secondaryApiKey != null && 
                                   !secondaryApiKey.isBlank() && 
                                   Objects.equals(apiKey, secondaryApiKey);
        
        return matchesPrimary || matchesSecondary;
    }
    
    private static class ApiKeyAuthentication implements Authentication {
        private final String apiKey;
        private final boolean authenticated = true;
        
        public ApiKeyAuthentication(String apiKey) {
            this.apiKey = apiKey;
        }
        
        @Override
        public java.util.Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        
        @Override
        public Object getCredentials() {
            return apiKey;
        }
        
        @Override
        public Object getDetails() {
            return null;
        }
        
        @Override
        public Object getPrincipal() {
            return apiKey;
        }
        
        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }
        
        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            // Immutable
        }
        
        @Override
        public String getName() {
            return "API_KEY";
        }
    }
}
