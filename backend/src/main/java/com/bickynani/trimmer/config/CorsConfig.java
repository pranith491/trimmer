package com.bickynani.trimmer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${app.frontend-origin:http://localhost:5500}")
    private String frontendOrigin;

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(frontendOrigin, "http://127.0.0.1:5500", "http://localhost:5500")
                    .allowedMethods("GET", "POST", "PATCH", "OPTIONS")
                    .allowedHeaders("*");
            }
        };
    }
}
