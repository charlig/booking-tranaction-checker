# Use the official OpenJDK 17 base image
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the jar file into the container at /app
COPY ./target/your-application-name.jar /app/your-application-name.jar

CMD ["java", "-jar", "/app/booking-tranaction-checker.jar"]
