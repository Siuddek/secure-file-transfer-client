FROM openjdk:11-jre-slim
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN apt-get update && apt-get install libgtk-3-0 libglu1-mesa -y && apt-get update
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
