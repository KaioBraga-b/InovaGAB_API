# InovaGAB - Plano Completo de Migração Backend: Firebase para Java Spring Boot & MongoDB

**Documento:** Plano Arquitetural, Mapeamento de Endpoints e Especificação RESTful  
**Projeto:** InovaGAB (Inovação Corporativa - Grupo Águia Branca)  
**Stack de Destino:** Java 21+ / Spring Boot, Spring Security (JWT), Spring Data MongoDB (NoSQL)

---

## 1. Mapeamento e Citação de Todos os Endpoints do Aplicativo

Com base no documento de especificação da API (*InovaGAB - Especificação da API Backend*) e nas regras e telas do aplicativo Android (Kotlin / Jetpack Compose), a seguir estão listados todos os endpoints existentes no projeto, agrupados por domínio de negócio, acompanhados de suas permissões, métodos HTTP e finalidades.

---

### 1.1 Módulo de Autenticação e Usuários
**Base URL:** `/api/auth`

| Método | Endpoint | Perfil Autorizado | Descrição | Status HTTP |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | Público | Realiza a autenticação com e-mail, senha e validação do perfil selecionado (`role`: `OPERADOR`, `GESTOR`, `LIDER`). Retorna o token de acesso JWT e os dados cadastrais essenciais. | `200 OK` / `401 Unauthorized` |
| **POST** | `/api/auth/register` | Público | Registra um novo colaborador no sistema. Por padrão corporativo, é criado com o cargo `OPERADOR`. | `201 Created` / `400 Bad Request` |
| **GET** | `/api/auth/me` | Autenticado (`Bearer`) | Retorna os dados do usuário logado (usado na `ProfileScreen` e cabeçalhos de tela do App). | `200 OK` / `401 Unauthorized` |

#### Detalhamento Técnico dos Payloads:
- **POST `/api/auth/login`**
  - **Request Body:**
    ```json
    {
      "email": "operador@aguiabranca.com.br",
      "password": "senhaSegura123",
      "role": "OPERADOR"
    }
    ```
  - **Response (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "type": "Bearer",
      "user": {
        "id": "65e8a1f2b4c1a2001c89ef01",
        "nome": "Carlos",
        "sobrenome": "Silva",
        "email": "operador@aguiabranca.com.br",
        "role": "OPERADOR",
        "unidade": "Garagem Vitória - ES"
      }
    }
    ```
- **POST `/api/auth/register`**
  - **Request Body:**
    ```json
    {
      "nome": "Carlos",
      "sobrenome": "Silva",
      "email": "operador@aguiabranca.com.br",
      "password": "senhaSegura123",
      "unidade": "Garagem Vitória - ES"
    }
    ```
  - **Response (201 Created):**
    ```json
    {
      "id": "65e8a1f2b4c1a2001c89ef01",
      "nome": "Carlos",
      "sobrenome": "Silva",
      "email": "operador@aguiabranca.com.br",
      "role": "OPERADOR",
      "unidade": "Garagem Vitória - ES",
      "createdAt": "2026-09-10T21:00:00Z"
    }
    ```
- **GET `/api/auth/me`**
  - **Header:** `Authorization: Bearer <token>`
  - **Response (200 OK):** Objeto User completo.

---

### 1.2 Módulo de Ideias de Inovação
**Base URL:** `/api/ideias`

| Método | Endpoint | Perfil Autorizado | Descrição | Status HTTP |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/ideias` | `GESTOR`, `LIDER` | Lista todas as ideias do sistema. Suporta filtros opcionais por `area` ou `status` (ex: `Enviada`, `Em análise`, `Aprovada`, `Recusada`). | `200 OK` |
| **GET** | `/api/ideias/user/{userId}` | `OPERADOR`, `GESTOR` | Lista as ideias específicas submetidas por um operador (utilizado na `MinhasIdeiasScreen`). | `200 OK` |
| **POST** | `/api/ideias` | Todos (`OPERADOR` prioritário) | Cadastra uma nova ideia/problema enfrentado pelo operador, permitindo vincular com a estratégia vigente. | `201 Created` |
| **PUT** | `/api/ideias/{id}` | Autor da ideia ou `GESTOR` | Atualiza os dados de uma ideia (título, descrição, área). | `200 OK` |
| **PATCH** | `/api/ideias/{id}/status` | `GESTOR` | Altera o status da ideia durante a curadoria (Aprovar/Recusar, etapa e progresso). | `200 OK` |
| **POST** | `/api/ideias/{id}/vote` | Todos autenticados | Incrementa o contador de votos/apoio comunitário de uma ideia. | `200 OK` |
| **DELETE** | `/api/ideias/{id}` | Autor da ideia ou `GESTOR` | Remove permanentemente uma ideia do sistema. | `204 No Content` |

