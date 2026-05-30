#Compilamos la aplicación usando Gradle
FROM eclipse-temurin:17-jdk-jammy AS build
COPY --chown=gradle:gradle src/main/java/com/aninode/backend /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build -x test --no-daemon

#Creamos la imagen ligera para ejecutar el backend
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]