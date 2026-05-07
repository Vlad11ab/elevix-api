# syntax=docker/dockerfile:1.7

# ===== Stage 1: build =====
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw \
 && chmod +x mvnw \
 && ./mvnw -B -q -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -q -DskipTests package

# ===== Stage 2: runtime =====
FROM eclipse-temurin:21-jre

RUN groupadd --system app \
 && useradd --system --gid app --create-home app

USER app
WORKDIR /app

COPY --from=build --chown=app:app /workspace/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
