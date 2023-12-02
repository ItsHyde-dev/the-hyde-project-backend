FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
COPY .env .env
COPY root.crt /root/.postgresql/root.crt
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
