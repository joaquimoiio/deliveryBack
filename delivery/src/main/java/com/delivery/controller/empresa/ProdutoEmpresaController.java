package com.delivery.controller.empresa;

import com.delivery.dto.empresa.ProdutoDTO;
import com.delivery.service.empresa.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/empresa/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProdutoEmpresaController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarProdutos(Authentication authentication) {
        List<ProdutoDTO> produtos = produtoService.listarProdutosDaEmpresa(authentication.getName());
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<ProdutoDTO>> listarProdutosPaginado(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = produtoService.listarProdutosDaEmpresaPaginado(
                authentication.getName(), pageable);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarProdutoPorId(
            @PathVariable Long id,
            Authentication authentication) {

        ProdutoDTO produto = produtoService.buscarProdutoDaEmpresa(id, authentication.getName());
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> criarProduto(
            @Valid @RequestBody ProdutoDTO produtoDTO,
            Authentication authentication) {

        ProdutoDTO produto = produtoService.criarProduto(produtoDTO, authentication.getName());
        return ResponseEntity.ok(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizarProduto(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO,
            Authentication authentication) {

        ProdutoDTO produto = produtoService.atualizarProduto(id, produtoDTO, authentication.getName());
        return ResponseEntity.ok(produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(
            @PathVariable Long id,
            Authentication authentication) {

        produtoService.deletarProduto(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<ProdutoDTO> ativarProduto(
            @PathVariable Long id,
            Authentication authentication) {

        ProdutoDTO produto = produtoService.alternarStatusProduto(id, true, authentication.getName());
        return ResponseEntity.ok(produto);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<ProdutoDTO> desativarProduto(
            @PathVariable Long id,
            Authentication authentication) {

        ProdutoDTO produto = produtoService.alternarStatusProduto(id, false, authentication.getName());
        return ResponseEntity.ok(produto);
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<ProdutoDTO> atualizarEstoque(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {

        Integer novoEstoque = request.get("estoque");
        ProdutoDTO produto = produtoService.atualizarEstoque(id, novoEstoque, authentication.getName());
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticasProdutos(Authentication authentication) {
        Map<String, Object> stats = produtoService.obterEstatisticasProdutos(authentication.getName());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/baixo-estoque")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosBaixoEstoque(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limite) {

        List<ProdutoDTO> produtos = produtoService.listarProdutosBaixoEstoque(
                authentication.getName(), limite);
        return ResponseEntity.ok(produtos);
    }
}