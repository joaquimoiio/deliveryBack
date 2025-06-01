package com.delivery.controller.cliente;

import com.delivery.dto.cliente.ClienteDTO;
import com.delivery.dto.cliente.PedidoDTO;
import com.delivery.service.cliente.ClienteService;
import com.delivery.service.cliente.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;
    private final PedidoService pedidoService;

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

    @PostMapping("/pedidos")
    public ResponseEntity<PedidoDTO> criarPedido(
            @Valid @RequestBody PedidoDTO pedidoDTO,
            Authentication authentication) {

        PedidoDTO pedido = pedidoService.criarPedido(pedidoDTO, authentication.getName());
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/pedidos")
    public ResponseEntity<Page<PedidoDTO>> listarPedidos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoDTO> pedidos = pedidoService.listarPedidosCliente(authentication.getName(), pageable);
        return ResponseEntity.ok(pedidos);
    }
}