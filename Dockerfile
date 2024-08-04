# Use an official Maven image as a parent image
FROM maven:3.8.6-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy the source code into the container
COPY src ./src

# Run the Maven build
RUN mvn clean install -DskipTests

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the executable JAR file from the host to the container
COPY target/survey_client-0.0.1-SNAPSHOT.jar /app/client-service.jar

# Expose the port that the service will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "client-service.jar"]