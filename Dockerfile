# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:21-jre-jammy

# Não exponha uma porta fixa pois o Render atribui dinamicamente a porta via variável de ambiente.
# Porém, é bom documentar uma porta padrão para testes locais.
EXPOSE 8080

COPY --from=builder /app/target/*.jar app.jar

# Ajusta para usar a porta do Render
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
