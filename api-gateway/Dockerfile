FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY api-gateway/build.gradle.kts api-gateway/
COPY job-orchestrator/build.gradle.kts job-orchestrator/
COPY notification-service/build.gradle.kts notification-service/
RUN --mount=type=cache,target=/root/.gradle \
    gradle :api-gateway:dependencies --configuration compileClasspath --no-daemon || \
    gradle :api-gateway:dependencies --configuration compileClasspath --no-daemon --refresh-dependencies && \
    gradle :api-gateway:dependencies --configuration runtimeClasspath --no-daemon || \
    gradle :api-gateway:dependencies --configuration runtimeClasspath --no-daemon --refresh-dependencies
COPY api-gateway/src api-gateway/src
RUN --mount=type=cache,target=/root/.gradle \
    gradle :api-gateway:bootJar --no-daemon -x test || \
    gradle :api-gateway:bootJar --no-daemon -x test --refresh-dependencies || \
    gradle :api-gateway:bootJar --no-daemon -x test --refresh-dependencies

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/api-gateway/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
