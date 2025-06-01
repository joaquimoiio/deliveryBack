package com.delivery.controller.auth;

import com.delivery.dto.auth.TokenResponse;
import com.delivery.entity.base.Usuario;
import com.delivery.repository.UsuarioRepository;
import com.delivery.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", usuario.getId());
        userInfo.put("email", usuario.getEmail());
        userInfo.put("tipoUsuario", usuario.getTipoUsuario());
        userInfo.put("ativo", usuario.getAtivo());
        userInfo.put("createdAt", usuario.getCreatedAt());
        userInfo.put("updatedAt", usuario.getUpdatedAt());

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("user", Map.of(
                "id", usuario.getId(),
                "email", usuario.getEmail(),
                "tipoUsuario", usuario.getTipoUsuario(),
                "ativo", usuario.getAtivo()
        ));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        TokenResponse response = new TokenResponse(
                null,
                usuario.getTipoUsuario(),
                usuario.getId(),
                usuario.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuário desativado com sucesso");

        return ResponseEntity.ok(response);
    }
}