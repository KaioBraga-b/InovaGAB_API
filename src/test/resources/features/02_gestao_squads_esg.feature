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
