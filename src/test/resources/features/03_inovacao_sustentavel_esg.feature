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
