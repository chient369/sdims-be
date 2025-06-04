FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src src/

# Build the application
RUN mvn clean package -DskipTests

# Create the runtime image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create directories for uploads and logs
RUN mkdir -p /app/uploads /app/logs

# Set volume points
VOLUME /app/uploads
VOLUME /app/logs

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 