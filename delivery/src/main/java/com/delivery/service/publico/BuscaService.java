package com.delivery.service.publico;

import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.ProdutoDTO;
import com.delivery.dto.publico.BuscaDTO;
import com.delivery.dto.publico.CategoriaDTO;
import com.delivery.service.empresa.EmpresaService;
import com.delivery.service.empresa.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BuscaService {

    private final EmpresaService empresaService;
    private final ProdutoService produtoService;
    private final CatalogoService catalogoService;

    public Map<String, Object> buscarTudo(BuscaDTO buscaDTO) {
        Pageable pageable = PageRequest.of(
                buscaDTO.getPagina() != null ? buscaDTO.getPagina() : 0,
                buscaDTO.getTamanho() != null ? buscaDTO.getTamanho() : 20
        );

        Map<String, Object> resultados = new HashMap<>();

        // Buscar empresas
        Page<EmpresaDTO> empresas = empresaService.buscar(
                buscaDTO.getTermo(),
                buscaDTO.getCategoriaId(),
                pageable
        );
        resultados.put("empresas", empresas);

        // Buscar produtos
        Page<ProdutoDTO> produtos = null;
        if (buscaDTO.getCategoriaId() != null) {
            produtos = produtoService.buscarPorCategoria(buscaDTO.getCategoriaId(), pageable);
        } else if (buscaDTO.getTermo() != null && !buscaDTO.getTermo().trim().isEmpty()) {
            produtos = produtoService.buscarPorTermo(buscaDTO.getTermo(), pageable);
        } else {
            produtos = produtoService.listarTodos(pageable);
        }
        resultados.put("produtos", produtos);

        // Buscar categorias relacionadas
        if (buscaDTO.getTermo() != null && !buscaDTO.getTermo().trim().isEmpty()) {
            List<CategoriaDTO> categorias = catalogoService.buscarCategorias(buscaDTO.getTermo());
            resultados.put("categorias", categorias);
        }

        return resultados;
    }

    public Page<EmpresaDTO> buscarEmpresas(BuscaDTO buscaDTO) {
        Pageable pageable = PageRequest.of(
                buscaDTO.getPagina() != null ? buscaDTO.getPagina() : 0,
                buscaDTO.getTamanho() != null ? buscaDTO.getTamanho() : 20
        );

        return empresaService.buscar(buscaDTO.getTermo(), buscaDTO.getCategoriaId(), pageable);
    }

    public Page<ProdutoDTO> buscarProdutos(BuscaDTO buscaDTO) {
        Pageable pageable = PageRequest.of(
                buscaDTO.getPagina() != null ? buscaDTO.getPagina() : 0,
                buscaDTO.getTamanho() != null ? buscaDTO.getTamanho() : 20
        );

        if (buscaDTO.getCategoriaId() != null) {
            return produtoService.buscarPorCategoria(buscaDTO.getCategoriaId(), pageable);
        } else if (buscaDTO.getTermo() != null && !buscaDTO.getTermo().trim().isEmpty()) {
            return produtoService.buscarPorTermo(buscaDTO.getTermo(), pageable);
        } else {
            return produtoService.listarTodos(pageable);
        }
    }

    public List<EmpresaDTO> buscarEmpresasProximas(BuscaDTO buscaDTO) {
        if (buscaDTO.getLatitude() == null || buscaDTO.getLongitude() == null) {
            throw new IllegalArgumentException("Latitude e longitude são obrigatórias para busca por proximidade");
        }

        Double raio = buscaDTO.getRaioKm() != null ? buscaDTO.getRaioKm() : 10.0;

        return empresaService.buscarPorLocalizacao(
                buscaDTO.getLatitude(),
                buscaDTO.getLongitude(),
                raio
        );
    }

    public Map<String, Object> buscarComFiltros(BuscaDTO buscaDTO) {
        Map<String, Object> resultados = new HashMap<>();

        // Busca básica
        Map<String, Object> resultadosBasicos = buscarTudo(buscaDTO);
        resultados.putAll(resultadosBasicos);

        // Se há coordenadas, incluir busca por proximidade
        if (buscaDTO.getLatitude() != null && buscaDTO.getLongitude() != null) {
            List<EmpresaDTO> empresasProximas = buscarEmpresasProximas(buscaDTO);
            resultados.put("empresasProximas", empresasProximas);
        }

        // Estatísticas da busca
        Page<EmpresaDTO> empresas = (Page<EmpresaDTO>) resultados.get("empresas");
        Page<ProdutoDTO> produtos = (Page<ProdutoDTO>) resultados.get("produtos");

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalEmpresas", empresas.getTotalElements());
        estatisticas.put("totalProdutos", produtos.getTotalElements());
        estatisticas.put("paginaAtual", empresas.getNumber());
        estatisticas.put("totalPaginas", Math.max(empresas.getTotalPages(), produtos.getTotalPages()));

        resultados.put("estatisticas", estatisticas);

        return resultados;
    }

    public List<String> obterSugestoesBusca(String termo) {
        // Implementação básica de sugestões
        // Você pode expandir isso com um mecanismo mais sofisticado

        if (termo == null || termo.trim().length() < 2) {
            return List.of();
        }

        // Sugestões fixas para demonstração
        List<String> sugestoes = List.of(
                "pizza", "hambúrguer", "sushi", "açaí", "lanche",
                "comida brasileira", "comida italiana", "comida japonesa",
                "restaurante", "lanchonete", "pizzaria", "sorveteria"
        );

        return sugestoes.stream()
                .filter(sugestao -> sugestao.toLowerCase().contains(termo.toLowerCase()))
                .limit(10)
                .toList();
    }

    public Map<String, Object> obterEstatisticasBusca() {
        Map<String, Object> stats = new HashMap<>();

        // Estatísticas básicas
        stats.put("totalEmpresas", empresaService.contarEmpresasAtivas());
        stats.put("totalProdutos", produtoService.contarProdutosAtivos());
        stats.put("totalCategorias", catalogoService.contarCategorias());

        // Categorias mais populares
        List<CategoriaDTO> categoriasPopulares = catalogoService.listarCategoriasPopulares(5);
        stats.put("categoriasPopulares", categoriasPopulares);

        return stats;
    }

    public Map<String, Object> buscarPorTermo(String termo, int pagina, int tamanho) {
        BuscaDTO buscaDTO = new BuscaDTO();
        buscaDTO.setTermo(termo);
        buscaDTO.setPagina(pagina);
        buscaDTO.setTamanho(tamanho);

        return buscarTudo(buscaDTO);
    }

    public Map<String, Object> buscarPorCategoria(Long categoriaId, int pagina, int tamanho) {
        BuscaDTO buscaDTO = new BuscaDTO();
        buscaDTO.setCategoriaId(categoriaId);
        buscaDTO.setPagina(pagina);
        buscaDTO.setTamanho(tamanho);

        return buscarTudo(buscaDTO);
    }

    public Map<String, Object> buscarProximas(Double latitude, Double longitude, Double raioKm) {
        BuscaDTO buscaDTO = new BuscaDTO();
        buscaDTO.setLatitude(latitude);
        buscaDTO.setLongitude(longitude);
        buscaDTO.setRaioKm(raioKm);

        Map<String, Object> resultados = new HashMap<>();
        List<EmpresaDTO> empresasProximas = buscarEmpresasProximas(buscaDTO);
        resultados.put("empresasProximas", empresasProximas);
        resultados.put("coordenadas", Map.of(
                "latitude", latitude,
                "longitude", longitude,
                "raioKm", raioKm
        ));

        return resultados;
    }
}