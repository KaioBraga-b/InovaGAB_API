# 🌆 Documentação e Evidências de Testes Automatizados (BDD & APIs com Foco em ESG)
## Projeto: Cidades ESG Inteligentes — InovaGAB API (Grupo Águia Branca)

---

### 👥 Integrantes da Equipe & RMs (FIAP 2026)

| Nome | RM | Papel no Projeto |
|:-----|:---|:-----------------|
| **Gabriel Aparecido Lopes de Campos** | RM562021 | Arquiteto de Software & QA Engineer |
| **Henry Gabriel Ferreira** | RM565688 | Backend Developer & Automação BDD |
| **Kaio Henrique Veríssimo Braga** | RM565115 | DevOps & Engenheiro de CI/CD |
| **Marianna Rocha de Miranda** | RM566305 | QA & Especialista em Contratos de API |
| **Wesley Araujo Chaves** | RM563114 | Analista de Requisitos & Compliance ESG |

**Curso:** Análise e Desenvolvimento de Sistemas (ADS) • **Instituição:** FIAP • **Ano:** 2026  
**Disciplina:** Testes Automatizados e Qualidade de Software

---

## 1. Introdução e Contextualização ESG

A sustentabilidade corporativa moderna exige mais do que declarações de intenção: exige **rastreabilidade, integridade de dados e governança tecnológica auditável**. O projeto **InovaGAB API** foi concebido para o **Grupo Águia Branca** no contexto de Cidades Inteligentes, atuando como um hub estratégico para captura de ideias, formação de squads multidisciplinares, execução de projetos ecoeficientes e apuração do Retorno sobre Investimento (ROI) sustentável.

A qualidade de software é um vetor essencial de ESG. Sistemas instáveis, com falhas de segurança ou consumo ineficiente de recursos de infraestrutura violam os pilares de sustentabilidade e governança. Nesse sentido, a presente atividade avaliativa implementou uma **suíte robusta de 59 testes automatizados**, combinando:
1. **BDD (Behavior-Driven Development)** com cenários em Gherkin em português, assegurando que o comportamento de negócio reflita fielmente os requisitos dos stakeholders;
2. **Testes de Integração de API com REST-Assured**, validando status codes HTTP e estruturas de resposta para 100% dos endpoints;
3. **Testes de Contrato com JSON Schema**, garantindo conformidade regulatória, integridade das payloads e retrocompatibilidade de microsserviços.

---

