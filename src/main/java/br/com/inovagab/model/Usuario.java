package br.com.inovagab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String nome;

    private String sobrenome;

    private String email;

    private String senha;

    private Role role;

    private String unidade;

    @Builder.Default
    private boolean ativo = true;

    @CreatedDate
    private Instant dataCriacao;
}
