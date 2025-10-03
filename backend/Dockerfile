# Multi-stage build for Spring Boot application
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY src ./src

# Copy Gradle wrapper
COPY gradlew .
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]