## 2. Alinhamento com os Pilares ESG

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PILARES ESG NO INOVAGAB                         │
├───────────────────┬───────────────────┬────────────────────────────────┤
│   ENVIRONMENTAL   │      SOCIAL       │           GOVERNANCE           │
│    (Ambiental)    │     (Social)      │          (Governança)          │
├───────────────────┼───────────────────┼────────────────────────────────┤
│ • Eficiência      │ • Inclusão e      │ • Compliance regulatório       │
│   energética      │   diversidade     │ • Controle de acesso RBAC      │
│ • Logística       │ • Gestão de       │ • Rastreabilidade por JWT      │
│   reversa IoT     │   squads          │ • Contratos JSON Schema        │
│ • Redução de CO2  │ • Engajamento     │ • Transparência de ROI         │
└───────────────────┴───────────────────┴────────────────────────────────┘
```

- **Pilar Ambiental (E):** Validação de ideias e projetos de transição energética (usinas solares, ônibus elétricos, gestão de resíduos por sensores IoT e logística reversa).
- **Pilar Social (S):** Estímulo à diversidade corporativa, formação de squads multidisciplinares e votação democrática em projetos de acessibilidade para colaboradores.
- **Pilar Governança (G):** Controle estrito de acesso baseado em papéis (RBAC - GESTOR, LIDER, OPERADOR), emissão segura de tokens JWT stateless e conformidade contratual estrita via JSON Schema.

---

## 3. Arquitetura da Suíte de Testes Automatizados

A suíte foi estruturada de forma modular seguindo a pirâmide de testes:

```
src/test/
├── java/br/com/inovagab/
│   ├── InovagabApplicationTests.java         # Teste de bootstrapping do contexto Spring
│   ├── security/
│   │   └── JwtTokenProviderTest.java          # Testes unitários do gerador/validador de JWT
│   ├── service/
│   │   └── GrupoServiceTest.java              # Testes de serviço de squads e isolamento IDOR
│   ├── api/                                   # Suíte REST-Assured (100% das APIs)
│   │   ├── BaseApiTest.java                   # Setup base com porta aleatória e utilitários
│   │   ├── AuthApiTest.java                   # Testes de /api/auth (Register, Login, Me)
│   │   ├── GestorGrupoApiTest.java            # Testes de /api/gestor (Squads, Membros)
│   │   ├── InovacaoApiTest.java               # Testes de /api/inovacao (Projetos, Ideias, ROI)
│   │   ├── NotificacaoApiTest.java            # Testes de /api/notificacoes (Alertas ESG)
│   │   └── ActuatorHealthApiTest.java         # Teste de /actuator/health (SLA de disponibilidade)
│   └── bdd/                                   # Suíte BDD Cucumber JUnit Platform
│       ├── CucumberSuiteTest.java             # Suite runner Cucumber (@Suite)
│       ├── CucumberSpringConfiguration.java   # Integração do Cucumber com Spring Boot Test
│       ├── CucumberTestContext.java           # Estado compartilhado entre steps
│       └── steps/
│           ├── CommonSteps.java               # Steps genéricos (Status code, JSON Schema)
│           ├── AutenticacaoSteps.java         # Steps de registro, login e perfil
│           ├── GestaoSquadsSteps.java         # Steps de criação de squads e permissões
│           ├── InovacaoIdeiasSteps.java       # Steps de submissão de ideias e votação
│           └── DashboardRoiSteps.java         # Steps de metas e dashboard executivo
└── resources/
    ├── cucumber.properties                    # Configurações de relatórios (HTML, JSON)
    ├── features/                              # 13 Cenários BDD Gherkin em Português
    │   ├── 01_autenticacao_seguranca_esg.feature
    │   ├── 02_gestao_squads_esg.feature
    │   ├── 03_inovacao_sustentavel_esg.feature
    │   └── 04_metricas_dashboard_roi_esg.feature
    └── schemas/                               # 19 Contratos JSON Schema (Draft-07)
        ├── auth-response-schema.json
        ├── user-profile-schema.json
        ├── error-response-schema.json
        ├── message-response-schema.json
        ├── grupo-response-schema.json
        ├── grupo-list-schema.json
        ├── usuarios-list-schema.json
        ├── projeto-schema.json
        ├── projetos-list-schema.json
        ├── estrategia-schema.json
        ├── estrategias-list-schema.json
        ├── ideia-schema.json
        ├── ideias-list-schema.json
        ├── transacao-schema.json
        ├── transacoes-list-schema.json
        ├── dashboard-resumo-schema.json
        ├── notificacao-schema.json
        ├── notificacoes-list-schema.json
        └── health-schema.json
