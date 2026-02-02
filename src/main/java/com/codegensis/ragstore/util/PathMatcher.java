package com.codegensis.ragstore.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for matching request paths against pattern arrays.
 * Supports exact path matching and wildcard patterns ending with "/**".
 * Used by filters to determine which endpoints should be processed or skipped.
 */
@Component
public class PathMatcher {
    
    public boolean matchesAny(String requestPath, String[] patterns) {
        for (String pattern : patterns) {
            if (matchesPattern(requestPath, pattern)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean matchesPattern(String requestPath, String pattern) {
        if (pattern.equals(requestPath)) {
            return true;
        }
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return requestPath.startsWith(prefix);
        }
        return false;
    }
}
