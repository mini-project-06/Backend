# Backend repo (app at root) — Gradle 9.5.1 / JDK17 -> JRE17
FROM public.ecr.aws/docker/library/eclipse-temurin:17-jdk AS build
ENV JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"
WORKDIR /app
COPY gradlew settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies >/dev/null 2>&1 || true
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=h2
ENTRYPOINT ["java","-jar","/app/app.jar"]
