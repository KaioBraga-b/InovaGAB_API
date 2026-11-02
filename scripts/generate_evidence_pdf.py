#!/usr/bin/env python3
"""
Gerador de Relatório Técnico de Evidências em PDF
FIAP 2026 - Testes Automatizados (BDD Gherkin, APIs e JSON Schema ESG)
Projeto: InovaGAB API (Cidades ESG Inteligentes)
"""

import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable, Image as RLImage
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        if self._pageNumber == 1:
            return  # Não desenha header/footer na capa

        self.saveState()
        self.setFont("Helvetica-Bold", 8)
        self.setFillColor(colors.HexColor("#4B5563"))

        # Cabeçalho
        self.drawString(54, 800, "FIAP 2026 • InovaGAB API — Relatório de Testes Automatizados ESG")
        self.drawRightString(541, 800, "Qualidade de Software & BDD")
        self.setStrokeColor(colors.HexColor("#D1D5DB"))
        self.setLineWidth(0.5)
        self.line(54, 792, 541, 792)

        # Rodapé
        self.line(54, 45, 541, 45)
        self.setFont("Helvetica", 8)
        self.drawString(54, 32, "Cidades ESG Inteligentes • Grupo Águia Branca")
        self.drawRightString(541, 32, f"Página {self._pageNumber} de {page_count}")
        self.restoreState()

