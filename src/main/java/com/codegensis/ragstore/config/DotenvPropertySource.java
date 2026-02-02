package com.codegensis.ragstore.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * EnvironmentPostProcessor to load .env file and make it available to Spring Boot.
 * This runs before the application context is created, so @Value annotations can read from .env.
 */
public class DotenvPropertySource implements EnvironmentPostProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(DotenvPropertySource.class);
    private static final String PROPERTY_SOURCE_NAME = "dotenv";
    
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // Determine which env file to load based on active profile
            String[] activeProfiles = environment.getActiveProfiles();
            String profile = activeProfiles.length > 0 ? activeProfiles[0] : null;
            
            // Also check SPRING_PROFILES_ACTIVE system property or environment variable
            if (profile == null || profile.isEmpty()) {
                profile = System.getProperty("spring.profiles.active");
                if (profile == null || profile.isEmpty()) {
                    profile = System.getenv("SPRING_PROFILES_ACTIVE");
                }
            }
            
            File envFile = null;
            String filename = null;
            
            // Priority order:
            // 1. .env (highest priority - user override)
            // 2. env.{profile} (profile-specific, e.g., env.qa, env.prod)
            // 3. env.dev (fallback for development)
            
            File dotEnvFile = new File(".env");
            if (dotEnvFile.exists() && dotEnvFile.isFile()) {
                envFile = dotEnvFile;
                filename = ".env";
                logger.info("Using .env file (highest priority)");
            } else if (profile != null && !profile.isEmpty()) {
                // Try profile-specific file (e.g., env.qa, env.prod)
                String profileEnvFile = "env." + profile;
                File profileFile = new File(profileEnvFile);
                if (profileFile.exists() && profileFile.isFile()) {
                    envFile = profileFile;
                    filename = profileEnvFile;
                    logger.info("Using {} file for profile: {}", profileEnvFile, profile);
                }
            }
            
            // Fall back to env.dev if no other file found
            if (envFile == null) {
                File envDevFile = new File("env.dev");
                if (envDevFile.exists() && envDevFile.isFile()) {
                    envFile = envDevFile;
                    filename = "env.dev";
                    logger.info("Using env.dev file (fallback)");
                }
            }
            
            if (envFile != null && envFile.exists() && envFile.isFile()) {
                logger.info("Loading {} file for Spring Boot property resolution", filename);
                
                Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .filename(filename)
                    .ignoreIfMissing()
                    .load();
                
                Map<String, Object> properties = new HashMap<>();
                final String finalFilename = filename; // Make final for lambda
                dotenv.entries().forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    
                    // Add to property source
                    properties.put(key, value);

                    if (System.getProperty(key) == null && System.getenv(key) == null) {
                        System.setProperty(key, value);
                    }
                    
                    logger.debug("Loaded {} from {} file", key, finalFilename);
                });
                
                // Add as a property source
                MapPropertySource propertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, properties);
                environment.getPropertySources().addLast(propertySource);
                
                logger.info("Successfully loaded {} properties from {} file", properties.size(), filename);
            } else {
                logger.debug("No environment file found (.env, env.{profile}, or env.dev), skipping dotenv loading");
            }
        } catch (Exception e) {
            logger.warn("Failed to load .env file: {}. Continuing with system environment variables.", e.getMessage());
        }
    }
}
