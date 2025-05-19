# Build with Gradle
FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY . .

RUN ./gradlew bootJar --no-daemon

# Build docker image
FROM eclipse-temurin:23-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
