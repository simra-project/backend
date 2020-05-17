FROM maven:3.6-openjdk-15 AS build

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /build/src/
RUN mvn package

FROM openjdk:15-alpine
ENV API_SECRET="mysecret"
COPY --from=build /build/out/backend-3-jar-with-dependencies.jar ./app.jar

COPY ./start.sh ./start.sh
RUN sed -i 's/\r$//' ./start.sh  && chmod +x ./start.sh

CMD ["sh", "./start.sh"]


