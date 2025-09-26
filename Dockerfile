# Etapa 1: Build - Usa uma imagem Maven com JDK
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia e baixa as dependências para aproveitar o cache
COPY pom.xml .
COPY src ./src

# Build da aplicação, pulando os testes
RUN mvn clean package -DskipTests

# ----------------------------------------------------
# Etapa 2: Execução - Imagem de Produção Leve
# CORREÇÃO: Usando eclipse-temurin, que é estável e leve para o JRE
FROM eclipse-temurin:17-jre-jammy

# Expõe a porta que a aplicação Spring Boot irá usar
EXPOSE 8080

# Copia o JAR construído da etapa anterior
# Assume que o JAR é o único arquivo JAR no diretório target e está correto.
COPY --from=builder /app/target/*.jar app.jar

# Ponto de entrada: Executa a aplicação JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
