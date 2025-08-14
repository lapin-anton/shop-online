FROM openjdk:21-jdk
WORKDIR /app
COPY ./target/*-exec.jar app.jar
EXPOSE 8280
ENTRYPOINT ["java", "-jar", "app.jar"]
