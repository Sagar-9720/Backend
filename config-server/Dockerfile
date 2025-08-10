# Multi-stage build for better security and smaller image size
# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -g 1001 spring && \
    adduser -D -u 1001 -G spring spring

COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown spring:spring app.jar
USER spring

EXPOSE 8888

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8888/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
