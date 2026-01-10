# ============================================================================
# Dockerfile - Backend Java (Spring Boot)
# ============================================================================
# Multi-stage build para otimizar tamanho da imagem
# Stage 1: Build da aplicação
# Stage 2: Runtime com JRE apenas
# ============================================================================

# ============================================================================
# STAGE 1: BUILD
# ============================================================================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build

# Metadados
LABEL maintainer="FlightOnTime Team"
LABEL description="Backend Java Spring Boot - FlightOnTime API"
LABEL stage="build"

# Diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven primeiro (cache de dependências)
COPY pom.xml .

# Download de dependências (esta camada será cacheada se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação (skip tests para build mais rápido)
# Para incluir testes, remova -DskipTests
RUN mvn clean package -DskipTests -B

# ============================================================================
# STAGE 2: RUNTIME
# ============================================================================
FROM eclipse-temurin:17-jre-alpine

# Metadados
LABEL maintainer="FlightOnTime Team"
LABEL description="Backend Java Spring Boot - FlightOnTime API - Runtime"
LABEL version="1.0.0"

# Instalar curl para health checks
RUN apk add --no-cache curl

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Diretório de trabalho
WORKDIR /app

# Copiar JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

# Mudar ownership para usuário não-root
RUN chown -R appuser:appgroup /app

# Usar usuário não-root
USER appuser

# Variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SERVER_PORT=8080 \
    PREDICTION_SERVICE_URL=http://python-api:5000 \
    PREDICTION_SERVICE_USE_MOCK=false

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# ============================================================================
# Como construir e executar:
# ============================================================================
# docker build -t flightontime-backend:latest .
# docker run -p 8080:8080 -e PREDICTION_SERVICE_URL=http://localhost:5000 flightontime-backend:latest
# ============================================================================
