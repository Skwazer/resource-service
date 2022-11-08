FROM amazoncorretto:11-alpine-jdk
ADD web/build/libs/*SNAPSHOT.jar resource-service.jar
ENTRYPOINT ["java","-jar","resource-service.jar"]