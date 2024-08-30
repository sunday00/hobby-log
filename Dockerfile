FROM openjdk:21-jdk-slim

RUN apt update && \
    apt install bash && \
    apt install procps && \
    apt install vim

RUN dpkg -S /usr/bin/nohup

COPY ./restart.sh ./restart.sh

#java -Dspring.profiles.active=prod -jar jar/app.jar &
