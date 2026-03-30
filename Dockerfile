FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S sentinel && adduser -S sentinel -G sentinel
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /uploads && chown sentinel:sentinel /uploads
USER sentinel
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
