package com.delivery.controller.cliente;

import com.delivery.dto.cliente.PagamentoDTO;
import com.delivery.dto.cliente.PedidoDTO;
import com.delivery.entity.enums.StatusPedido;
import com.delivery.service.cliente.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoClienteController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(
            @Valid @RequestBody PedidoDTO pedidoDTO,
            Authentication authentication) {

        PedidoDTO pedido = pedidoService.criarPedido(pedidoDTO, authentication.getName());
        return ResponseEntity.ok(pedido);
    }

    @GetMapping
    public ResponseEntity<Page<PedidoDTO>> listarMeusPedidos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoDTO> pedidos = pedidoService.listarPedidosCliente(authentication.getName(), pageable);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPedidoPorId(
            @PathVariable Long id,
            Authentication authentication) {

        PedidoDTO pedido = pedidoService.buscarPedidoDoCliente(id, authentication.getName());
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PedidoDTO>> listarPedidosPorStatus(
            @PathVariable StatusPedido status,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoDTO> pedidos = pedidoService.listarPedidosClientePorStatus(
                authentication.getName(), status, pageable);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(
            @PathVariable Long id,
            Authentication authentication) {

        PedidoDTO pedido = pedidoService.cancelarPedido(id, authentication.getName());
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(Authentication authentication) {
        Map<String, Object> stats = pedidoService.obterEstatisticasCliente(authentication.getName());
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/rastrear")
    public ResponseEntity<Map<String, Object>> rastrearPedido(
            @PathVariable Long id,
            Authentication authentication) {

        Map<String, Object> rastreamento = pedidoService.rastrearPedido(id, authentication.getName());
        return ResponseEntity.ok(rastreamento);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<PedidoDTO> pagarPedido(
            @PathVariable Long id,
            Authentication authentication) {

        PedidoDTO pedido = pedidoService.pagarPedido(id, authentication.getName());
        return ResponseEntity.ok(pedido);
    }
}