#### Detalhamento Técnico dos Payloads:
- **POST `/api/ideias`**
  - **Request Body:**
    ```json
    {
      "titulo": "Otimização de Rota Vitória x Linhares",
      "descricao": "Ajustar paradas intermediárias nos horários de pico para economizar combustível e tempo.",
      "area": "Operação Rodoviária",
      "impacto": "Alto",
      "objetivo": "Redução de custos e aumento de pontualidade",
      "prioridade": "Média",
      "estrategiaId": "65e8a1f2b4c1a2001c89ef99"
    }
    ```
  - **Response (201 Created):** Objeto Ideia criado com `id`, `votos: 0`, `status: "Enviada"`, `dataCriacao`.
- **PATCH `/api/ideias/{id}/status`**
  - **Request Body:**
    ```json
    {
      "status": "Aprovada",
      "justificativa": "Viabilidade técnica confirmada e alinhada à estratégia de descarbonização."
    }
    ```
  - **Response (200 OK):** Objeto Ideia atualizado com `status: "Aprovada"`, `etapa: "Aprovada pelo gestor"`, `progresso: 1.0`.

---

### 1.3 Módulo de Projetos e Iniciativas (Dashboard Operacional)
**Base URL:** `/api/projetos`

| Método | Endpoint | Perfil Autorizado | Descrição | Status HTTP |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/projetos` | Todos autenticados | Retorna a lista de projetos ativos, suas etapas atuais e progresso percentual. | `200 OK` |
| **POST** | `/api/projetos` | `GESTOR` | Cria um novo projeto/iniciativa a ser implementado pela área, vinculado à estratégia vigente. | `201 Created` |
| **PUT** | `/api/projetos/{id}` | `GESTOR` | Atualiza o progresso, etapa ativa (Ideação, Aprovação, Execução, Resultado), status e dados financeiros. | `200 OK` |
| **DELETE** | `/api/projetos/{id}` | `GESTOR` ou `LIDER` | Remove um projeto do sistema. | `204 No Content` |

#### Detalhamento Técnico dos Payloads:
- **POST `/api/projetos`**
  - **Request Body:**
    ```json
    {
      "titulo": "Rota Inteligente GAB",
      "area": "Logística",
      "etapaAtiva": 0,
      "progresso": 0.1,
      "investimento": 50000.00,
      "retornoMensalEstimado": 12000.00,
      "prazoMeses": 6,
      "estrategiaId": "65e8a1f2b4c1a2001c89ef99"
    }
    ```
  - **Response (201 Created):** Objeto Projeto persistido com `id`, status calculado e cores associadas.
- **PUT `/api/projetos/{id}`**
  - **Request Body:**
    ```json
    {
      "titulo": "Rota Inteligente GAB",
      "area": "Logística",
      "etapaAtiva": 2,
      "progresso": 0.65,
      "investimentoRealizado": 45000.00,
      "retornoObtido": 68000.00,
      "aumentoProdutividade": 18.5
    }
    ```
  - **Response (200 OK):** Objeto Projeto atualizado.

---

### 1.4 Módulo de Orientações Estratégicas (Liderança)
**Base URL:** `/api/estrategias`

| Método | Endpoint | Perfil Autorizado | Descrição | Status HTTP |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/estrategias` | Todos (`OPERADOR`, `GESTOR`, `LIDER`) | Lista os objetivos estratégicos vigentes da empresa para direcionar os times. | `200 OK` |
| **POST** | `/api/estrategias` | `LIDER` exclusivo | Cria uma nova orientação estratégica para o grupo (CRUD Liderança). | `201 Created` |
| **PUT** | `/api/estrategias/{id}` | `LIDER` exclusivo | Atualiza dados, fase (Planejamento, Em andamento, Concluído) e progresso da estratégia. | `200 OK` |
| **DELETE** | `/api/estrategias/{id}` | `LIDER` exclusivo | Remove uma orientação estratégica do sistema. | `204 No Content` |
| **GET** | `/api/estrategias/historico` | Todos autenticados | Consulta o registro histórico das estratégias (id, data, categoria, campanha). | `200 OK` |

