# Multi-stage build for Spring Boot application
FROM maven:3.9-openjdk-21-slim AS builder

# Set working directory
WORKDIR /app

# Copy Maven configuration
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:21-jdk-slim

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app directory and user
RUN groupadd -r dizzme && useradd -r -g dizzme dizzme
WORKDIR /app

# Create uploads directory
RUN mkdir -p uploads && chown -R dizzme:dizzme /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/dizzme-platform-*.jar app.jar

# Change ownership
RUN chown -R dizzme:dizzme /app

# Switch to non-root user
USER dizzme

# Expose port
EXPOSE 8080

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]