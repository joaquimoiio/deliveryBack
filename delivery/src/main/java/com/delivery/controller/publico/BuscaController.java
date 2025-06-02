package com.delivery.controller.publico;

import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.ProdutoDTO;
import com.delivery.dto.publico.CategoriaDTO;
import com.delivery.service.empresa.EmpresaService;
import com.delivery.service.empresa.ProdutoService;
import com.delivery.service.publico.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publico/busca")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuscaController {

    private final EmpresaService empresaService;
    private final ProdutoService produtoService;
    private final CatalogoService catalogoService;

    // Busca geral por termo
    @GetMapping("/geral")
    public ResponseEntity<Page<EmpresaDTO>> buscarGeral(
            @RequestParam String termo,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "10") Double raioKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Se tem coordenadas, buscar por proximidade primeiro
        if (latitude != null && longitude != null) {
            List<EmpresaDTO> empresasProximas = empresaService.buscarPorLocalizacao(latitude, longitude, raioKm);
            // Filtrar por termo se necessário
            List<EmpresaDTO> empresasFiltradas = empresasProximas.stream()
                    .filter(e -> e.getNomeFantasia().toLowerCase().contains(termo.toLowerCase()))
                    .toList();

            // Converter para Page (implementação simples)
            Page<EmpresaDTO> result = new org.springframework.data.domain.PageImpl<>(
                    empresasFiltradas, pageable, empresasFiltradas.size());
            return ResponseEntity.ok(result);
        }

        // Busca padrão por termo
        Page<EmpresaDTO> empresas = empresaService.buscar(termo, null, pageable);
        return ResponseEntity.ok(empresas);
    }

    // Busca por categoria específica
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<EmpresaDTO>> buscarPorCategoria(
            @PathVariable String categoria,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "10") Double raioKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Converter categoria string para ID
        Long categoriaId = obterCategoriaIdPorSlug(categoria);
        if (categoriaId == null) {
            return ResponseEntity.ok(Page.empty());
        }

        Page<EmpresaDTO> empresas = empresaService.buscarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(empresas);
    }

    // Busca empresas próximas
    @GetMapping("/proximos")
    public ResponseEntity<List<EmpresaDTO>> buscarProximos(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double raioKm,
            @RequestParam(defaultValue = "20") int limite) {

        List<EmpresaDTO> empresas = empresaService.buscarPorLocalizacao(latitude, longitude, raioKm);

        // Limitar resultados
        List<EmpresaDTO> empresasLimitadas = empresas.stream()
                .limit(limite)
                .toList();

        return ResponseEntity.ok(empresasLimitadas);
    }

    // Busca com filtros avançados
    @PostMapping("/filtros")
    public ResponseEntity<Page<EmpresaDTO>> buscarComFiltros(
            @RequestBody Map<String, Object> filtros,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        String termo = (String) filtros.get("termo");
        Double latitude = (Double) filtros.get("latitude");
        Double longitude = (Double) filtros.get("longitude");
        Double raioKm = (Double) filtros.getOrDefault("raioMaximoKm", 10.0);
        String categoria = (String) filtros.get("categoria");

        // Se tem localização, usar busca por proximidade
        if (latitude != null && longitude != null) {
            List<EmpresaDTO> empresasProximas = empresaService.buscarPorLocalizacao(latitude, longitude, raioKm);

            // Aplicar filtros adicionais
            List<EmpresaDTO> empresasFiltradas = empresasProximas.stream()
                    .filter(e -> termo == null || e.getNomeFantasia().toLowerCase().contains(termo.toLowerCase()))
                    .filter(e -> categoria == null || e.getCategoria() != null &&
                            e.getCategoria().getSlug().equals(categoria))
                    .toList();

            Page<EmpresaDTO> result = new org.springframework.data.domain.PageImpl<>(
                    empresasFiltradas, pageable, empresasFiltradas.size());
            return ResponseEntity.ok(result);
        }

        // Busca padrão
        Long categoriaId = categoria != null ? obterCategoriaIdPorSlug(categoria) : null;
        Page<EmpresaDTO> empresas = empresaService.buscar(termo, categoriaId, pageable);
        return ResponseEntity.ok(empresas);
    }

    // Obter sugestões para autocomplete
    @GetMapping("/sugestoes")
    public ResponseEntity<List<String>> obterSugestoes(
            @RequestParam String termo,
            @RequestParam(defaultValue = "8") int limite) {

        if (termo == null || termo.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        // Buscar empresas que correspondem ao termo
        Page<EmpresaDTO> empresas = empresaService.buscar(termo, null, PageRequest.of(0, limite));

        List<String> sugestoes = empresas.getContent().stream()
                .map(EmpresaDTO::getNomeFantasia)
                .distinct()
                .limit(limite)
                .toList();

        return ResponseEntity.ok(sugestoes);
    }

    // Listar empresas (todas ativas)
    @GetMapping("/empresas")
    public ResponseEntity<Page<EmpresaDTO>> listarEmpresas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaDTO> empresas = empresaService.listarEmpresas(pageable);
        return ResponseEntity.ok(empresas);
    }

    // Buscar empresas por termo
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

    // Buscar empresas por categoria
    @GetMapping("/empresas/categoria/{categoriaId}")
    public ResponseEntity<Page<EmpresaDTO>> buscarEmpresasPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaDTO> empresas = empresaService.buscarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(empresas);
    }

    // Buscar empresas próximas
    @GetMapping("/empresas/proximas")
    public ResponseEntity<List<EmpresaDTO>> buscarEmpresasProximas(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double raioKm) {

        List<EmpresaDTO> empresas = empresaService.buscarPorLocalizacao(latitude, longitude, raioKm);
        return ResponseEntity.ok(empresas);
    }

    // Buscar empresa específica
    @GetMapping("/empresas/{id}")
    public ResponseEntity<EmpresaDTO> buscarEmpresaPorId(@PathVariable Long id) {
        EmpresaDTO empresa = empresaService.buscarPorId(id);
        return ResponseEntity.ok(empresa);
    }

    // Listar produtos de uma empresa
    @GetMapping("/empresas/{empresaId}/produtos")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosDaEmpresa(@PathVariable Long empresaId) {
        List<ProdutoDTO> produtos = produtoService.listarProdutosPorEmpresa(empresaId);
        return ResponseEntity.ok(produtos);
    }

    // Buscar produtos
    @GetMapping("/produtos")
    public ResponseEntity<Page<ProdutoDTO>> listarProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = produtoService.listarTodos(pageable);
        return ResponseEntity.ok(produtos);
    }

    // Buscar produtos por termo
    @GetMapping("/produtos/termo")
    public ResponseEntity<Page<ProdutoDTO>> buscarProdutos(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = produtoService.buscarPorTermo(termo, pageable);
        return ResponseEntity.ok(produtos);
    }

    // Buscar produtos por categoria
    @GetMapping("/produtos/categoria/{categoriaId}")
    public ResponseEntity<Page<ProdutoDTO>> buscarProdutosPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos = produtoService.buscarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(produtos);
    }

    // Estatísticas da busca
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmpresas", empresaService.contarEmpresasAtivas());
        stats.put("totalCategorias", catalogoService.contarCategorias());

        List<CategoriaDTO> categoriasPopulares = catalogoService.listarCategoriasPopulares(5);
        stats.put("categoriasPopulares", categoriasPopulares);

        return ResponseEntity.ok(stats);
    }

    // Método auxiliar para converter slug de categoria para ID
    private Long obterCategoriaIdPorSlug(String slug) {
        try {
            // Mapeamento básico de slugs para IDs
            return switch (slug.toLowerCase()) {
                case "hamburgueria" -> 4L;
                case "comida-japonesa", "japonesa" -> 5L;
                case "pizzaria" -> 3L;
                case "acai" -> 12L; // Assumindo que açaí seria ID 12
                case "bebidas" -> 13L; // Assumindo que bebidas seria ID 13
                case "restaurante" -> 1L;
                case "lanchonete" -> 2L;
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }
}