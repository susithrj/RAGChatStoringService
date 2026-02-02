package com.codegensis.ragstore.config;

import com.codegensis.ragstore.filter.ApiKeyAuthenticationFilter;
import com.codegensis.ragstore.filter.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final RateLimitFilter rateLimitFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final CorsConfig corsConfig;
    
    public SecurityConfig(RateLimitFilter rateLimitFilter,
                         ApiKeyAuthenticationFilter apiKeyAuthenticationFilter,
                         CorsConfig corsConfig) {
        this.rateLimitFilter = rateLimitFilter;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
        this.corsConfig = corsConfig;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/health/**", "/swagger-ui/**", 
                                "/v3/api-docs/**", "/swagger-ui.html", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            );
        
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        
        return http.build();
    }
}
