FROM eclipse-temurin:25-jre-alpine

WORKDIR /chatapp

COPY ./target/chatapp-0.0.1-SNAPSHOT.jar ./application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]