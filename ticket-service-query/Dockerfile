FROM eclipse-temurin:21-jre-alpine


WORKDIR /app


COPY target/ticket-query-service-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8505


ENTRYPOINT ["java", "-jar", "app.jar"]