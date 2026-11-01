FROM eclipse-temurin:17-jdk-alpine

# Crear usuario no root
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copiamos el jar
COPY target/*.jar app.jar

# Creamos carpeta uploads y damos permisos
RUN mkdir -p /app/uploads && chown -R spring:spring /app

USER spring

EXPOSE 8082

# Perfil docker
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
