FROM maven:3.9.14-eclipse-temurin-21-alpine AS build
WORKDIR /vv
COPY . .
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /vv
COPY --from=build /vv/target/*.jar viceVersa.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]