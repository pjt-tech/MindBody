FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./build/libs/MindBody.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
