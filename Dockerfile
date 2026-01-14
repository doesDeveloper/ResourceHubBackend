# Get lightweight jdk just for building we use our own host gradle
FROM eclipse-temurin:21-jdk-alpine

# Create a user for security reason
RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

WORKDIR /app

ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]