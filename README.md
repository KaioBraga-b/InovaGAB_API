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

## 🧪 Testes Automatizados (BDD com Gherkin, APIs e JSON Schema ESG)

A plataforma conta com uma suíte abrangente de **59 testes automatizados**, combinando **BDD (Behavior Driven Development)** via **Cucumber** com testes de integração e contrato via **REST-Assured** e **JSON Schema Validator**, totalmente integrados aos princípios **ESG (Environmental, Social, Governance)**.

### 📐 Pirâmide e Estratégia de Testes

```
                  /\
                 /  \     BDD Gherkin / E2E (Cucumber)
                /    \    • 13 cenários comportamentais focados em ESG
               /──────\   
              /        \  Testes de API & Contrato (REST-Assured + JSON Schema)
             /          \ • 100% dos endpoints cobertos (Status, Payload, Schemas)
            /────────────\
           /              \ Testes de Unidade e Serviços
          /────────────────\ • GrupoService, JwtTokenProvider, Security
```

### 🌿 Pilares ESG Validados nos Cenários de Teste

| Pilar ESG | Dimensão Testada | Exemplo Prático no Sistema |
|:----------|:-----------------|:---------------------------|
| **🌳 Environmental (Ambiental)** | Eficiência energética, reciclagem e redução de pegada de carbono | Submissão e triagem de ideias de coleta seletiva IoT, compostagem e descarbonização da frota |
| **🤝 Social (Social)** | Inclusão corporativa, diversidade e engajamento participativo | Votação democrática em ideias, cotas individuais de votos e squads de diversidade e acessibilidade PCD |
| **⚖️ Governance (Governança)** | Compliance, controle de acesso RBAC, rastreabilidade e integridade | Bloqueio de não-gestores na criação de squads (400/403), expiração de JWT e validação estrita de contratos |
| **📈 ROI & Accountability** | Otimização de recursos e sustentabilidade financeira | Monitoramento executivo no Dashboard de sustentabilidade com apuração de ROI e retorno por meta ODS |

---

### 📝 Cenários BDD em Gherkin (`src/test/resources/features/`)

A suíte implementa **13 cenários de teste** estruturados em linguagem Gherkin em português, cobrindo fluxos positivos e negativos:

1. **`01_autenticacao_seguranca_esg.feature`**:
   - `Cenário 1 (Positivo)`: Registrar novo colaborador de sustentabilidade com sucesso e validação de schema.
   - `Cenário 2 (Positivo)`: Autenticar gestor de sustentabilidade e obter token JWT rastreável.
   - `Cenário 3 (Negativo)`: Falha de autenticação ao fornecer senha incorreta (rejeição 400 por compliance).
   - `Cenário 4 (Positivo)`: Consultar perfil autenticado para auditoria e rastreabilidade corporativa.

2. **`02_gestao_squads_esg.feature`**:
   - `Cenário 5 (Positivo)`: Gestor cria Squad corporativa focada em Descarbonização da Frota (201 Created).
   - `Cenário 6 (Positivo)`: Adição de membro especialista multidisciplinar à Squad de Sustentabilidade.
   - `Cenário 7 (Negativo)`: Usuário sem permissão tenta criar Squad e recebe bloqueio por RBAC.

3. **`03_inovacao_sustentavel_esg.feature`**:
   - `Cenário 8 (Positivo)`: Submeter ideia de Coleta Inteligente de Resíduos e Logística Reversa IoT.
   - `Cenário 9 (Positivo)`: Votação comunitária e colaboração em ideias de inclusão e acessibilidade.
   - `Cenário 10 (Negativo)`: Validação de payload e integridade ao submeter ideias vazias.

4. **`04_metricas_dashboard_roi_esg.feature`**:
   - `Cenário 11 (Positivo)`: Cadastrar diretriz estratégica corporativa alinhada aos ODS da ONU (Redução de CO2).
   - `Cenário 12 (Positivo)`: Registrar projeto de usina solar fotovoltaica com apuração de indicadores e ROI.
   - `Cenário 13 (Positivo)`: Consulta ao Dashboard executivo com cálculo consolidado de ROI e metas ESG ativas.

---

### 🔌 Cobertura Completa de APIs e Validação de Contrato (JSON Schema)

Todos os endpoints da aplicação possuem testes automatizados que validam:
1. **Status Code HTTP** (200, 201, 400, 403)
2. **Corpo da Resposta (JSON)** (campos obrigatórios, valores esperados, tipos de dados)
3. **Contratos JSON Schema** (verificação rigorosa de estrutura via Draft-07)

