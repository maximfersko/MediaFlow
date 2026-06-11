package com.mediaflow.gateway.filter;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-200)
@RequiredArgsConstructor
public class TraceHeaderWebFilter implements WebFilter {

    private final Tracer tracer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.deferContextual(ctx -> {
            var span = tracer.currentSpan();
            if (span != null) {
                String traceId = span.context().traceId();
                exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
                log.debug("traceId={}", traceId);
            } else {
                log.warn("currentSpan is null");
            }
            return chain.filter(exchange);
        });
    }
}
