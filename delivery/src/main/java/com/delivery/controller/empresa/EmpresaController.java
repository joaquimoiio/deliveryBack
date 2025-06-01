package com.delivery.controller.empresa;

import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.RelatorioDTO;
import com.delivery.dto.cliente.PedidoDTO;
import com.delivery.entity.enums.StatusPedido;
import com.delivery.service.empresa.EmpresaService;
import com.delivery.service.cliente.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/empresa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final PedidoService pedidoService;

    @GetMapping("/perfil")
    public ResponseEntity<EmpresaDTO> obterPerfil(Authentication authentication) {
        EmpresaDTO empresa = empresaService.buscarPorEmail(authentication.getName());
        return ResponseEntity.ok(empresa);
    }

    @GetMapping("/relatorio")
    public ResponseEntity<RelatorioDTO> obterRelatorio(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int mes,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int ano) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (ano == 0) ano = LocalDate.now().getYear();

        RelatorioDTO relatorio = empresaService.gerarRelatorio(authentication.getName(), mes, ano);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/pedidos")
    public ResponseEntity<Page<PedidoDTO>> listarPedidos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoDTO> pedidos = pedidoService.listarPedidosEmpresa(authentication.getName(), pageable);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/pedidos/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @RequestParam StatusPedido status,
            Authentication authentication) {

        PedidoDTO pedido = pedidoService.atualizarStatus(id, status, authentication.getName());
        return ResponseEntity.ok(pedido);
    }

    // REMOVIDOS: Os métodos de produtos foram removidos daqui pois já existem em ProdutoEmpresaController
    // Isso evita conflitos de mapeamento:
    // - listarProdutos() -> ProdutoEmpresaController
    // - criarProduto() -> ProdutoEmpresaController
    // - atualizarProduto() -> ProdutoEmpresaController
    // - deletarProduto() -> ProdutoEmpresaController
}