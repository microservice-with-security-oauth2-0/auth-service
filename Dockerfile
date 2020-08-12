FROM openjdk:8-jre
MAINTAINER Seshasai <kseshasai.88@gmail.com>

# Add the service itself
#ARG JAR_FILE
#ADD target/${JAR_FILE} /usr/share/microservice-ex1/authentication-service.jar
ADD target/*.jar /usr/share/microservice-ex1/authentication-service.jar

ENTRYPOINT ["java", "-jar", "/usr/share/microservice-ex1/authentication-service.jar"]