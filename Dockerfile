FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app
COPY build.gradle .
COPY settings.gradle .
COPY src ./src
RUN gradle clean build -x test
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar gym.jar
ENTRYPOINT ["java", "-jar", "gym.jar","--debug"]