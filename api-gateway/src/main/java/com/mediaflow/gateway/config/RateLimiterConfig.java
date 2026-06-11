package com.mediaflow.gateway.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

    private final RateLimitProperties properties;

    public BucketConfiguration configForPlan(String plan) {
        var limit = properties.getPlanLimit(plan);

        return BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(limit.getBurst())
                        .refillGreedy(limit.getPerSecond(), Duration.ofSeconds(1))
                        .build())
                .addLimit(Bandwidth.builder()
                        .capacity(limit.getPerHour())
                        .refillGreedy(limit.getPerHour(), Duration.ofHours(1))
                        .build())
                .build();
    }

    @Bean
    public LettuceBasedProxyManager<byte[]> proxyManager(ReactiveRedisConnectionFactory connectionFactory) {
        var factory = (LettuceConnectionFactory) connectionFactory;
        var client = (RedisClient) factory.getNativeClient();
        return LettuceBasedProxyManager.builderFor(client).build();
    }
}
