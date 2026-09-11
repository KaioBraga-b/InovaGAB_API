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
@Document(collection = "estrategias")
public class Estrategia {

    @Id
    private String id;

    private String criadoPorId;

    private String criadoPorNome;

    private String titulo;

    private String descricao;

    private String categoria;

    private String campanha;

    /** Planejamento | Em andamento | Concluído */
    @Builder.Default
    private String etapa = "Planejamento";

    @Builder.Default
    private double progresso = 0.0;

    @Builder.Default
    private boolean ativa = true;

    @CreatedDate
    private Instant dataCriacao;

    @LastModifiedDate
    private Instant dataAtualizacao;
}
