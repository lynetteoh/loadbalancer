FROM maven:latest AS BUILD
WORKDIR /app
COPY . /app
RUN mvn clean install -DskipTests

FROM amazoncorretto:20-al2-jdk
COPY --from=BUILD /app/target/loadbalancer-0.0.1-SNAPSHOT.jar loadbalancer.jar
ENTRYPOINT ["java", "-jar", "loadbalancer.jar"]
EXPOSE 8888