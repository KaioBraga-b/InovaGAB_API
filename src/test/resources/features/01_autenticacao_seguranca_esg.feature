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
