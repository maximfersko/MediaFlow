package com.mediaflow.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private boolean enabled;
    private Map<String, PlanLimit> plans = new HashMap<>();


    @Data
    public static class PlanLimit {
        private int perSecond;
        private int burst;
        private int perHour;
    }

    public PlanLimit getPlanLimit(String plan) {
        return plans.getOrDefault(plan,
                plans.getOrDefault("BASIC", new PlanLimit()));
    }
}
