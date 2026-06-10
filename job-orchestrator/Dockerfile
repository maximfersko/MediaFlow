FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY api-gateway/build.gradle.kts api-gateway/
COPY job-orchestrator/build.gradle.kts job-orchestrator/
COPY notification-service/build.gradle.kts notification-service/
RUN gradle :job-orchestrator:dependencies --no-daemon
COPY job-orchestrator/src job-orchestrator/src
RUN gradle :job-orchestrator:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/job-orchestrator/build/libs/*.jar app.jar
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