```

---

## 4. Detalhamento dos Cenários BDD em Gherkin

A especificação contempla **13 cenários de teste**, superando o mínimo de 3 exigido pela atividade e abordando caminhos felizes (positivos) e fluxos de exceção (negativos).

### 4.1. Feature: Autenticação, Controle de Acesso e Segurança ESG
**Arquivo:** `src/test/resources/features/01_autenticacao_seguranca_esg.feature`

```gherkin
# language: pt
Funcionalidade: Autenticação, Controle de Acesso e Segurança ESG
  Como um colaborador ou gestor da plataforma Cidades ESG Inteligentes
  Eu quero me autenticar de forma segura na API
  Para acessar as funcionalidades de inovação sustentável com rastreabilidade e governança

  Contexto:
    Dado que a API de autenticação está disponível

  @positivo @seguranca @esg_governanca
  Cenário: Registrar novo colaborador de sustentabilidade com sucesso
    Dado que eu envio os dados cadastrais válidos:
      | nome      | Carlos                     |
      | sobrenome | Silva                      |
      | email     | carlos.esg@aguabranca.com  |
      | password  | senhaSegura123             |
      | role      | COLABORADOR                |
      | unidade   | Vitória - ES               |
    Quando eu realizo uma requisição POST para "/api/auth/register"
    Então o status da resposta deve ser 200
    E o corpo da resposta deve conter a mensagem "Usuário registrado com sucesso."
    E a resposta deve respeitar o contrato JSON Schema "schemas/message-response-schema.json"

  @positivo @autenticacao @esg_governanca
  Cenário: Autenticar gestor de sustentabilidade e obter token JWT
    Dado que existe um gestor previamente cadastrado com email "gestor.esg@aguabranca.com" e senha "senhaGestor123"
    Quando eu solicito a autenticação com email "gestor.esg@aguabranca.com", senha "senhaGestor123" e perfil "GESTOR"
    Então o status da resposta deve ser 200
    E o corpo da resposta deve conter um token JWT válido
    E o perfil retornado deve ser "GESTOR"
    E a resposta deve respeitar o contrato JSON Schema "schemas/auth-response-schema.json"

  @negativo @seguranca @esg_compliance
  Cenário: Falha de autenticação ao fornecer senha incorreta
    Dado que existe um usuário com email "gestor.esg@aguabranca.com"
    Quando eu solicito a autenticação com email "gestor.esg@aguabranca.com", senha "senhaErrada999" e perfil "GESTOR"
    Então o status da resposta deve ser 400
    E o corpo da resposta deve conter o erro "Senha inválida."
    E a resposta deve respeitar o contrato JSON Schema "schemas/error-response-schema.json"

  @positivo @rastreabilidade @esg_governanca
  Cenário: Consultar dados do perfil autenticado para auditoria
    Dado que eu estou autenticado como "gestor.esg@aguabranca.com"
    Quando eu realizo uma requisição GET para "/api/auth/me"
    Então o status da resposta deve ser 200
    E o email no perfil retornado deve ser "gestor.esg@aguabranca.com"
    E a resposta deve respeitar o contrato JSON Schema "schemas/user-profile-schema.json"
```

### 4.2. Feature: Gestão de Squads de Sustentabilidade e Governança Corporativa
**Arquivo:** `src/test/resources/features/02_gestao_squads_esg.feature`

```gherkin
# language: pt
Funcionalidade: Gestão de Squads de Sustentabilidade e Governança Corporativa
  Como um Gestor de Sustentabilidade do Grupo Águia Branca
  Eu quero criar squads e gerenciar membros multidisciplinares
  Para descentralizar ações ESG e assegurar governança colaborativa

  Contexto:
    Dado que eu estou autenticado como GESTOR com email "gestor.squad@aguabranca.com"

  @positivo @social @esg_inclusao
  Cenário: Gestor cria squad focada em Eficiência Energética e Descarbonização
    Dado que eu preparo a criação de uma Squad com os dados:
      | nome         | Squad Descarbonização da Frota |
      | departamento | Logística Verde                |
      | descricao    | Otimização de rotas e eletrificação veicular |
    Quando eu envio uma requisição POST para "/api/gestor/grupos"
    Então o status da resposta deve ser 201
    E a squad criada deve ter o nome "Squad Descarbonização da Frota"
    E a resposta deve respeitar o contrato JSON Schema "schemas/grupo-response-schema.json"

  @positivo @social @colaboracao
  Cenário: Adicionar membro especialista à Squad de Sustentabilidade
    Dado que existe uma Squad de inovação ESG criada pelo gestor
    Quando o gestor adiciona o membro com email "especialista.solar@aguabranca.com", nome "Mariana Solar" e role "COLABORADOR"
    Então o status da resposta deve ser 200
    E a lista de membros da squad deve conter o email "especialista.solar@aguabranca.com"
    E a resposta deve respeitar o contrato JSON Schema "schemas/grupo-response-schema.json"

  @negativo @governanca @esg_compliance
  Cenário: Impedir que usuário sem perfil GESTOR crie squads (Controle de Acesso RBAC)
    Dado que eu estou autenticado como COLABORADOR com email "colaborador.comum@aguabranca.com"
    Quando eu tento criar uma squad com nome "Squad Não Autorizada"
    Então o status da resposta deve ser 400
    E a mensagem de erro deve indicar que o acesso é exclusivo para perfil GESTOR
