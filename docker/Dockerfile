# Multi-stage build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ src/
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: non-root user
RUN addgroup -g 1001 ftms && adduser -u 1001 -G ftms ftms

COPY --from=builder /app/target/ftms-backend-*.jar app.jar
RUN chown -R ftms:ftms /app

USER ftms

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseStringDeduplication", "-XX:+UseG1GC", \
            "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", \
            "-jar", "app.jar"]