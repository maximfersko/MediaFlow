package com.mediaflow.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserContextFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {

                    Authentication authentication = securityContext.getAuthentication();

                    if (authentication == null || !authentication.isAuthenticated()) {
                        return chain.filter(exchange);
                    }

                    if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
                        return chain.filter(exchange);
                    }

                    String userId = jwt.getSubject();

                    log.info("user id: {}", userId);

                    String roles = extractRoles(jwt);

                    log.info("roles: {}", roles);

                    String plan = jwt.getClaimAsString("plan");
                    if (plan == null || plan.isBlank()) {
                        plan = "BASIC";
                    }

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Roles", roles)
                            .header("X-User-Plan", plan)
                            .build();


                    return chain.filter(
                            exchange.mutate().request(mutatedRequest).build()
                    );

                }).switchIfEmpty(chain.filter(exchange));

    }

    private String extractRoles(Jwt jwt) {
        try {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return "";

            Object rolesObj = realmAccess.get("roles");
            if (!(rolesObj instanceof List<?> roleList)) return "";

            return roleList.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            log.warn("Failed to extract roles from JWT", e);
            return "";
        }
    }

    @Override
    public int getOrder() {
        return -90;
    }
}
