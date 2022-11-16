FROM openjdk:11-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
