# Stage 1: Builder
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy gradle config files
COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copy full source code
COPY . .
# Build jar file
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runner
FROM eclipse-temurin:17-jre-alpine

ENV TZ=Asia/Ho_Chi_Minh
ENV SPRING_PROFILES_ACTIVE=prod

RUN apk add --no-cache postgresql-client && \
    apk add --no-cache bash && \
    apk add --no-cache dos2unix

# Copy the built jar file and healthcheck script
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY script/data-healthcheck.sh /data-healthcheck.sh

RUN dos2unix /data-healthcheck.sh && \
    chmod +x /data-healthcheck.sh

EXPOSE 8080
# Khong chay healthcheck vi tren Railway khong co container db noi bo
# Neon PostgreSQL luon san sang, khong can cho
ENTRYPOINT ["/bin/bash", "-c", "java $JAVA_OPTS -jar /app/app.jar"]