#### Detalhamento Técnico dos Payloads:
- **POST `/api/estrategias`**
  - **Request Body:**
    ```json
    {
      "titulo": "Eficiência Operacional e Descarbonização",
      "descricao": "Redução de emissões e economia de combustível através de condução econômica e rotas inteligentes.",
      "categoria": "Sustentabilidade",
      "campanha": "EcoÁguia 2026",
      "etapa": "Planejamento",
      "progresso": 0.1
    }
    ```
  - **Response (201 Created):** Objeto Estratégia persistido.

---

### 1.5 Módulo de Dashboard Executivo e Relatórios (Liderança e Gestão)
**Base URL:** `/api/dashboard`

| Método | Endpoint | Perfil Autorizado | Descrição | Status HTTP |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/dashboard/resumo-geral` | `LIDER`, `GESTOR` | Retorna o consolidado estruturado: ROI Total, Lucro Obtido, Investimento Total, Prazo Médio, Aumento de Produtividade, Total de Ideias e Taxa de Engajamento. | `200 OK` |
| **GET** | `/api/dashboard/estrategia/{estrategiaId}` | `LIDER`, `GESTOR` | Retorna o relatório específico de uma estratégia (projetos vinculados, retorno financeiro gerado, ideias aplicadas). | `200 OK` |
| **GET** | `/api/dashboard/projeto/{projetoId}` | `LIDER`, `GESTOR` | Retorna o detalhamento individual de métricas de um projeto (ROI específico, investimento vs retorno, prazo e cronograma). | `200 OK` |

#### Payload de Resumo Geral (`GET /api/dashboard/resumo-geral`):
```json
{
  "roiTotalPercentual": 220.0,
  "lucroObtidoTotal": 142000.00,
  "investimentoTotal": 65000.00,
  "projetosAtivos": 4,
  "projetosNoPrazo": 3,
  "ideiasRegistradas": 148,
  "taxaEngajamento": 68.0,
  "aumentoMedioProdutividade": 18.5,
  "retornosPorEstrategia": [
    {
      "estrategiaId": "65e8a1f2b4c1a2001c89ef99",
      "estrategiaTitulo": "Eficiência Operacional e Descarbonização",
      "totalProjetos": 2,
      "investimentoTotal": 45000.00,
      "retornoTotal": 120000.00,
      "roi": 166.6
    }
  ]
}
```

---

## 2. Decisão Técnica: MongoDB (NoSQL) vs JPA/Hibernate

O pedido solicita:
> *"1. Java: Spring Boot, Spring Security, JPA/Hibernate ... Banco de dados Mongodb (no SQL)"*

### Esclarecimento e Diretriz de Engenharia:
1. **JPA (Java Persistence API) e Hibernate** foram concebidos e padronizados estritamente para o paradigma **Relacional (SQL / RDBMS)** (tabelas, colunas, chaves primárias, chaves estrangeiras, joins e dialetos SQL).
2. O **MongoDB** é um banco de dados **NoSQL orientado a documentos BSON (JSON binário)**, caracterizado por esquemas flexíveis e ausência de joins nativos relacionais rígidos.
3. No ecossistema oficial do Spring Framework, para bancos MongoDB utiliza-se o **Spring Data MongoDB** (`spring-boot-starter-data-mongodb`), que substitui o Spring Data JPA / Hibernate de forma nativa e sem gambiarras.
4. O Spring Data MongoDB oferece **exatamente a mesma elegância arquitetural e padrões de design** que o JPA oferece:
   - Interfaces `MongoRepository<T, ID>` com injeção automática de métodos de CRUD (`save`, `findById`, `findAll`, `deleteById`).
   - Mapeamento por anotações de documento: `@Document(collection = "...")`, `@Id`, `@Field`, `@Indexed`.
   - Suporte a pipelines de agregação complexos via `MongoTemplate` para relatórios analíticos de alta performance (ROI, somatórios e médias).
   - Transações com `@Transactional` caso sejam configurados replica sets.
5. **Benefício para o InovaGAB:** A migração do Firebase Firestore (que já é NoSQL em documentos) para o MongoDB torna-se quase 1:1 na modelagem conceitual, aproveitando todo o poder de tipagem forte e validação do Java, sem o atrito de tentar forçar um ORM relacional (Hibernate) em uma base de documentos.

---

## 3. Desenho da Arquitetura RESTful em Camadas

A arquitetura foi desenhada seguindo o padrão **Clean Layered Architecture**, separando estritamente a camada de apresentação, regras de negócio, persistência e segurança:

```
                    [ App Android (Jetpack Compose) ]
                                    │
                         HTTPS + JWT Bearer Token
                                    ▼
