package com.delivery.controller.publico;

import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.ProdutoDTO;
import com.delivery.service.empresa.EmpresaService;
import com.delivery.service.empresa.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publico/busca")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuscaController {

    private final EmpresaService empresaService;
    private final ProdutoService produtoService;

    @GetMapping("/empresas")
    public ResponseEntity<Page<EmpresaDTO>> listarEmpresas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaDTO> empresas = empresaService.listarEmpresas(pageable);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/empresas/termo")
    public ResponseEntity<Page<EmpresaDTO>> buscarEmpresas(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaDTO> empresas = empresaService.buscar(termo, categoriaId, pageable);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/empresas/categoria/{categoriaId}")
    public ResponseEntity<Page<EmpresaDTO>> buscarEmpresasPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaDTO> empresas = empresaService.buscarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/empresas/proximas")
    public ResponseEntity<List<EmpresaDTO>> buscarEmpresasProximas(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double raioKm) {

        List<EmpresaDTO> empresas = empresaService.buscarPorLocalizacao(latitude, longitude, raioKm);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/empresas/{id}")
    public ResponseEntity<EmpresaDTO> buscarEmpresaPorId(@PathVariable Long id) {
        EmpresaDTO empresa = empresaService.buscarPorId(id);
        return ResponseEntity.ok(empresa);
    }

    @GetMapping("/empresas/{empresaId}/produtos")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosDaEmpresa(@PathVariable Long empresaId) {
        List<ProdutoDTO> produtos = produtoService.listarProdutosPorEmpresa(empresaId);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/produtos")
    public ResponseEntity<Page<ProdutoDTO>> listarProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = produtoService.listarTodos(pageable);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/produtos/termo")
    public ResponseEntity<Page<ProdutoDTO>> buscarProdutos(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = produtoService.buscarPorTermo(termo, pageable);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/produtos/categoria/{categoriaId}")
    public ResponseEntity<Page<ProdutoDTO>> buscarProdutosPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = produtoService.buscarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(produtos);
    }
}