# Base Image
FROM eclipse-temurin:17-jdk

# Copy the jar into the container
COPY target/config-server-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8888
# Start the application
ENTRYPOINT ["java", "-jar", "/app.jar"]