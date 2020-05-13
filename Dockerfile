FROM maven:3.6-openjdk-15 AS build

WORKDIR /tmp
COPY . .
RUN mvn clean package

FROM openjdk:15-alpine
COPY --from=build /tmp/out/backend-3-jar-with-dependencies.jar ./app.jar
CMD ["java", "-jar", "./app.jar"]


