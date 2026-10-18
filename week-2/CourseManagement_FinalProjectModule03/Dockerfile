# ===================================================================
# Stage 1: Build the Spring Boot application using Gradle Wrapper
# ===================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Update CA certificates and sync to Java truststore (official Temurin mechanism)
RUN update-ca-certificates && \
    USE_SYSTEM_CA_CERTS=true /__cacert_entrypoint.sh true

# Copy Gradle wrapper and configuration files first (leverage Docker layer caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle* ./

# Ensure gradlew has unix line-endings and execution rights
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Download project dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy application source code
COPY src src

# Build bootable JAR without running tests (tests run separately via Gradle test)
RUN ./gradlew bootJar --no-daemon -x test

# ===================================================================
# Stage 2: Minimal, secure runtime image
# ===================================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Ensure CA certificates are synchronized to Java truststore for outbound HTTPS
RUN update-ca-certificates && \
    USE_SYSTEM_CA_CERTS=true /__cacert_entrypoint.sh true

# Run as non-root user for security best practices
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy executable jar from builder stage
COPY --from=builder /build/build/libs/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Configure JVM flags and launch Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