def build_pdf(filename="docs/InovaGAB_Evidencias_Testes_Automatizados_ESG.pdf"):
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    doc = SimpleDocTemplate(
        filename,
        pagesize=A4,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()

    # Estilos customizados
    title_style = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=28,
        textColor=colors.HexColor("#14532D"),
        alignment=1, # Centro
        spaceAfter=10
    )

    subtitle_style = ParagraphStyle(
        'CoverSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=13,
        leading=17,
        textColor=colors.HexColor("#374151"),
        alignment=1,
        spaceAfter=20
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Heading1'],
        fontName='Helvetica-Bold',
        fontSize=15,
        leading=19,
        textColor=colors.HexColor("#14532D"),
        spaceBefore=14,
        spaceAfter=8,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        parent=styles['Heading2'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor("#1E3A8A"),
        spaceBefore=10,
        spaceAfter=5,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=colors.HexColor("#1F2937"),
        spaceAfter=6
    )

    body_bold = ParagraphStyle(
        'Body_Bold',
        parent=body_style,
        fontName='Helvetica-Bold'
    )

    code_style = ParagraphStyle(
        'CodeStyle',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=7.5,
        leading=10,
        textColor=colors.HexColor("#111827"),
        spaceAfter=4
    )

    callout_style = ParagraphStyle(
        'CalloutText',
        parent=body_style,
        fontSize=8.5,
        leading=12,
        textColor=colors.HexColor("#065F46")
    )

    story = []

    # ==========================================
    # CAPA
    # ==========================================
    story.append(Spacer(1, 40))
    story.append(Paragraph("🌆 FIAP — PÓS TECH / ADS 2026", ParagraphStyle('TopBadge', parent=body_style, alignment=1, fontName='Helvetica-Bold', textColor=colors.HexColor("#15803D"), fontSize=11)))
    story.append(Spacer(1, 15))
    story.append(Paragraph("RELATÓRIO TÉCNICO DE TESTES AUTOMATIZADOS", title_style))
    story.append(Paragraph("Automação BDD (Cucumber/Gherkin), Testes de API REST e Validação de Contrato (JSON Schema) integrados aos temas ESG", subtitle_style))
    story.append(Spacer(1, 15))

    story.append(HRFlowable(width="80%", thickness=2, color=colors.HexColor("#15803D"), spaceAfter=25, spaceBefore=10))

    # Tabela de Identificação da Equipe
    team_data = [
        [Paragraph("<b>Nome do Aluno</b>", body_style), Paragraph("<b>RM</b>", body_style), Paragraph("<b>Função no Projeto</b>", body_style)],
        [Paragraph("Gabriel Aparecido Lopes de Campos", body_style), Paragraph("RM562021", body_style), Paragraph("QA Engineer & Test Architecture", body_style)],
        [Paragraph("Henry Gabriel Ferreira", body_style), Paragraph("RM565688", body_style), Paragraph("BDD Automation & Backend", body_style)],
        [Paragraph("Kaio Henrique Veríssimo Braga", body_style), Paragraph("RM565115", body_style), Paragraph("CI/CD Pipeline & DevOps", body_style)],
        [Paragraph("Marianna Rocha de Miranda", body_style), Paragraph("RM566305", body_style), Paragraph("API Contract Testing & Schemas", body_style)],
        [Paragraph("Wesley Araujo Chaves", body_style), Paragraph("RM563114", body_style), Paragraph("ESG Requirements & Compliance", body_style)]
    ]
    team_table = Table(team_data, colWidths=[200, 90, 190])
    team_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#F0FDF4")),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#D1D5DB")),
        ('PADDING', (0,0), (-1,-1), 5),
        ('TEXTCOLOR', (0,0), (-1,0), colors.HexColor("#14532D")),
    ]))
    story.append(team_table)
    story.append(Spacer(1, 20))

    info_box = [
        [Paragraph("<b>Disciplina:</b> Testes Automatizados de Software", body_style), Paragraph("<b>Versão da API:</b> 0.0.1-SNAPSHOT (Java 24 / Spring Boot)", body_style)],
        [Paragraph("<b>Parceiro de Negócio:</b> Grupo Águia Branca", body_style), Paragraph("<b>Status da Suíte:</b> 59 Testes aprovados (100% Sucesso)", body_style)],
        [Paragraph("<b>Data da Execução:</b> Novembro / 2026", body_style), Paragraph("<b>Pipeline:</b> GitHub Actions (CI/CD Automático)", body_style)]
    ]
    info_table = Table(info_box, colWidths=[240, 240])
    info_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#F9FAFB")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#E5E7EB")),
        ('PADDING', (0,0), (-1,-1), 6)
    ]))
    story.append(info_table)

    story.append(PageBreak())

    # ==========================================
    # 1. INTRODUÇÃO E ALINHAMENTO ESG
    # ==========================================
    story.append(Paragraph("1. Introdução e Alinhamento com ESG", h1_style))
    story.append(Paragraph(
        "Esta atividade avaliativa integra as técnicas avançadas de <b>Automação de Testes</b> ao projeto corporativo "
        "<b>InovaGAB API</b>, desenvolvido para o <b>Grupo Águia Branca</b> no contexto de Cidades Inteligentes e Sustentáveis. "
        "A qualidade de software desempenha papel crucial nos pilares de sustentabilidade corporativa, assegurando eficiência no uso "
        "de recursos, governança transparente e acessibilidade inclusiva.",
        body_style
    ))

    esg_pillars = [
        [Paragraph("<b>Pilar ESG</b>", body_style), Paragraph("<b>Princípio Aplicado</b>", body_style), Paragraph("<b>Implementação no Sistema e Suíte de Testes</b>", body_style)],
        [
            Paragraph("<b>Environmental<br/>(Ambiental)</b>", body_style),
            Paragraph("Eficiência energética, descarbonização e gestão de resíduos", body_style),
            Paragraph("Cenários BDD e testes de API validando cadastro de ideias de coleta seletiva IoT, usinas solares, ônibus elétricos e metas ODS da ONU.", body_style)
        ],
        [
            Paragraph("<b>Social<br/>(Social)</b>", body_style),
            Paragraph("Inclusão, diversidade corporativa e engajamento comunitário", body_style),
            Paragraph("Validação da formação de squads multidisciplinares, votação democrática de ideias sustentáveis e cotas individuais de participação.", body_style)
        ],
        [
            Paragraph("<b>Governance<br/>(Governança)</b>", body_style),
            Paragraph("Compliance regulatório, rastreabilidade e integridade de contratos", body_style),
            Paragraph("Controle estrito de acesso RBAC (GESTOR, LIDER, OPERADOR), tokens JWT stateless auditáveis e validação sistemática de contratos JSON Schema.", body_style)
        ]
    ]
    esg_table = Table(esg_pillars, colWidths=[100, 160, 220])
    esg_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#14532D")),
        ('TEXTCOLOR', (0,0), (-1,0), colors.white),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#D1D5DB")),
        ('PADDING', (0,0), (-1,-1), 6),
        ('VALIGN', (0,0), (-1,-1), 'TOP')
    ]))
    story.append(esg_table)
    story.append(Spacer(1, 10))

    # ==========================================
    # 2. ARQUITETURA E RESUMO DOS RESULTADOS
    # ==========================================
    story.append(Paragraph("2. Arquitetura da Suíte de Testes e Resumo Geral", h1_style))
    story.append(Paragraph(
        "A suíte totaliza <b>59 testes automatizados</b> executados via Maven Surefire e JUnit Platform Engine, "
        "cobrindo as camadas de unidade, serviços, integração de APIs e comportamento (BDD).",
        body_style
    ))

    summary_data = [
        [Paragraph("<b>Categoria de Teste</b>", body_style), Paragraph("<b>Ferramenta / Framework</b>", body_style), Paragraph("<b>Quantidade</b>", body_style), Paragraph("<b>Status</b>", body_style)],
        [Paragraph("BDD Gherkin (Cenários de Negócio)", body_style), Paragraph("Cucumber 7.20 + JUnit Platform", body_style), Paragraph("13 cenários", body_style), Paragraph("✔ 100% Passou", body_style)],
        [Paragraph("Testes de API & Contrato REST", body_style), Paragraph("REST-Assured 5.5 + JSON Schema", body_style), Paragraph("37 testes", body_style), Paragraph("✔ 100% Passou", body_style)],
        [Paragraph("Testes de Serviços e Regras", body_style), Paragraph("Spring Boot Test + MongoDB Real", body_style), Paragraph("5 testes", body_style), Paragraph("✔ 100% Passou", body_style)],
        [Paragraph("Segurança & Token JWT", body_style), Paragraph("JJWT + Spring Security Test", body_style), Paragraph("3 testes", body_style), Paragraph("✔ 100% Passou", body_style)],
        [Paragraph("Context Bootstrapping", body_style), Paragraph("Spring Boot Starter Test", body_style), Paragraph("1 teste", body_style), Paragraph("✔ 100% Passou", body_style)],
        [Paragraph("<b>TOTAL GERAL EXECUTADO</b>", body_bold), Paragraph("<b>Maven Surefire Suite</b>", body_bold), Paragraph("<b>59 testes</b>", body_bold), Paragraph("<b>✔ 0 Falhas / 0 Erros</b>", body_bold)]
    ]
    summary_table = Table(summary_data, colWidths=[150, 170, 70, 90])
    summary_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1E3A8A")),
        ('TEXTCOLOR', (0,0), (-1,0), colors.white),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#D1D5DB")),
        ('BACKGROUND', (0,-1), (-1,-1), colors.HexColor("#DCFCE7")),
        ('PADDING', (0,0), (-1,-1), 4.5)
    ]))
    story.append(summary_table)

    story.append(PageBreak())

    # ==========================================
    # 3. DETALHAMENTO DOS CENÁRIOS BDD (GHERKIN)
    # ==========================================
    story.append(Paragraph("3. Detalhamento dos Cenários BDD em Gherkin", h1_style))
    story.append(Paragraph(
        "Foram elaborados <b>13 cenários em português</b> cobrindo fluxos positivos (caminho feliz) e negativos (tratamento de falha), "
        "com total aderência aos pilares ESG do Grupo Águia Branca:",
        body_style
    ))

    scenarios_list = [
        ("01_autenticacao_seguranca_esg.feature", [
            ("Cenário 1 (Positivo)", "Registrar novo colaborador de sustentabilidade com sucesso e validação de schema"),
            ("Cenário 2 (Positivo)", "Autenticar gestor de sustentabilidade e obter token JWT rastreável"),
            ("Cenário 3 (Negativo)", "Falha de autenticação ao fornecer senha incorreta (rejeição 400 por compliance)"),
            ("Cenário 4 (Positivo)", "Consultar perfil autenticado para auditoria e rastreabilidade corporativa")
        ]),
        ("02_gestao_squads_esg.feature", [
            ("Cenário 5 (Positivo)", "Gestor cria Squad corporativa focada em Descarbonização da Frota (201 Created)"),
            ("Cenário 6 (Positivo)", "Adição de membro especialista multidisciplinar à Squad de Sustentabilidade"),
            ("Cenário 7 (Negativo)", "Usuário sem permissão tenta criar Squad e recebe bloqueio RBAC 400")
        ]),
        ("03_inovacao_sustentavel_esg.feature", [
            ("Cenário 8 (Positivo)", "Submeter ideia de Coleta Inteligente de Resíduos e Logística Reversa IoT"),
            ("Cenário 9 (Positivo)", "Votação comunitária e colaboração em ideias de inclusão e acessibilidade"),
            ("Cenário 10 (Negativo)", "Validação de payload e integridade ao submeter ideias vazias")
        ]),
        ("04_metricas_dashboard_roi_esg.feature", [
            ("Cenário 11 (Positivo)", "Cadastrar diretriz estratégica corporativa alinhada aos ODS da ONU (Redução de CO2)"),
            ("Cenário 12 (Positivo)", "Registrar projeto de usina solar fotovoltaica com apuração de indicadores e ROI"),
            ("Cenário 13 (Positivo)", "Consulta ao Dashboard executivo com cálculo consolidado de ROI e metas ESG ativas")
        ])
    ]

    for feat_name, scens in scenarios_list:
        story.append(Paragraph(f"<b>Arquivo:</b> <font color='#15803D'>{feat_name}</font>", h2_style))
        s_data = [[Paragraph("<b>Cenário</b>", body_style), Paragraph("<b>Descrição do Comportamento ESG Validado</b>", body_style)]]
        for tag, desc in scens:
            s_data.append([Paragraph(f"<b>{tag}</b>", body_style), Paragraph(desc, body_style)])
        st = Table(s_data, colWidths=[120, 360])
        st.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#F3F4F6")),
            ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#D1D5DB")),
            ('PADDING', (0,0), (-1,-1), 4)
        ]))
        story.append(st)
        story.append(Spacer(1, 4))

    story.append(PageBreak())

    # ==========================================
    # 4. COBERTURA DE APIS E CONTRATOS JSON SCHEMA
    # ==========================================
    story.append(Paragraph("4. Cobertura de APIs e Validação de Contrato (JSON Schema)", h1_style))
    story.append(Paragraph(
        "<b>100% dos controllers e endpoints REST</b> foram validados contra esquemas formais baseados no padrão <b>JSON Schema Draft-07</b>. "
        "As validações garantem que nenhum campo essencial seja omitido ou transmitido com tipo incompatível.",
        body_style
    ))

    api_matrix = [
        [Paragraph("<b>Controller</b>", body_style), Paragraph("<b>Endpoint / Método</b>", body_style), Paragraph("<b>Status</b>", body_style), Paragraph("<b>JSON Schema de Contrato Validado</b>", body_style)],
        [Paragraph("AuthController", body_style), Paragraph("POST /api/auth/register", code_style), Paragraph("200 / 400", body_style), Paragraph("message-response-schema.json", code_style)],
        [Paragraph("AuthController", body_style), Paragraph("POST /api/auth/login", code_style), Paragraph("200 / 400", body_style), Paragraph("auth-response-schema.json", code_style)],
        [Paragraph("AuthController", body_style), Paragraph("GET /api/auth/me", code_style), Paragraph("200 / 400", body_style), Paragraph("user-profile-schema.json", code_style)],
        [Paragraph("GestorGrupoController", body_style), Paragraph("POST /api/gestor/grupos", code_style), Paragraph("201 / 400", body_style), Paragraph("grupo-response-schema.json", code_style)],
        [Paragraph("GestorGrupoController", body_style), Paragraph("GET /api/gestor/grupos", code_style), Paragraph("200", body_style), Paragraph("grupo-list-schema.json", code_style)],
        [Paragraph("GestorGrupoController", body_style), Paragraph("GET /api/gestor/grupos/{id}", code_style), Paragraph("200", body_style), Paragraph("grupo-response-schema.json", code_style)],
        [Paragraph("GestorGrupoController", body_style), Paragraph("PUT /api/gestor/grupos/{id}", code_style), Paragraph("200", body_style), Paragraph("grupo-response-schema.json", code_style)],
        [Paragraph("GestorGrupoController", body_style), Paragraph("POST .../membros", code_style), Paragraph("200", body_style), Paragraph("grupo-response-schema.json", code_style)],
        [Paragraph("GestorGrupoController", body_style), Paragraph("DELETE .../membros/{email}", code_style), Paragraph("200", body_style), Paragraph("grupo-response-schema.json", code_style)],
        [Paragraph("GestorGrupoController", body_style), Paragraph("GET /api/gestor/usuarios", code_style), Paragraph("200", body_style), Paragraph("usuarios-list-schema.json", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("POST/GET /api/inovacao/projetos", code_style), Paragraph("200", body_style), Paragraph("projeto-schema.json / list", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("PUT/DELETE .../projetos/{id}", code_style), Paragraph("200", body_style), Paragraph("projeto-schema.json", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("POST/GET /api/inovacao/estrategias", code_style), Paragraph("200", body_style), Paragraph("estrategia-schema.json / list", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("PUT/DELETE .../estrategias/{id}", code_style), Paragraph("200", body_style), Paragraph("estrategia-schema.json", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("POST/GET /api/inovacao/ideias", code_style), Paragraph("200", body_style), Paragraph("ideia-schema.json / list", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("POST .../ideias/{id}/votar", code_style), Paragraph("200", body_style), Paragraph("ideia-schema.json", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("POST .../ideias/{id}/comentar", code_style), Paragraph("200", body_style), Paragraph("ideia-schema.json", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("POST/GET .../transacoes", code_style), Paragraph("200", body_style), Paragraph("transacao-schema.json / list", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("POST/GET .../receitas; despesas", code_style), Paragraph("200", body_style), Paragraph("transacoes-list-schema.json", code_style)],
        [Paragraph("InovacaoController", body_style), Paragraph("GET /api/inovacao/dashboard", code_style), Paragraph("200", body_style), Paragraph("dashboard-resumo-schema.json", code_style)],
        [Paragraph("NotificacaoController", body_style), Paragraph("GET /api/notificacoes/{role}", code_style), Paragraph("200", body_style), Paragraph("notificacoes-list-schema.json", code_style)],
        [Paragraph("NotificacaoController", body_style), Paragraph("PUT .../notificacoes/{id}/lida", code_style), Paragraph("200", body_style), Paragraph("notificacao-schema.json", code_style)],
        [Paragraph("Actuator", body_style), Paragraph("GET /actuator/health", code_style), Paragraph("200", body_style), Paragraph("health-schema.json", code_style)]
    ]
    api_table = Table(api_matrix, colWidths=[110, 160, 60, 150])
    api_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#065F46")),
        ('TEXTCOLOR', (0,0), (-1,0), colors.white),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#D1D5DB")),
        ('PADDING', (0,0), (-1,-1), 3.5)
    ]))
    story.append(api_table)

    story.append(PageBreak())

    # ==========================================
    # 5. EVIDÊNCIAS DE EXECUÇÃO
    # ==========================================
    story.append(Paragraph("5. Evidências de Execução dos Testes", h1_style))
    story.append(Paragraph("Abaixo são apresentados os registros literais de execução gerados pelo Maven Surefire e Cucumber:", body_style))

    log_box = """
-------------------------------------------------------------------------------
T E S T S   R E S U L T S   -   M A V E N   S U R E F I R E
-------------------------------------------------------------------------------
Running br.com.inovagab.security.JwtTokenProviderTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.149 s
Running br.com.inovagab.InovagabApplicationTests
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.250 s
Running Testes de API - Inovação, Projetos, Ideias, Estratégias e Dashboard ESG (InovacaoController)
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.116 s
Running Testes de API - Monitoramento e Health Check (Spring Boot Actuator)
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.051 s
Running Testes de API - Notificações Corporativas e Alertas ESG (NotificacaoController)
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.170 s
Running Testes de API - Gestão de Squads e Governança (GestorGrupoController)
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.132 s
Running Testes de API - Autenticação e Segurança (AuthController)
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.504 s
Running br.com.inovagab.service.GrupoServiceTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.181 s
Running br.com.inovagab.bdd.CucumberSuiteTest
13 Scenarios (13 passed)
52 Steps (52 passed)
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.268 s

Results:
Tests run: 59, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 11.561 s
    """
    log_p = Paragraph(log_box.strip().replace("\n", "<br/>"), code_style)
    log_table = Table([[log_p]], colWidths=[480])
    log_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#1E293B")),
        ('TEXTCOLOR', (0,0), (-1,-1), colors.HexColor("#38BDF8")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#0F172A")),
        ('PADDING', (0,0), (-1,-1), 8)
    ]))
    story.append(log_table)
    story.append(Spacer(1, 10))

    # Print 1: Terminal
    story.append(Paragraph("<b>Figura 1: Print da Execução Local dos 59 Testes Automatizados via Maven Surefire</b>", h2_style))
    img_term = "docs/images/print_terminal_execucao_59_testes.png"
    if os.path.exists(img_term):
        story.append(RLImage(img_term, width=480, height=260))
    story.append(Spacer(1, 10))

    story.append(PageBreak())

    # Print 2: Cucumber HTML
    story.append(Paragraph("<b>Figura 2: Print do Relatório Interativo BDD do Cucumber (cucumber.html)</b>", h2_style))
    story.append(Paragraph(
        "O Cucumber gera automaticamente o relatório interativo em <code>target/cucumber-reports/cucumber.html</code> "
        "comprovando a taxa de 100% de sucesso nos 13 cenários e 52 passos:", body_style
    ))
    img_cuc = "docs/images/print_cucumber_report_html.png"
    if os.path.exists(img_cuc):
        story.append(RLImage(img_cuc, width=480, height=270))
    story.append(Spacer(1, 10))

    # Print 3: GitHub Actions
    story.append(Paragraph("<b>Figura 3: Print do Pipeline CI/CD no GitHub Actions com MongoDB Service</b>", h2_style))
    story.append(Paragraph(
        "Execução automatizada em cada push/PR na nuvem garantindo a integração contínua e publicação dos artefatos de teste:", body_style
    ))
    img_gh = "docs/images/print_github_actions_pipeline.png"
    if os.path.exists(img_gh):
        story.append(RLImage(img_gh, width=480, height=270))
    story.append(Spacer(1, 10))

    story.append(Spacer(1, 10))
    story.append(Paragraph("6. Conclusão", h1_style))
    story.append(Paragraph(
        "A suíte implementada atende integralmente a todos os requisitos estipulados na atividade avaliativa. "
        "A combinação de <b>BDD com Gherkin</b> em português, <b>testes de contrato com JSON Schema</b> e automação "
        "em <b>pipeline CI/CD</b> comprova a maturidade do processo de engenharia de software e reforça a governança corporativa, "
        "sustentabilidade técnica e responsabilidade social do <b>Grupo Águia Branca</b>.",
        body_style
    ))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"✅ PDF gerado com sucesso em: {filename}")

if __name__ == "__main__":
    build_pdf()
