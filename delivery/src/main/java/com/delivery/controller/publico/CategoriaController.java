package com.delivery.controller.publico;

import com.delivery.dto.publico.CategoriaDTO;
import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.ProdutoDTO;
import com.delivery.service.publico.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publico/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        List<CategoriaDTO> categorias = catalogoService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarCategoriaPorId(@PathVariable Long id) {
        CategoriaDTO categoria = catalogoService.buscarCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoriaDTO> buscarCategoriaPorSlug(@PathVariable String slug) {
        CategoriaDTO categoria = catalogoService.buscarCategoriaPorSlug(slug);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/{id}/empresas")
    public ResponseEntity<Page<EmpresaDTO>> listarEmpresasPorCategoria(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaDTO> empresas = catalogoService.listarEmpresasPorCategoria(id, pageable);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}/produtos")
    public ResponseEntity<Page<ProdutoDTO>> listarProdutosPorCategoria(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = catalogoService.listarProdutosPorCategoria(id, pageable);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticasCategoria(@PathVariable Long id) {
        Map<String, Object> stats = catalogoService.obterEstatisticasCategoria(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/populares")
    public ResponseEntity<List<CategoriaDTO>> listarCategoriasPopulares(
            @RequestParam(defaultValue = "10") int limite) {
        List<CategoriaDTO> categorias = catalogoService.listarCategoriasPopulares(limite);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/com-empresas")
    public ResponseEntity<List<CategoriaDTO>> listarCategoriasComEmpresas() {
        List<CategoriaDTO> categorias = catalogoService.listarCategoriasComEmpresas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaDTO>> buscarCategorias(@RequestParam String termo) {
        List<CategoriaDTO> categorias = catalogoService.buscarCategorias(termo);
        return ResponseEntity.ok(categorias);
    }
}