┌───────────────────────────────────────────────────────────────────────┐
│                           SECURITY LAYER                              │
│  JwtAuthenticationFilter  ──►  JwtTokenProvider  ──►  SecurityConfig │
└───────────────────────────────────┬───────────────────────────────────┘
                                    ▼
┌───────────────────────────────────────────────────────────────────────┐
│                          CONTROLLER LAYER                             │
│     AuthController  |  IdeiaController  |  ProjetoController          │
│          EstrategiaController  |  DashboardController                 │
│         (@RestController, @Valid, @PreAuthorize, ResponseEntity)      │
└───────────────────┬───────────────────────────────┬───────────────────┘
                    │                               │
                    ▼                               ▼
       ┌────────────────────────┐      ┌────────────────────────┐
       │     DTO / DTA LAYER    │      │    EXCEPTION LAYER     │
       │  Request DTOs (Input)  │      │ GlobalExceptionHandler │
       │  Response DTOs (Output)│      │  StandardError Payload │
       └────────────────────────┘      └────────────────────────┘
                    │
                    ▼
┌───────────────────────────────────────────────────────────────────────┐
│                           SERVICE LAYER                               │
│       AuthService  |  IdeiaService  |  ProjetoService                  │
│           EstrategiaService  |  DashboardService                      │
│     (Regras de Negócio, Cálculo de ROI, Validações de Papéis)         │
└───────────────────────────────────┬───────────────────────────────────┘
                                    ▼
┌───────────────────────────────────────────────────────────────────────┐
│                          REPOSITORY LAYER                             │
│   UsuarioRepository | IdeiaRepository | ProjetoRepository             │
│        EstrategiaRepository | HistoricoEstrategiaRepository           │
│        (Spring Data MongoRepository & Agregações MongoTemplate)       │
└───────────────────────────────────┬───────────────────────────────────┘
                                    ▼
┌───────────────────────────────────────────────────────────────────────┐
│                       MODEL / DOCUMENT LAYER                          │
│   @Document: Usuario | Ideia | Projeto | Estrategia | Historico       │
└───────────────────────────────────┬───────────────────────────────────┘
                                    ▼
                        [( Banco de Dados MongoDB )]
