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
@Document(collection = "ideias")
public class Ideia {

    @Id
    private String id;

    private String userId;

    private String autor;

    private String titulo;

    private String descricao;

    private String area;

    /** Alto | Médio | Baixo */
    private String impacto;

    private String objetivo;

    /** Alta | Média | Baixa */
    @Builder.Default
    private String prioridade = "Média";

    /** Enviada | Em análise | Aprovada | Recusada */
    @Builder.Default
    private String status = "Enviada";

    @Builder.Default
    private String etapa = "Enviada pelo operador";

    @Builder.Default
    private double progresso = 0.0;

    @Builder.Default
    private int votos = 0;

    private String estrategiaId;

    private String justificativa;

    @CreatedDate
    private Instant dataCriacao;

    @LastModifiedDate
    private Instant dataAtualizacao;
}
