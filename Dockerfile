FROM maven:latest AS BUILD
WORKDIR /app
COPY . /app
RUN mvn clean install -DskipTests

FROM amazoncorretto:20-al2-jdk
COPY --from=BUILD /app/target/echoserver-0.0.1-SNAPSHOT.jar echoserver.jar
ENTRYPOINT ["java", "-jar", "echoserver.jar"]
EXPOSE 8888