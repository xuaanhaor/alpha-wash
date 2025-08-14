# Stage 1: Builder
FROM arm64v8/eclipse-temurin:17-jdk-focal as builder

WORKDIR /app

COPY gradlew build.gradle settings.gradle gradle.properties ./
RUN chmod +x ./gradlew

COPY gradle ./gradle

RUN apt-get update && apt-get install -y findutils

RUN ./gradlew dependencies --no-daemon || true

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runner
FROM eclipse-temurin:17-jdk-jammy

ENV TZ=Asia/Ho_Chi_Minh

RUN apt-get update && \
    apt-get install -y postgresql-client bash dos2unix && \
    rm -rf /var/lib/apt/lists/*

# Copy the built jar file and healthcheck script
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY script/data-healthcheck.sh /data-healthcheck.sh

RUN dos2unix /data-healthcheck.sh && \
    chmod +x /data-healthcheck.sh

EXPOSE 8080
ENTRYPOINT ["/bin/bash", "-c", "/data-healthcheck.sh && java $JAVA_OPTS -jar /app/app.jar"]