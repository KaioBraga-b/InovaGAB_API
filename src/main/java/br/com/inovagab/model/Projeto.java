package br.com.inovagab.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projetos")
public class Projeto {
    @Id
    private String id;
    private String titulo;
    private String status;
    private Integer statusColor;
    private Integer statusBg;
    private String area;
    private String periodo;
    private Double progresso;
    private String progressoTexto;
    private Integer etapaAtiva;
    private String estMensal;
    private String resultadoValor;
    private String resultadoRoi;
    private String investimento;
    private String estrategiaId;
    private String estrategiaTitulo;
    private Double lucroObtido;
    private Double aumentoProdutividade;
    private Boolean noPrazo;
}
