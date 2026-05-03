package com.monstertrials.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    private static final String[] DEFAULT_ALLOWED_ORIGINS = {"http://localhost:5173", "http://127.0.0.1:5173"};

    private final String allowedOrigins;

    public CorsConfig(@Value("${monster-trial.cors.allowed-origins:}") String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        String[] origins = parseAllowedOrigins();
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("Accept", "Content-Type", "Origin")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    private String[] parseAllowedOrigins() {
        String[] parsed = Arrays.stream(allowedOrigins == null ? "".split(",") : allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .distinct()
                .toArray(String[]::new);
        return parsed.length == 0 ? DEFAULT_ALLOWED_ORIGINS : parsed;
    }
}
