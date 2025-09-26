# Etapa 1: Build
# Usa a imagem oficial do Maven com JDK para a etapa de build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY pom.xml .
# Copia o código-fonte da aplicação
COPY src ./src

# Executa o build da aplicação, pulando os testes para ser mais rápido
RUN mvn clean package -DskipTests

# Etapa 2: Execução
# Usa uma imagem leve do OpenJDK apenas com o JRE para a execução
FROM openjdk:17-jre-slim

# Expõe a porta que a aplicação Spring Boot irá usar
EXPOSE 8080

# Define o ponto de entrada, copiando o JAR da etapa de build
# Certifique-se de que o nome do JAR aqui corresponde ao nome no seu `pom.xml`
COPY --from=build /app/target/dizzme-platform-*.jar app.jar

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