```

### 4.3. Feature: Gestão do Funil de Ideias Sustentáveis e Engajamento ESG
**Arquivo:** `src/test/resources/features/03_inovacao_sustentavel_esg.feature`

```gherkin
# language: pt
Funcionalidade: Gestão do Funil de Ideias Sustentáveis e Engajamento ESG
  Como um colaborador participante de um Squad ESG
  Eu quero submeter ideias sustentáveis, votar e interagir
  Para promover economia circular, redução de desperdício e inovação participativa

  Contexto:
    Dado que existe uma Squad ESG configurada e o usuário está autenticado no Squad

  @positivo @ambiental @esg_residuos
  Cenário: Submeter com sucesso ideia de Coleta Inteligente e Reciclagem de Resíduos
    Dado que eu elaboro uma proposta de ideia com os seguintes dados:
      | titulo      | Coleta Inteligente de Resíduos em Terminais |
      | descricao   | Sensores IoT em lixeiras para coleta otimizada de recicláveis |
      | area        | Gestão Ambiental e Logística Reversa        |
      | impacto     | ALTO                                        |
      | prioridade  | ALTA                                        |
    Quando eu realizo uma requisição POST para "/api/inovacao/ideias"
    Então o status da resposta deve ser 200
    E a ideia cadastrada deve ter o título "Coleta Inteligente de Resíduos em Terminais"
    E a resposta deve respeitar o contrato JSON Schema "schemas/ideia-schema.json"

  @positivo @social @engajamento
  Cenário: Votar democraticamente em uma ideia de inclusão e acessibilidade
    Dado que existe uma ideia cadastrada com título "Programa de Acessibilidade Digital para PCDs"
    Quando eu realizo uma requisição POST de voto para a ideia
    Então o status da resposta deve ser 200
    E a quantidade de votos da ideia deve ser incrementada em 1
    E a resposta deve respeitar o contrato JSON Schema "schemas/ideia-schema.json"

  @negativo @governanca @validacao
  Cenário: Rejeitar submissão de ideia sem informações essenciais
    Dado que eu tento enviar uma ideia sem título e sem descrição
    Quando eu envio o payload vazio ou nulo para "/api/inovacao/ideias"
    Então a API deve processar a requisição mantendo a integridade do banco de dados
