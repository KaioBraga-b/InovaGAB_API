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
