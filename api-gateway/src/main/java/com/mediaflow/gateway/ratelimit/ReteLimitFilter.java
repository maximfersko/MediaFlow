package com.mediaflow.gateway.ratelimit;

import com.mediaflow.gateway.config.RateLimiterConfig;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReteLimitFilter implements GlobalFilter, Ordered {

    private final RateLimiterConfig rateLimiterConfig;
    private final LettuceBasedProxyManager<byte[]> proxyManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String plan = exchange.getRequest().getHeaders().getFirst("X-User-Plan");

        byte[] key = ("rl:" + plan + ":" + userId).getBytes();

        if (userId == null) return chain.filter(exchange);

        return Mono.fromCallable(() -> {
                    var syncBucket = proxyManager.builder()
                            .build(key, () -> rateLimiterConfig.configForPlan(plan));
                    return syncBucket.tryConsumeAndReturnRemaining(1);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Rate limit error: {}", e.getMessage()))
                .flatMap(probe -> {
                    log.debug("userId={} plan={} consumed={} remaining={}",
                            userId, plan, probe.isConsumed(), probe.getRemainingTokens());
                    if (probe.isConsumed()) {
                        return chain.filter(exchange);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}
