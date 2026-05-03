FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S monstertrials && adduser -S monstertrials -G monstertrials
WORKDIR /app
COPY --from=build /workspace/target/monster-trial-1.0.0.jar /app/app.jar
EXPOSE 8080
USER monstertrials
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
