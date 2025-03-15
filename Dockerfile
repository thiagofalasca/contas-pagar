FROM eclipse-temurin:21-jdk AS builder

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]