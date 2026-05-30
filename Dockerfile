# Usamos una imagen ligera de Java 17 solo para ejecutar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 8080

# Copiamos el archivo .jar
COPY build/libs/*-SNAPSHOT.jar app.jar

# Lo arrancamos directamente
ENTRYPOINT ["java", "-jar", "app.jar"]