```

### Responsabilidade de Cada Camada:
1. **Controller (`controller`):** Ponto de entrada HTTP REST. Recebe as requisições, valida o corpo com `@Valid`, autoriza os perfis com `@PreAuthorize("hasRole('...')")` e devolve respostas com códigos semânticos e DTOs tipados.
2. **DTO / DTA (`dto`):** Separação clara entre **Request DTOs** (dados recebidos) e **Response DTOs** (dados devolvidos). Impede que campos confidenciais (ex: hash de senhas) sejam expostos e desacopla a API pública do esquema físico do banco de dados.
3. **Service (`service`):** O coração da aplicação. Executa as validações de negócio, orquestra operações entre múltiplos repositórios, realiza a transição de status das ideias, garante o vínculo obrigatório com a estratégia vigente e calcula as métricas do Dashboard (ROI, lucros, prazos e produtividade).
4. **Repository (`repository`):** Gerencia a persistência no MongoDB com zero boilerplate graças ao Spring Data, fornecendo queries customizadas (`findByArea`, `findByUserId`, etc.) e suporte ao `MongoTemplate` para relatórios analíticos.
5. **Model / Document (`model`):** Classes Java anotadas com `@Document` que representam a estrutura de dados persistida nas coleções do MongoDB.
6. **Security (`security`):** Intercepta e autentica os tokens JWT, valida assinaturas HMAC-SHA256, carrega os dados do usuário e injeta as `GrantedAuthorities` no contexto do Spring Security.
7. **Exception Handler (`exception`):** Captura qualquer exceção não tratada ou de negócio (`ResourceNotFoundException`, `BusinessException`, erros de validação `@Valid`) e retorna um JSON estruturado com status HTTP adequado, evitando falhas silenciosas ou crashes no cliente Android.

---

## 4. Localização e Mapeamento de Cada Pasta e Arquivo

Todas as pastas foram criadas e localizadas dentro do caminho base da API Java:  
`c:\Users\Caio\Desktop\TRABALHO ENTREGARS\InovaGAB\API JAVA\src\main\java\br\com\inovagab\`

Abaixo, a localização detalhada de cada pasta e seus respectivos componentes:

### 📁 1. `config/` (Configurações Gerais do Sistema)
- **Localização:** `br.com.inovagab.config`
- **Componentes:**
  - `SecurityConfig.java`: Configuração de segurança do Spring Security (CSRF desabilitado, sessão STATELESS, regras de autorização de rotas, encoder BCrypt e configuração de CORS para permitir requisições do App Android no emulador `10.0.2.2` e dispositivos físicos).
  - `MongoConfig.java`: Habilita auditoria no MongoDB (`@EnableMongoAuditing`) para preenchimento automático de datas de criação e alteração (`@CreatedDate`, `@LastModifiedDate`).
  - `OpenApiConfig.java`: Configura a documentação automática do Swagger / OpenAPI 3.0 com suporte a autenticação Bearer JWT no Swagger UI.

### 📁 2. `model/` (Documentos do MongoDB)
- **Localização:** `br.com.inovagab.model`
- **Componentes:**
  - `Role.java`: Enum com os 3 perfis exigidos: `OPERADOR`, `GESTOR`, `LIDER`.
  - `Usuario.java`: `@Document(collection = "usuarios")` contendo `id`, `nome`, `sobrenome`, `email`, `senha` (hash BCrypt), `role`, `unidade`, `ativo`, `dataCriacao`.
  - `Ideia.java`: `@Document(collection = "ideias")` contendo `id`, `userId`, `autor`, `titulo`, `descricao`, `area`, `impacto`, `objetivo`, `prioridade`, `status`, `etapa`, `progresso`, `votos`, `estrategiaId`, `dataCriacao`.
  - `Projeto.java`: `@Document(collection = "projetos")` contendo `id`, `titulo`, `area`, `status`, `etapaAtiva`, `progresso`, `periodo`, `investimento`, `retornoFinanceiro`, `lucroObtido`, `roiPercentual`, `aumentoProdutividadePercentual`, `prazoMeses`, `estrategiaId`, `dataInicio`.
  - `Estrategia.java`: `@Document(collection = "estrategias")` contendo `id`, `titulo`, `descricao`, `categoria`, `campanha`, `status`, `progresso`, `dataCriacao`, `ativa`.
  - `HistoricoEstrategia.java`: `@Document(collection = "historico_estrategias")` contendo `id`, `estrategiaId`, `dataRegistro`, `categoria`, `campanha`, `resultadoFinal`, `roiAlcancado`.

### 📁 3. `dto/` (Objetos de Transferência de Dados - DTAs)
- **Localização:** `br.com.inovagab.dto`
- **Subpastas:**
  - **`dto/request/`**:
    - `LoginRequest.java`: DTO com validações (`email`, `password`, `role`).
    - `RegisterRequest.java`: DTO com validações (`nome`, `sobrenome`, `email`, `password`, `unidade`).
    - `IdeiaRequest.java`: DTO para criação e edição de ideia (`titulo`, `descricao`, `area`, `impacto`, `objetivo`, `estrategiaId`).
    - `IdeiaStatusRequest.java`: DTO para curadoria (`status`, `justificativa`).
    - `ProjetoRequest.java`: DTO para criação e atualização de projetos (`titulo`, `area`, `etapaAtiva`, `progresso`, `investimento`, `retornoFinanceiro`, `estrategiaId`).
    - `EstrategiaRequest.java`: DTO para cadastro de orientação estratégica pela liderança.
  - **`dto/response/`**:
    - `AuthResponse.java`: Retorna o token JWT e dados resumidos do usuário autenticado.
    - `UserResponse.java`: Retorna perfil completo sem expor a senha.
    - `IdeiaResponse.java`: Dados completos da ideia incluindo status, votos e cores visuais.
    - `ProjetoResponse.java`: Dados completos do projeto, etapa e métricas.
    - `EstrategiaResponse.java`: Dados da orientação estratégica.
    - `HistoricoEstrategiaResponse.java`: Dados do histórico da estratégia (id, data, categoria, campanha).
    - `DashboardResumoResponse.java`: Resumo estruturado para a liderança (ROI total, lucros, prazos, investimentos, engajamento e retornos por estratégia).

### 📁 4. `repository/` (Acesso a Dados com Spring Data MongoDB)
- **Localização:** `br.com.inovagab.repository`
- **Componentes:**
  - `UsuarioRepository.java`: `extends MongoRepository<Usuario, String>` (`findByEmail`, `existsByEmail`).
  - `IdeiaRepository.java`: `extends MongoRepository<Ideia, String>` (`findByUserId`, `findByStatus`, `findByArea`, `findByEstrategiaId`).
  - `ProjetoRepository.java`: `extends MongoRepository<Projeto, String>` (`findByStatus`, `findByEstrategiaId`).
  - `EstrategiaRepository.java`: `extends MongoRepository<Estrategia, String>` (`findByAtivaTrue`).
  - `HistoricoEstrategiaRepository.java`: `extends MongoRepository<HistoricoEstrategia, String>` (`findByEstrategiaIdOrderByDataRegistroDesc`).

### 📁 5. `service/` (Regras de Negócio e Cálculos)
- **Localização:** `br.com.inovagab.service`
- **Componentes:**
  - `AuthService.java`: Validação de credenciais, correspondência do perfil (role) selecionado com o banco, hash com BCrypt e geração do token JWT.
  - `IdeiaService.java`: CRUD de ideias, contagem de votos atômica, fluxo de aprovação/rejeição pelo gestor e vinculação com estratégia ativa.
  - `ProjetoService.java`: Cadastro e acompanhamento de projetos pelos gestores, atualização de progresso, cálculo automático de ROI e lucro obtido.
  - `EstrategiaService.java`: Gestão das orientações estratégicas pela liderança, controle de permissões e geração de histórico imutável.
  - `DashboardService.java`: Agregações no MongoDB para gerar o relatório executivo (cálculo do ROI total, somatórios de lucros, médias de produtividade e engajamento).

### 📁 6. `controller/` (Controladores RESTful)
- **Localização:** `br.com.inovagab.controller`
- **Componentes:**
  - `AuthController.java`: Endpoints `/api/auth/login`, `/api/auth/register`, `/api/auth/me`.
  - `IdeiaController.java`: Endpoints `/api/ideias/**` (listagem, cadastro, curadoria, votação, exclusão).
  - `ProjetoController.java`: Endpoints `/api/projetos/**` (CRUD de projetos e progresso).
  - `EstrategiaController.java`: Endpoints `/api/estrategias/**` (CRUD de estratégias e histórico).
  - `DashboardController.java`: Endpoints `/api/dashboard/**` (relatórios analíticos e KPIs para líderes).

### 📁 7. `security/` (Infraestrutura de Segurança e JWT)
- **Localização:** `br.com.inovagab.security`
- **Componentes:**
  - `JwtTokenProvider.java`: Criação, assinatura (HMAC-SHA256) e validação da expiração de tokens JWT.
  - `JwtAuthenticationFilter.java`: Filtro OncePerRequestFilter que extrai o Bearer token do header e autentica o usuário no `SecurityContextHolder`.
  - `UserDetailsServiceImpl.java`: Implementa `UserDetailsService` para consultar o usuário no MongoDB.
  - `UserPrincipal.java`: Implementa `UserDetails`, contendo id, email, senha e autoridades (`ROLE_OPERADOR`, `ROLE_GESTOR`, `ROLE_LIDER`).

### 📁 8. `exception/` (Tratamento Global de Exceções)
- **Localização:** `br.com.inovagab.exception`
- **Componentes:**
  - `StandardError.java`: Payload padrão de erro contendo `timestamp`, `status`, `error`, `message`, `path`.
  - `ResourceNotFoundException.java`: Lançada quando um ID (ideia, projeto, estratégia) não é encontrado (HTTP 404).
  - `BusinessException.java`: Lançada para violações de regras de negócio ou permissões (HTTP 400 ou 403).
  - `GlobalExceptionHandler.java`: Anotado com `@RestControllerAdvice`, intercepta erros de validação `@Valid`, acessos não autorizados e exceções personalizadas, retornando respostas amigáveis para o App Android.

---

## 5. Modelagem de Dados MongoDB e Relacionamentos NoSQL

### Estrutura de Documentos e Vínculos:
No MongoDB, o vínculo entre entidades é estabelecido através do identificador `String estrategiaId` indexado no documento dependente:

```
┌─────────────────────────────────┐
│     Estrategia (Coleção)        │
│ _id: ObjectId("...")            │
│ titulo: "EcoÁguia Sustentável"  │
│ categoria: "Sustentabilidade"   │
└────────────────┬────────────────┘
                 │
                 ├───────────────────────────────┐
                 ▼ (1:N)                         ▼ (1:N)
┌─────────────────────────────────┐   ┌─────────────────────────────────┐
│        Ideia (Coleção)          │   │       Projeto (Coleção)         │
│ _id: ObjectId("...")            │   │ _id: ObjectId("...")            │
│ titulo: "Sensores de Consumo"   │   │ titulo: "Telemetria Avançada"   │
│ estrategiaId: ObjectId("...")   │   │ estrategiaId: ObjectId("...")   │
│ autor: "Carlos Silva"           │   │ investimento: 60000.00          │
│ status: "Aprovada"              │   │ retornoFinanceiro: 180000.00    │
│ votos: 35                       │   │ roiPercentual: 200.0            │
└─────────────────────────────────┘   └─────────────────────────────────┘
```

---

## 6. Plano de Execução da Migração Passo a Passo (Roadmap)

A migração do Firebase para Java Spring Boot + MongoDB deve seguir rigorosamente as seguintes 5 etapas:

### Etapa 1: Preparação do Ambiente e Infraestrutura
1. **Banco de Dados MongoDB:**
   - Iniciar o container local via Docker Compose:
     ```bash
     docker compose -f "API JAVA/compose.yaml" up -d
     ```
   - O banco estará disponível na porta `27017` com usuário `root` e senha `secret`.
2. **Configuração da Aplicação Spring Boot:**
   - O arquivo `application.properties` apontará para a URI do MongoDB:
     ```properties
     spring.data.mongodb.uri=mongodb://root:secret@localhost:27017/inovagab_db?authSource=admin
     spring.data.mongodb.auto-index-creation=true
     inovagab.jwt.secret=9a3f8b1c4e7d2a5f8e0b3c6d9a1f4e7d2a5f8e0b3c6d9a1f4e7d2a5f8e0b3c6d
     inovagab.jwt.expiration-ms=86400000
     ```

### Etapa 2: Implementação do Backend Java Spring Boot
1. Atualizar o `pom.xml` incluindo as dependências JWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).
2. Implementar as classes de cada pacote localizado:
   - `model` -> `repository` -> `dto` -> `service` -> `security` -> `controller` -> `exception`.
3. Compilar e executar os testes unitários e de integração com Maven Wrapper:
   ```bash
   ./mvnw clean test
   ```
4. Subir a API e testar todos os endpoints interativamente via Swagger UI:
   `http://localhost:8080/swagger-ui/index.html`

### Etapa 3: Script de Migração de Dados (ETL Firebase -> MongoDB)
1. **Extração:** Desenvolver script em Python ou Node.js que utiliza as credenciais de serviço do Firebase Admin SDK (`serviceAccountKey.json`) para ler as coleções `usuarios`, `ideias`, `projetos` e `estrategias`.
2. **Transformação:**
   - Como os hashes de senha do Firebase Auth utilizam algoritmo SCRYPT proprietário do Google, definir estratégia:
     - Definir uma senha padrão temporária (ex: `InovaGAB@2026`) com hash BCrypt para os usuários importados, forçando a redefinição no primeiro login, OU enviar link de reset via e-mail.
   - Preservar os mesmos identificadores (`_id` no MongoDB igual ao document ID do Firestore) para manter íntegros os relacionamentos de `userId` e `estrategiaId`.
3. **Carga:** Inserir os documentos transformados no MongoDB utilizando o driver oficial ou `mongoimport`.

### Etapa 4: Adaptação do Aplicativo Android (Kotlin / Jetpack Compose)
1. **Configuração de Dependências no Android:**
   - Adicionar Retrofit, OkHttp e Logging Interceptor no `app/build.gradle.kts`:
     ```kotlin
     implementation("com.squareup.retrofit2:retrofit:2.11.0")
     implementation("com.squareup.retrofit2:converter-gson:2.11.0")
     implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
     implementation("androidx.security:security-crypto:1.1.0-alpha06")
     ```
2. **Camada de Rede no Android:**
   - Criar `InovaGabApi.kt` com a interface Retrofit definindo os endpoints exatos mapeados.
   - Criar `AuthInterceptor.kt` para ler o token JWT salvo no `EncryptedSharedPreferences` e adicionar o cabeçalho `Authorization: Bearer <token>`.
3. **Refatoração dos ViewModels:**
   - `AuthViewModel.kt`: Substituir `FirebaseAuth` por chamadas à API Java (`api.login(...)`, `api.register(...)`, `api.getMe()`).
   - `InovacaoViewModel.kt`: Substituir `FirebaseFirestore.collection(...).addSnapshotListener` por chamadas REST assíncronas com Kotlin Coroutines e StateFlow/mutableStateOf.

### Etapa 5: Homologação, Testes E2E e Virada de Chave (Cutover)
1. **Validação das 3 Matrizes de Permissão:**
   - Operador: Acesso restrito a criar/listar suas próprias ideias e visualizar estratégias.
   - Gestor: Acesso a aprovar/recusar ideias e gerenciar projetos.
   - Líder: Acesso exclusivo ao CRUD de estratégias e acesso completo aos gráficos/cards do Dashboard de ROI.
2. **Corte e Desativação do Firebase:**
   - Bloquear escritas nas regras de segurança do Firestore (`allow write: if false;`).
   - Publicar a nova versão do App Android apontando a URL base para o backend Java em produção.
   - Monitorar logs e métricas de desempenho.

---

## 7. Conclusão

Com este plano técnico e arquitetural:
1. **Todos os 18 endpoints existentes** no documento de especificação e os novos endpoints analíticos foram formalmente mapeados.
2. A decisão pelo **Spring Data MongoDB** garante alta performance NoSQL, tipagem estrita e conformidade com as melhores práticas de Clean Architecture.
3. Todas as **pastas físicas do projeto Java já foram localizadas e criadas** na estrutura de diretórios do projeto, prontas para receber o código de cada componente na fase de implementação.