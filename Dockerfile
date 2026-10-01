# ============================================================
# STAGE 1: BUILD — Compila a aplicação com Maven (multi-stage)
# ============================================================
FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app

# Copia wrapper e configurações Maven primeiro (melhor cache de camadas)
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# Baixa dependências offline (cache layer separada)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copia código-fonte
COPY src ./src

# Build do JAR sem executar os testes (serão executados na pipeline CI/CD)
RUN ./mvnw clean package -DskipTests -B

# ============================================================
# STAGE 2: RUNTIME — Imagem final leve somente com JRE
# ============================================================
FROM eclipse-temurin:24-jre

LABEL maintainer="InovaGAB Team <kaio@fiap.com>"
LABEL description="InovaGAB API — Cidades ESG Inteligentes"

WORKDIR /app

# Cria usuário não-root para segurança
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copia o JAR compilado do stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Define variáveis de ambiente padrão (podem ser sobrescritas pelo docker-compose)
ENV SPRING_PROFILES_ACTIVE=production \
    JAVA_OPTS="-Xms256m -Xmx512m"

# Healthcheck integrado
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Executa como usuário não-root
USER appuser

# Entrypoint com suporte a JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
