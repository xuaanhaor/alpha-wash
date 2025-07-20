# Stage 1: Builder
FROM gradle:8.4.1-jdk17-alpine AS builder

WORKDIR /app

# Copy gradle config files
COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY gradle/wrapper ./gradle/wrapper

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copy full source code
COPY . .

# build the JAR file
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runner
FROM eclipse-temurin:17-jre-alpine

ENV TZ=Asia/Ho_Chi_Minh

# Install Postgres client (for pg_isready)
RUN apk add --no-cache postgresql-client bash

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy and setup the healthcheck script
COPY script/data-healthcheck.sh /data-healthcheck.sh
RUN chmod +x /data-healthcheck.sh

EXPOSE 8080

# Run healthcheck + app
ENTRYPOINT ["/bin/bash", "-c", "/data-healthcheck.sh && exec java $JAVA_OPTS -jar /app/app.jar"]
