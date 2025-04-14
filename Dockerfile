FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /app

COPY pom.xml .
COPY src src
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:8-jre
WORKDIR /app

COPY --from=build /app/target/starwars-api-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]