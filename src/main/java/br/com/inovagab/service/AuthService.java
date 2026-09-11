package br.com.inovagab.service;

import br.com.inovagab.dto.request.LoginRequest;
import br.com.inovagab.dto.request.RegisterRequest;
import br.com.inovagab.dto.response.AuthResponse;
import br.com.inovagab.dto.response.UserResponse;
import br.com.inovagab.exception.BusinessException;
import br.com.inovagab.exception.ResourceNotFoundException;
import br.com.inovagab.model.Role;
import br.com.inovagab.model.Usuario;
import br.com.inovagab.repository.UsuarioRepository;
import br.com.inovagab.security.JwtTokenProvider;
import br.com.inovagab.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getSenha())) {
            throw new BusinessException("Credenciais inválidas");
        }

        // Valida se o perfil selecionado corresponde ao perfil cadastrado
        if (!usuario.getRole().equals(request.getRole())) {
            throw new BusinessException(
                    "Perfil selecionado não corresponde ao perfil cadastrado para este usuário.");
        }

        if (!usuario.isAtivo()) {
            throw new BusinessException("Usuário inativo. Contate o administrador.");
        }

        UserPrincipal principal = UserPrincipal.create(usuario);
        String token = tokenProvider.generateToken(principal);

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserResponse.builder()
                        .id(usuario.getId())
                        .nome(usuario.getNome())
                        .sobrenome(usuario.getSobrenome())
                        .email(usuario.getEmail())
                        .role(usuario.getRole())
                        .unidade(usuario.getUnidade())
                        .build())
                .build();
    }

    public UserResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado: " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .sobrenome(request.getSobrenome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getPassword()))
                .unidade(request.getUnidade())
                .role(request.getRole() != null ? request.getRole() : Role.OPERADOR)
                .build();

        Usuario saved = usuarioRepository.save(usuario);
        return toUserResponse(saved);
    }

    public UserResponse getMe(String userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));
        return toUserResponse(usuario);
    }

    private UserResponse toUserResponse(Usuario u) {
        return UserResponse.builder()
                .id(u.getId())
                .nome(u.getNome())
                .sobrenome(u.getSobrenome())
                .email(u.getEmail())
                .role(u.getRole())
                .unidade(u.getUnidade())
                .ativo(u.isAtivo())
                .dataCriacao(u.getDataCriacao())
                .build();
    }
}
