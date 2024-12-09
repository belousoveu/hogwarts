FROM openjdk:17-jdk-alpine
LABEL authors="ChessMaster"

WORKDIR /app
COPY target/hogwarts-0.0.1-SNAPSHOT.jar /app/hogwarts-0.0.1-SNAPSHOT.jar
EXPOSE 8080

CMD ["java", "-jar", "hogwarts-0.0.1-SNAPSHOT.jar"]