# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
# Copiamos archivos de Maven y código
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copia el wrapper y dale permisos si hace falta (en algunos contextos)
RUN chmod +x mvnw || true

# Copiamos el código fuente
COPY src ./src

# Construimos el jar (sin tests)
RUN ./mvnw -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]