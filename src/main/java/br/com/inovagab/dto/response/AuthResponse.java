package br.com.inovagab.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String userId;
    private String role;
    private String nome;
    private String sobrenome;
    private String unidade;
    private String email;
}
