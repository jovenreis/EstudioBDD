package com.estudiobdd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o acesso à API somente para a origem da SPA (app.cors.origem).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String origemSpa;

    public CorsConfig(@Value("${app.cors.origem}") String origemSpa) {
        this.origemSpa = origemSpa;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origemSpa)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Content-Type", "X-Usuario-Id")
                .exposedHeaders("Content-Disposition", "Location");
    }
}
