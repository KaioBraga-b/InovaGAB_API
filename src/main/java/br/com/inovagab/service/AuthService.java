package br.com.inovagab.service;

import br.com.inovagab.dto.request.LoginRequest;
import br.com.inovagab.dto.request.RegisterRequest;
import br.com.inovagab.dto.response.AuthResponse;
import br.com.inovagab.model.Usuario;
import br.com.inovagab.repository.UsuarioRepository;
import br.com.inovagab.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida.");
        }
        
        if (!usuario.getRole().equals(loginRequest.getSelectedProfile())) {
            throw new RuntimeException("Acesso negado: Seu perfil é " + usuario.getRole());
        }

        String token = tokenProvider.generateToken(usuario.getId(), usuario.getRole());
        return new AuthResponse(token, usuario.getId(), usuario.getRole(), usuario.getNome(), usuario.getSobrenome(), usuario.getUnidade(), usuario.getEmail());
    }

    public void registerUser(RegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já está em uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(registerRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setNome(registerRequest.getNome());
        usuario.setSobrenome(registerRequest.getSobrenome());
        usuario.setUnidade(registerRequest.getUnidade());
        usuario.setRole(registerRequest.getRole());

        usuarioRepository.save(usuario);
    }
}
