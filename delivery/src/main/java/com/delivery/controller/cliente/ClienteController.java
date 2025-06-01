package com.delivery.controller.cliente;

import com.delivery.dto.cliente.ClienteDTO;
import com.delivery.service.cliente.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/perfil")
    public ResponseEntity<ClienteDTO> obterPerfil(Authentication authentication) {
        ClienteDTO cliente = clienteService.buscarPorEmail(authentication.getName());
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/perfil")
    public ResponseEntity<ClienteDTO> atualizarPerfil(
            @Valid @RequestBody ClienteDTO clienteDTO,
            Authentication authentication) {

        ClienteDTO cliente = clienteService.atualizarPerfil(clienteDTO, authentication.getName());
        return ResponseEntity.ok(cliente);
    }
}