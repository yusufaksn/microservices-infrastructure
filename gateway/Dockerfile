FROM eclipse-temurin:21-jre-alpine


WORKDIR /app

COPY target/gateway-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8504


ENTRYPOINT ["java", "-jar", "app.jar"]