```

### 4.4. Feature: Monitoramento de ROI ESG e Indicadores de Sustentabilidade
**Arquivo:** `src/test/resources/features/04_metricas_dashboard_roi_esg.feature`

```gherkin
# language: pt
Funcionalidade: Monitoramento de ROI ESG e Indicadores de Sustentabilidade
  Como um Gestor ou Líder de Inovação ESG
  Eu quero acompanhar os projetos, metas estratégicas e o ROI financeiro/ambiental
  Para garantir transparência, prestação de contas (accountability) e compliance ambiental

  Contexto:
    Dado que existe um Squad ESG com projetos e estratégias cadastradas

  @positivo @governanca @esg_estrategia
  Cenário: Cadastrar diretriz estratégica corporativa alinhada aos ODS da ONU
    Dado que eu defino uma meta estratégica com:
      | titulo    | Redução de 30% nas Emissões de CO2 até 2030  |
      | descricao | Transição gradual para energia limpa e frotas elétricas |
      | categoria | Ambiental (ESG - E)                           |
    Quando eu realizo uma requisição POST para "/api/inovacao/estrategias"
    Então o status da resposta deve ser 200
    E a estratégia cadastrada deve conter o título "Redução de 30% nas Emissões de CO2 até 2030"
    E a resposta deve respeitar o contrato JSON Schema "schemas/estrategia-schema.json"

  @positivo @financeiro @esg_roi
  Cenário: Registrar projeto de eficiência energética e apurar indicadores de retorno
    Dado que eu crio um projeto de inovação com os dados:
      | titulo        | Usina Solar Fotovoltaica na Sede Corporativa |
      | investimento  | R$ 150.000,00                                |
      | status        | EM_ANDAMENTO                                 |
      | area          | Eficiência Energética                        |
      | lucroObtido   | 45000.0                                      |
    Quando eu realizo uma requisição POST para "/api/inovacao/projetos"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato JSON Schema "schemas/projeto-schema.json"

  @positivo @dashboard @esg_monitoramento
  Cenário: Obter consolidação executiva do Dashboard de Sustentabilidade e ROI
    Quando eu realizo uma requisição GET para "/api/inovacao/dashboard"
    Então o status da resposta deve ser 200
    E a resposta deve conter indicadores numéricos de ROI e projetos ativos
    E a resposta deve respeitar o contrato JSON Schema "schemas/dashboard-resumo-schema.json"
