# =========================
# Build stage
# =========================
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/build/libs/sweet-management-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8081

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
