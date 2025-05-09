FROM openjdk:21-jdk-slim AS build

WORKDIR /app

RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