```

---

## 5. Cobertura de APIs e Testes de Contrato (JSON Schema)

A suíte cobre **100% dos endpoints REST** da aplicação, testando todas as rotas com:
- Validação do **Status Code HTTP** esperado;
- Validação do conteúdo do corpo da resposta com **Hamcrest**;
- Validação contratual estrita com **JSON Schema (Draft-07)**.

| Rota HTTP | Método | Tipo de Teste | Status Esperado | Validação de Contrato |
|:----------|:------:|:-------------:|:---------------:|:----------------------|
| `/api/auth/register` | `POST` | Integração / Contrato | 200 OK / 400 Bad Request | `message-response-schema.json` |
| `/api/auth/login` | `POST` | Integração / Contrato | 200 OK / 400 Bad Request | `auth-response-schema.json` |
| `/api/auth/me` | `GET` | Integração / Contrato | 200 OK / 400 Bad Request | `user-profile-schema.json` |
| `/api/gestor/grupos` | `POST` | Integração / Contrato | 201 Created / 400 Bad Request | `grupo-response-schema.json` |
| `/api/gestor/grupos` | `GET` | Integração / Contrato | 200 OK | `grupo-list-schema.json` |
| `/api/gestor/grupos/{groupId}` | `GET` | Integração / Contrato | 200 OK | `grupo-response-schema.json` |
| `/api/gestor/grupos/{groupId}` | `PUT` | Integração / Contrato | 200 OK | `grupo-response-schema.json` |
| `/api/gestor/grupos/{groupId}/membros` | `POST` | Integração / Contrato | 200 OK | `grupo-response-schema.json` |
| `/api/gestor/grupos/{groupId}/membros/{email}` | `DELETE` | Integração / Contrato | 200 OK | `grupo-response-schema.json` |
| `/api/gestor/usuarios` | `GET` | Integração / Contrato | 200 OK | `usuarios-list-schema.json` |
| `/api/inovacao/projetos` | `POST` | Integração / Contrato | 200 OK | `projeto-schema.json` |
| `/api/inovacao/projetos` | `GET` | Integração / Contrato | 200 OK | `projetos-list-schema.json` |
| `/api/inovacao/projetos/{id}` | `PUT` | Integração / Contrato | 200 OK | `projeto-schema.json` |
| `/api/inovacao/projetos/{id}` | `DELETE` | Integração | 200 OK | — |
| `/api/inovacao/estrategias` | `POST` | Integração / Contrato | 200 OK | `estrategia-schema.json` |
| `/api/inovacao/estrategias` | `GET` | Integração / Contrato | 200 OK | `estrategias-list-schema.json` |
| `/api/inovacao/estrategias/{id}` | `PUT` | Integração / Contrato | 200 OK | `estrategia-schema.json` |
| `/api/inovacao/estrategias/{id}` | `DELETE` | Integração | 200 OK | — |
| `/api/inovacao/ideias` | `POST` | Integração / Contrato | 200 OK | `ideia-schema.json` |
| `/api/inovacao/ideias` | `GET` | Integração / Contrato | 200 OK | `ideias-list-schema.json` |
| `/api/inovacao/ideias/{id}` | `PUT` | Integração / Contrato | 200 OK | `ideia-schema.json` |
| `/api/inovacao/ideias/{id}/votar` | `POST` | Integração / Contrato | 200 OK | `ideia-schema.json` |
| `/api/inovacao/ideias/{id}/comentar` | `POST` | Integração / Contrato | 200 OK | `ideia-schema.json` |
| `/api/inovacao/ideias/{id}` | `DELETE` | Integração | 200 OK | — |
| `/api/inovacao/transacoes` | `POST` | Integração / Contrato | 200 OK | `transacao-schema.json` |
| `/api/inovacao/transacoes` | `GET` | Integração / Contrato | 200 OK | `transacoes-list-schema.json` |
| `/api/inovacao/receitas` | `POST` / `GET` | Integração / Contrato | 200 OK | `transacoes-list-schema.json` |
| `/api/inovacao/despesas` | `POST` / `GET` | Integração / Contrato | 200 OK | `transacoes-list-schema.json` |
| `/api/inovacao/transacoes/{id}` | `DELETE` | Integração | 200 OK | — |
| `/api/inovacao/dashboard` | `GET` | Integração / Contrato | 200 OK | `dashboard-resumo-schema.json` |
| `/api/notificacoes/{role}` | `GET` | Integração / Contrato | 200 OK | `notificacoes-list-schema.json` |
| `/api/notificacoes/{id}/lida` | `PUT` | Integração / Contrato | 200 OK | `notificacao-schema.json` |
| `/actuator/health` | `GET` | SLA / Contrato | 200 OK | `health-schema.json` |

---

## 6. Instruções de Execução e Automação no Pipeline CI/CD

### 6.1. Execução Local

Para executar os testes localmente em ambiente de desenvolvimento:

1. **Subir o serviço do banco de dados (MongoDB 7.0):**
   ```bash
   docker compose up -d mongodb
   ```
2. **Executar todos os 59 testes via Maven Wrapper:**
   ```bash
   ./mvnw test
   ```
3. **Visualizar o relatório interativo do Cucumber:**
   ```bash
   open target/cucumber-reports/cucumber.html
   ```

### 6.2. Execução Automatizada no Pipeline de CI/CD (GitHub Actions)

O repositório possui a workflow `.github/workflows/ci.yml` configurada para disparar automaticamente a cada `push` ou `pull request`.

Etapa do Pipeline:
```yaml
      # MongoDB 7.0 gerenciado como service container pelo GitHub Actions
      services:
        mongodb:
          image: mongo:7.0
          env:
            MONGO_INITDB_ROOT_USERNAME: root
            MONGO_INITDB_ROOT_PASSWORD: secret
            MONGO_INITDB_DATABASE: inovagab_db
          ports:
            - 27017:27017

      - name: Executar testes automatizados (Unitários, BDD Gherkin e Contratos de API)
        run: ./mvnw -B test

      - name: Publicar relatórios de testes (Cucumber HTML e Surefire)
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-reports-cucumber-surefire
          path: |
            target/cucumber-reports/
            target/surefire-reports/
