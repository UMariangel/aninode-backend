FROM eclipse-temurin:23-jre
WORKDIR /app
EXPOSE 8080

# Copiamos el archivo .jar
COPY build/libs/*-SNAPSHOT.jar app.jar

# Lo arrancamos directamente
ENTRYPOINT ["java", "-jar", "app.jar"]