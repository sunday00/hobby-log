FROM openjdk:21-jdk-slim

RUN apt update && \
    apt install bash && \
    apt install procps && \
    apt install vim

#java -Dspring.profiles.active=prod -jar jar/app.jar &
