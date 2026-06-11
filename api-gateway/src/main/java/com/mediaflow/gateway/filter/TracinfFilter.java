package com.mediaflow.gateway.filter;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class TracinfFilter implements GlobalFilter, Ordered {

    private final Tracer tracer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.deferContextual(ctx -> {
            var span = tracer.currentSpan();

            return ReactiveSecurityContextHolder.getContext()
                    .doOnNext(secCtx -> {
                        var auth = secCtx.getAuthentication();
                        if (auth != null && span != null) {
                            span.tag("user.id", auth.getName());
                            log.debug("tagged span user.id={}", auth.getName());
                        }
                    })
                    .then(chain.filter(exchange));
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

}
