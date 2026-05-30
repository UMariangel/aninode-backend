# Compilar la aplicación usando Gradle
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .

RUN ./gradlew build -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx256m -XX:MaxMetaspaceSize=128m"
# Crear la imagen ligera para ejecutar el backend
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]