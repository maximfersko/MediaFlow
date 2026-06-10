package com.mediaflow.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("job-orchestrator", r -> r
                        .path("/api/jobs/**")
                        .uri("http://job-orchestrator:8081"))
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("http://notification-service:8082"))
                .route("keycloak", r -> r
                        .path("/auth/**")
                        .uri("http://keycloak:8180"))
                .build();
    }

}
