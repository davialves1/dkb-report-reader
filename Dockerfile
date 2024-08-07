FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]