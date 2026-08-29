FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

# Resolve dependencies from the build scripts alone: this layer then survives any source change.
# .env.dev is required because build.gradle reads it at configuration time to set up Flyway.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle .env.dev ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon

# A JRE is enough to run the jar; the JDK only belongs in the build stage.
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

USER 1000
EXPOSE 3568

CMD ["java", "-jar", "app.jar"]
