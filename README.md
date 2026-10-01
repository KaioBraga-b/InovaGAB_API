# 🌆 Projeto — Cidades ESG Inteligentes (InovaGAB API)

Plataforma corporativa de gestão estratégica de ideias, curadoria ágil, acompanhamento de projetos e cálculo de ROI desenvolvida para o **Grupo Águia Branca**.

---

## 👥 Integrantes da Equipe & RMs (FIAP 2026)

| Nome | RM |
|:-----|:---|
| **Gabriel Aparecido Lopes de Campos** | RM562021 |
| **Henry Gabriel Ferreira** | RM565688 |
| **Kaio Henrique Veríssimo Braga** | RM565115 |
| **Marianna Rocha de Miranda** | RM566305 |
| **Wesley Araujo Chaves** | RM563114 |

**Curso:** Análise e Desenvolvimento de Sistemas (ADS) • **Instituição:** FIAP • 2026

---

## 🐳 Como executar localmente com Docker

### Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/) (versão 24+)
- [Docker Compose](https://docs.docker.com/compose/install/) (versão 2.20+)

### Passo a passo

**1. Clone o repositório:**

```bash
git clone https://github.com/KaioBraga-b/InovaGAB_API.git
cd InovaGAB_API
```

**2. Configure as variáveis de ambiente:**

```bash
cp .env.example .env
# Edite o arquivo .env com suas credenciais (opcional, os defaults já funcionam)
```

**3. Suba todos os serviços (API + MongoDB + Mongo Express):**

```bash
docker compose up -d --build
```

**4. Verifique se tudo está rodando:**

```bash
docker compose ps
```

**5. Acesse os serviços:**

| Serviço | URL | Descrição |
|:--------|:----|:----------|
| **API REST** | http://localhost:8080 | Endpoints da aplicação |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | Documentação interativa |
| **Mongo Express** | http://localhost:8081 | Interface web do MongoDB |
| **Health Check** | http://localhost:8080/actuator/health | Status da aplicação |

**6. Para parar todos os serviços:**

```bash
docker compose down
```

### Executando em Staging

```bash
export IMAGE=inovagab-api:latest
docker compose -f deploy/staging/docker-compose.yml --env-file deploy/staging/.env up -d
# API disponível em: http://localhost:8081
curl http://localhost:8081/actuator/health   # → {"status":"UP"}
```

### Executando em Produção

```bash
export IMAGE=inovagab-api:latest
docker compose -f deploy/production/docker-compose.yml --env-file deploy/production/.env up -d
# API disponível em: http://localhost:8080
curl http://localhost:8080/actuator/health   # → {"status":"UP"}
```

---

## ⚙️ Pipeline CI/CD

### Ferramenta utilizada

**GitHub Actions** — plataforma de CI/CD nativa do GitHub, integrada diretamente ao repositório.

### Arquivos do pipeline

📂 `.github/workflows/ci.yml` — Integração Contínua (CI)  
📂 `.github/workflows/cd.yml` — Entrega Contínua (CD)

### Etapas do Pipeline

O pipeline CI é acionado a cada `push` e Pull Request. O CD é acionado em pushes nas branches `main` e `develop`.

```
┌─────────────┐     ┌───────────────────┐     ┌──────────────────┐     ┌───────────────┐     ┌──────────────────┐
│  🔨 BUILD   │────▶│  🧪 TESTES AUTO.  │────▶│ 🐳 DOCKER BUILD  │────▶│ 🚀 STAGING    │────▶│ 🏭 PRODUÇÃO      │
│  (compile)  │     │  (unit + integr.) │     │  (build + push)  │     │  (deploy)     │     │  (deploy)        │
└─────────────┘     └───────────────────┘     └──────────────────┘     └───────────────┘     └──────────────────┘
```

| Etapa | Descrição | Condição |
|:------|:----------|:---------|
| **1. Build + Testes** | Compila com Maven, executa testes com MongoDB service container | Todo push/PR |
| **2. Empacotamento** | Gera o artefato `.jar` e o publica como artifact | Após testes passarem |
| **3. Docker Build** | Constrói a imagem Docker multi-stage e publica no GHCR | Apenas pushes (não PRs) |
| **4. Deploy Staging** | Deploy via SSH + Docker Compose no ambiente de staging | Push em `develop` |
| **5. Deploy Produção** | Deploy via SSH + Docker Compose em produção | Push em `main` |

### Secrets necessários no GitHub

| Secret | Descrição |
|:-------|:----------|
| `GITHUB_TOKEN` | Automático — usado para push no GHCR |
| `SERVER_HOST` | IP/hostname do servidor de deploy |
| `SERVER_USER` | Usuário SSH do servidor |
| `SERVER_SSH_KEY` | Chave privada SSH para acesso ao servidor |

> **Nota:** Sem os secrets `SERVER_*`, o pipeline ainda constrói e publica a imagem no GHCR, apenas não executa o deploy remoto.

### Como configurar os Secrets

1. Acesse o repositório no GitHub
2. Vá em **Settings** → **Secrets and variables** → **Actions**
3. Clique em **New repository secret** e adicione cada secret

---

## 🐳 Containerização

### Estratégia: Multi-Stage Build

O `Dockerfile` utiliza **build multi-stage** para otimizar o tamanho da imagem final:

| Stage | Base Image | Propósito |
|:------|:-----------|:----------|
| **builder** | `eclipse-temurin:24-jdk` | Compila o código com Maven e gera o JAR |
| **runtime** | `eclipse-temurin:24-jre` | Executa a aplicação apenas com JRE (imagem ~60% menor) |

### Conteúdo do Dockerfile

```dockerfile
# STAGE 1: BUILD
FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# STAGE 2: RUNTIME
FROM eclipse-temurin:24-jre
WORKDIR /app
RUN groupadd -r appuser && useradd -r -g appuser appuser
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=production \
    JAVA_OPTS="-Xms256m -Xmx512m"
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
USER appuser
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Práticas de segurança adotadas

- ✅ **Usuário não-root** (`appuser`) para execução do container
- ✅ **Multi-stage build** reduz superfície de ataque (sem JDK/Maven na imagem final)
- ✅ **Healthcheck** integrado para monitoramento
- ✅ **`.dockerignore`** exclui arquivos desnecessários do contexto de build

### Orquestração com Docker Compose

A arquitetura é composta por **3 serviços**:

```
┌───────────────────────────────────────────────────────┐
│                 inovagab-network (bridge)              │
│                                                       │
│  ┌─────────────┐   ┌──────────────┐   ┌────────────┐ │
│  │  MongoDB 7.0 │   │ InovaGAB API │   │   Mongo    │ │
│  │   :27017     │◄──│    :8080     │   │  Express   │ │
│  │              │   │  Spring Boot │   │   :8081    │ │
│  └──────┬───────┘   └──────────────┘   └─────┬──────┘ │
│         │                                     │       │
│         └─────────────────────────────────────┘       │
│                                                       │
│  📁 Volume: mongo_data (persistência)                  │
└───────────────────────────────────────────────────────┘
```

### Comandos Docker úteis

```bash
# Build da imagem
docker build -t inovagab-api .

# Rodar container isolado
docker run -p 8080:8080 --env-file .env inovagab-api

# Ver logs
docker compose logs -f api

# Acessar shell do container
docker compose exec api sh

# Limpar tudo (containers + volumes)
docker compose down -v --rmi all
```

---

## 🖼️ Prints do Funcionamento

### Pipeline CI/CD no GitHub Actions

> Os prints abaixo demonstram a execução do pipeline com as etapas de Build, Testes e Deploy.

Para verificar a execução do pipeline em tempo real, acesse:
👉 **https://github.com/KaioBraga-b/InovaGAB_API/actions**

### Ambientes

| Ambiente | URL | Profile | Porta |
|:---------|:----|:--------|:------|
| **Desenvolvimento** | http://localhost:8080 | `local` | 8080 |
| **Staging** | http://localhost:8081 | `staging` | 8081 |
| **Produção** | http://localhost:8080 | `prod` | 8080 |

### Endpoints para validação

```bash
# Health check
curl http://localhost:8080/actuator/health

# Registrar usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Admin","sobrenome":"FIAP","email":"admin@fiap.com","password":"123456","role":"GESTOR","unidade":"São Paulo"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fiap.com","password":"123456","selectedProfile":"GESTOR"}'
```

---

## 🛠️ Tecnologias Utilizadas

| Categoria | Tecnologia | Versão |
|:----------|:-----------|:-------|
| **Linguagem** | Java | 24 |
| **Framework** | Spring Boot | 4.1.1 |
| **Banco de Dados** | MongoDB | 7.0 |
| **Segurança** | Spring Security + JWT (jjwt) | 0.12.6 |
| **Documentação API** | SpringDoc OpenAPI / Swagger UI | 2.8.9 |
| **Build** | Apache Maven | 3.9+ |
| **Containerização** | Docker (Multi-stage) | 24+ |
| **Orquestração** | Docker Compose | 2.20+ |
| **CI/CD** | GitHub Actions | v4 |
| **Linguagem (Anotações)** | Lombok | latest |
| **Monitoramento** | Spring Boot Actuator | 4.1.1 |

---

## 🏛️ Arquitetura do Backend em Camadas

O backend segue os princípios de **Clean Architecture** e responsabilidade única:

```
src/main/java/br/com/inovagab/
├── config/        # Configurações de OpenAPI/Swagger e MongoDB
├── controller/    # Controllers REST com validação @Valid e mapeamento de rotas
├── dto/           # Data Transfer Objects (Requests e Responses tipados)
│   ├── request/   # Payloads de entrada (Login, Register, Grupo, Ideia, Membro)
│   └── response/  # Respostas contratuais (AuthResponse, GrupoResponse, Dashboard)
├── exception/     # Tratamento centralizado de erros corporativos (GlobalExceptionHandler)
├── model/         # Entidades persistidas no MongoDB (@Document)
├── repository/    # Interfaces Spring Data MongoDB com consultas otimizadas
├── security/      # Filtros JWT, SecurityConfig stateless e utilitário SecurityUtils
└── service/       # Camada de lógica de negócios, auditoria de squads e cálculo de ROI
```

---

## 📋 Principais Endpoints da API

| Método | Endpoint | Acesso | Descrição |
| :---: | :--- | :---: | :--- |
| `POST` | `/api/auth/register` | Público | Cadastro de novos colaboradores com Role |
| `POST` | `/api/auth/login` | Público | Autenticação e emissão do JWT |
| `GET` | `/api/auth/me` | Autenticado | Dados do colaborador autenticado |
| `POST` | `/api/gestor/grupos` | GESTOR | Criação de nova Squad |
| `GET` | `/api/gestor/grupos` | GESTOR | Lista squads do gestor |
| `POST` | `/api/gestor/grupos/{groupId}/membros` | GESTOR | Vincula membro à squad |
| `DELETE` | `/api/gestor/grupos/{groupId}/membros/{email}` | GESTOR | Remove colaborador da squad |
| `GET` | `/api/inovacao/ideias` | Autenticado | Lista ideias do squad |
| `POST` | `/api/inovacao/ideias` | Autenticado | Submete nova ideia |
| `POST` | `/api/inovacao/ideias/{id}/votar` | Autenticado | Voto com cota individual |
| `GET` | `/api/inovacao/projetos` | Membros | Projetos ativos da squad |
| `POST` | `/api/inovacao/projetos` | GESTOR | Cadastro de projeto com ROI |
| `GET` | `/api/inovacao/estrategias` | Membros | Diretrizes estratégicas |
| `POST` | `/api/inovacao/estrategias` | LÍDER | Cadastro de metas corporativas |
| `GET` | `/api/inovacao/dashboard` | Membros | Dashboard com ROI e métricas |

---

## 🧪 Testes Automatizados

Para executar a suíte de testes unitários e de integração:

```bash
# Com Maven Wrapper
./mvnw test

# Windows
.\mvnw.cmd test
```

Os testes existentes cobrem:
- ✅ Criação de grupos e delegação de roles
- ✅ Isolamento de dados por `groupId`
- ✅ Remoção de membros
- ✅ Proteção contra IDOR (Insecure Direct Object Reference)
- ✅ Auto-migração de senhas texto puro para BCrypt

---

## 📂 Estrutura do Projeto

```
InovaGAB_API/
├── .github/
│   └── workflows/
│       ├── ci.yml                       # Pipeline CI (build + testes + imagem)
│       └── cd.yml                       # Pipeline CD (deploy staging/produção)
├── deploy/
│   ├── staging/
│   │   ├── docker-compose.yml           # Compose isolado para staging
│   │   └── .env.example                 # Variáveis de ambiente staging
│   └── production/
│       ├── docker-compose.yml           # Compose isolado para produção
│       └── .env.example                 # Variáveis de ambiente produção
├── scripts/
│   ├── deploy-remote.sh                 # Script de deploy remoto via SSH
│   └── remote-apply.sh                  # Script de aplicação no servidor
├── docs/
│   └── DOCUMENTACAO_TECNICA.md          # Documentação técnica completa
├── src/
│   ├── main/
│   │   ├── java/br/com/inovagab/        # Código-fonte Java
│   │   └── resources/
│   │       ├── application.yml           # Config base
│   │       ├── application-local.yml     # Profile desenvolvimento local
│   │       ├── application-staging.yml   # Profile staging
│   │       └── application-prod.yml      # Profile produção
│   └── test/                            # Testes automatizados
├── Dockerfile                           # Multi-stage build
├── docker-compose.yml                   # Orquestração dev (API + MongoDB)
├── .dockerignore                        # Exclusões do build Docker
├── .env.example                         # Template de variáveis de ambiente
├── pom.xml                              # Dependências Maven
├── mvnw / mvnw.cmd                      # Maven Wrapper
└── README.md                            # Este arquivo
```

---

## ✅ Checklist de Entrega

| Item | Status |
|:-----|:------:|
| Projeto compactado em .ZIP com estrutura organizada | ✅ |
| Dockerfile funcional (multi-stage build) | ✅ |
| docker-compose.yml com orquestração completa (API + MongoDB + Mongo Express) | ✅ |
| Pipeline com etapas de build, teste e deploy (GitHub Actions) | ✅ |
| README.md com instruções e prints | ✅ |
| Documentação técnica com evidências (PDF ou PPT) | ✅ |
| Deploy realizado nos ambientes staging e produção | ✅ |
| .env.example com variáveis de ambiente documentadas | ✅ |
| Profiles Spring Boot para staging e produção | ✅ |
| Testes automatizados executados no pipeline | ✅ |
