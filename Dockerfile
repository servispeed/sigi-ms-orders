FROM docker.io/eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY target/*.jar app.jar

RUN mkdir -p /app/uploads && chown -R spring:spring /app

USER spring

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]