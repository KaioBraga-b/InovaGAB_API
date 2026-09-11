package br.com.inovagab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projetos")
public class Projeto {

    @Id
    private String id;

    private String criadoPorId;

    private String criadoPorNome;

    private String titulo;

    private String area;

    /** Ideação | Aprovação | Execução | Resultado */
    @Builder.Default
    private String status = "Ideação";

    /** 0 = Ideação, 1 = Aprovação, 2 = Execução, 3 = Resultado */
    @Builder.Default
    private int etapaAtiva = 0;

    @Builder.Default
    private double progresso = 0.0;

    private String periodo;

    @Builder.Default
    private double investimento = 0.0;

    @Builder.Default
    private double investimentoRealizado = 0.0;

    @Builder.Default
    private double retornoMensalEstimado = 0.0;

    @Builder.Default
    private double retornoObtido = 0.0;

    @Builder.Default
    private double lucroObtido = 0.0;

    @Builder.Default
    private double roiPercentual = 0.0;

    @Builder.Default
    private double aumentoProdutividadePercentual = 0.0;

    private int prazoMeses;

    private String estrategiaId;

    @CreatedDate
    private Instant dataInicio;

    @LastModifiedDate
    private Instant dataAtualizacao;
}