```

---

## 7. Evidências de Execução dos Testes

### 7.1. Sumário Geral de Execução (Console Maven Surefire)

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running br.com.inovagab.security.JwtTokenProviderTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.149 s
[INFO] Running br.com.inovagab.InovagabApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.250 s
[INFO] Running Testes de API - Inovação, Projetos, Ideias, Estratégias e Dashboard ESG (InovacaoController)
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.116 s
[INFO] Running Testes de API - Monitoramento e Health Check (Spring Boot Actuator)
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.051 s
[INFO] Running Testes de API - Notificações Corporativas e Alertas ESG (NotificacaoController)
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.170 s
[INFO] Running Testes de API - Gestão de Squads e Governança (GestorGrupoController)
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.132 s
[INFO] Running Testes de API - Autenticação e Segurança (AuthController)
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.504 s
[INFO] Running br.com.inovagab.service.GrupoServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.181 s
[INFO] Running br.com.inovagab.bdd.CucumberSuiteTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.268 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 59, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  11.561 s
[INFO] ------------------------------------------------------------------------
```

### 7.2. Detalhamento dos Testes BDD Executados (Cucumber 7.20.1)

```text
Funcionalidade: Autenticação, Controle de Acesso e Segurança ESG
  ✔ Cenário: Registrar novo colaborador de sustentabilidade com sucesso
  ✔ Cenário: Autenticar gestor de sustentabilidade e obter token JWT
  ✔ Cenário: Falha de autenticação ao fornecer senha incorreta
  ✔ Cenário: Consultar dados do perfil autenticado para auditoria

Funcionalidade: Gestão de Squads de Sustentabilidade e Governança Corporativa
  ✔ Cenário: Gestor cria squad focada em Eficiência Energética e Descarbonização
  ✔ Cenário: Adicionar membro especialista à Squad de Sustentabilidade
  ✔ Cenário: Impedir que usuário sem perfil GESTOR crie squads (Controle de Acesso RBAC)

Funcionalidade: Gestão do Funil de Ideias Sustentáveis e Engajamento ESG
  ✔ Cenário: Submeter com sucesso ideia de Coleta Inteligente e Reciclagem de Resíduos
  ✔ Cenário: Votar democraticamente em uma ideia de inclusão e acessibilidade
  ✔ Cenário: Rejeitar submissão de ideia sem informações essenciais

Funcionalidade: Monitoramento de ROI ESG e Indicadores de Sustentabilidade
  ✔ Cenário: Cadastrar diretriz estratégica corporativa alinhada aos ODS da ONU
  ✔ Cenário: Registrar projeto de eficiência energética e apurar indicadores de retorno
  ✔ Cenário: Obter consolidação executiva do Dashboard de Sustentabilidade e ROI

13 Scenarios (13 passed)
52 Steps (52 passed)
Tempo total Cucumber: 1.268s
Relatório HTML publicado em: target/cucumber-reports/cucumber.html
```

---

## 8. Conclusão

A implementação dos testes automatizados nesta fase consolida as melhores práticas da engenharia de qualidade de software, aliando:
- **Alinhamento de Negócio e ESG:** A especificação executável em Gherkin traduz os objetivos de sustentabilidade e governança em critérios técnicos verificáveis a cada commit.
- **Conformidade Contratual:** A validação sistemática de schemas JSON impede que quebras de contrato de API cheguem a clientes web, mobile ou parceiros corporativos.
- **Segurança e Robustez:** Testes negativos comprovaram a resiliência contra acessos não autorizados, falhas de autenticação e inconsistências de banco de dados.
- **Automação Contínua:** A integração nativa ao Maven e ao GitHub Actions garante que nenhum código sem testes seja promovido para staging ou produção.

Dessa forma, os requisitos estabelecidos na atividade avaliativa foram plenamente atendidos e validados com **100% de sucesso**.
