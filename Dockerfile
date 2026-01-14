# Use Eclipse Temurin JDK 21 base image
FROM eclipse-temurin:21-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
# Set working directory in container
WORKDIR /app

# Copy Gradle wrapper & build files
COPY . .

# Make sure wrapper is executable
RUN chmod +x ./gradlew

# Build Spring Boot app using Gradle wrapper
RUN ./gradlew bootJar

# Expose app port (Render sets $PORT env var)
EXPOSE 8080

# Run the built JAR, using the $PORT provided by Render
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar build/libs/*.jar"]