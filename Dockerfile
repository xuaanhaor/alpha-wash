# Stage 1: Builder
FROM openjdk:17-alpine AS builder

WORKDIR /app

# Copy gradle config files
COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY gradle/wrapper ./gradle/wrapper

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copy full source code
COPY . .
# Build jar file
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runner
FROM eclipse-temurin:17-jre-alpine

ENV TZ=Asia/Ho_Chi_Minh

RUN apk add --no-cache postgresql-client

# Copy the built jar file and healthcheck script
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY script/data-healthcheck.sh /data-healthcheck.sh
RUN chmod +x /data-healthcheck.sh

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "/data-healthcheck.sh && java $JAVA_OPTS -jar /app/app.jar"]