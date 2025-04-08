package com.example.websitebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Frontend URL (Vite dev server)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // If you want Postman to work too (no origin), allow all for dev:
//        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-CSRF-TOKEN"
        ));

        configuration.setExposedHeaders(List.of(
                "Authorization",
                "X-CSRF-TOKEN"
        ));

        // Required to allow cookies (JWT, CSRF) to be sent
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
