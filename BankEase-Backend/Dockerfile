# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root system user and ensure data dir permissions
RUN addgroup -S bankease && adduser -S bankease -G bankease && \
    mkdir -p /app/data && chown -R bankease:bankease /app

USER bankease

# Copy artifact from build stage
COPY --from=build /app/target/bankease-backend-1.0.0.jar app.jar

# Expose backend port
EXPOSE 8080 10000

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -q --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Environment flags and launch
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
