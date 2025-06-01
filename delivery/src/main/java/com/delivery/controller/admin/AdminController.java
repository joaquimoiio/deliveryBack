package com.delivery.controller.admin;

import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.publico.CategoriaDTO;
import com.delivery.service.empresa.EmpresaService;
import com.delivery.service.publico.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final EmpresaService empresaService;
    private final CatalogoService catalogoService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obterDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // Estatísticas gerais
        dashboard.put("totalEmpresas", empresaService.contarEmpresasAtivas());
        dashboard.put("totalCategorias", catalogoService.contarCategorias());

        // Categorias mais populares
        List<CategoriaDTO> categoriasPopulares = catalogoService.listarCategoriasPopulares(5);
        dashboard.put("categoriasPopulares", categoriasPopulares);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/empresas")
    public ResponseEntity<Page<EmpresaDTO>> listarEmpresas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmpresaDTO> empresas = empresaService.listarEmpresas(pageable);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        List<CategoriaDTO> categorias = catalogoService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        Map<String, Object> stats = new HashMap<>();

        // Estatísticas básicas do sistema
        stats.put("totalEmpresas", empresaService.contarEmpresasAtivas());
        stats.put("totalCategorias", catalogoService.contarCategorias());

        return ResponseEntity.ok(stats);
    }
}