| Controller | Endpoints Testados | Schemas JSON Validados |
|:-----------|:-------------------|:-----------------------|
| **AuthController** | `POST /api/auth/register`<br>`POST /api/auth/login`<br>`GET /api/auth/me` | `message-response-schema.json`<br>`auth-response-schema.json`<br>`user-profile-schema.json`<br>`error-response-schema.json` |
| **GestorGrupoController** | `POST /api/gestor/grupos`<br>`GET /api/gestor/grupos`<br>`GET /api/gestor/grupos/{groupId}`<br>`PUT /api/gestor/grupos/{groupId}`<br>`POST /api/gestor/grupos/{groupId}/membros`<br>`DELETE /api/gestor/grupos/{groupId}/membros/{email}`<br>`GET /api/gestor/usuarios` | `grupo-response-schema.json`<br>`grupo-list-schema.json`<br>`usuarios-list-schema.json` |
| **InovacaoController (Projetos)** | `POST /api/inovacao/projetos`<br>`GET /api/inovacao/projetos`<br>`PUT /api/inovacao/projetos/{id}`<br>`DELETE /api/inovacao/projetos/{id}` | `projeto-schema.json`<br>`projetos-list-schema.json` |
| **InovacaoController (Estratégias)** | `POST /api/inovacao/estrategias`<br>`GET /api/inovacao/estrategias`<br>`PUT /api/inovacao/estrategias/{id}`<br>`DELETE /api/inovacao/estrategias/{id}` | `estrategia-schema.json`<br>`estrategias-list-schema.json` |
| **InovacaoController (Ideias)** | `POST /api/inovacao/ideias`<br>`GET /api/inovacao/ideias`<br>`PUT /api/inovacao/ideias/{id}`<br>`POST /api/inovacao/ideias/{id}/votar`<br>`POST /api/inovacao/ideias/{id}/comentar`<br>`DELETE /api/inovacao/ideias/{id}` | `ideia-schema.json`<br>`ideias-list-schema.json` |
| **InovacaoController (Transações)** | `POST /api/inovacao/transacoes`<br>`GET /api/inovacao/transacoes`<br>`POST /api/inovacao/receitas`<br>`GET /api/inovacao/receitas`<br>`POST /api/inovacao/despesas`<br>`GET /api/inovacao/despesas`<br>`DELETE /api/inovacao/transacoes/{id}` | `transacao-schema.json`<br>`transacoes-list-schema.json` |
| **InovacaoController (Dashboard)** | `GET /api/inovacao/dashboard` | `dashboard-resumo-schema.json` |
| **NotificacaoController** | `GET /api/notificacoes/{role}`<br>`PUT /api/notificacoes/{id}/lida` | `notificacao-schema.json`<br>`notificacoes-list-schema.json` |
| **Actuator** | `GET /actuator/health` | `health-schema.json` |

---

### 🚀 Como Executar os Testes

#### 1. Pré-requisito (Subir o MongoDB)

Os testes de integração e BDD executam a aplicação Spring Boot real conectada ao MongoDB:

```bash
# Subir apenas o serviço do MongoDB em background
docker compose up -d mongodb
```

#### 2. Executar toda a suíte de testes (59 testes)

```bash
# Executa todos os testes (Unitários, Integração, API e BDD Cucumber)
./mvnw test

# No Windows
.\mvnw.cmd test
```

#### 3. Executar apenas os testes BDD com Cucumber

```bash
./mvnw test -Dtest=CucumberSuiteTest
```

#### 4. Executar apenas os testes de API e Contrato JSON Schema

```bash
./mvnw test -Dtest=*ApiTest
```

#### 5. Visualizar o Relatório Visual do Cucumber

Após a execução, um relatório HTML rico e interativo é gerado em:
👉 `target/cucumber-reports/cucumber.html`

Abra no seu navegador:
```bash
# No macOS
open target/cucumber-reports/cucumber.html

# No Linux
xdg-open target/cucumber-reports/cucumber.html

# No Windows
start target/cucumber-reports/cucumber.html
```

---

### 📦 Artefatos de Entrega Gerados

- **Código-fonte em arquivo .ZIP**: Execute `./scripts/package-project.sh` para gerar `InovaGAB_Testes_Automatizados.zip`.
- **Relatório Técnico em PDF**: Documento completo disponível em `docs/InovaGAB_Evidencias_Testes_Automatizados_ESG.pdf`.
- **Relatório Técnico em Markdown**: Disponível em `docs/RELATORIO_TESTES_AUTOMATIZADOS_BDD_API_ESG.md`.

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
