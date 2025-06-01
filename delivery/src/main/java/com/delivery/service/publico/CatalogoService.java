package com.delivery.service.publico;

import com.delivery.dto.publico.CatalogoDTO;
import com.delivery.dto.publico.CategoriaDTO;
import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.ProdutoDTO;
import com.delivery.entity.Categoria;
import com.delivery.repository.CategoriaRepository;
import com.delivery.repository.EmpresaRepository;
import com.delivery.repository.ProdutoRepository;
import com.delivery.service.empresa.EmpresaService;
import com.delivery.service.empresa.ProdutoService;
import com.delivery.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoRepository produtoRepository;
    private final EmpresaService empresaService;
    private final ProdutoService produtoService;

    public List<CategoriaDTO> listarCategorias() {
        return categoriaRepository.findAllAtivasOrderByNome()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public CatalogoDTO obterCatalogo(Long categoriaId, Pageable pageable) {
        CatalogoDTO catalogo = new CatalogoDTO();

        // Listar categorias
        catalogo.setCategorias(listarCategorias());

        // Listar empresas
        if (categoriaId != null) {
            catalogo.setEmpresas(empresaService.buscarPorCategoria(categoriaId, pageable));
        } else {
            catalogo.setEmpresas(empresaService.listarEmpresas(pageable));
        }

        // Listar produtos
        if (categoriaId != null) {
            catalogo.setProdutos(produtoService.buscarPorCategoria(categoriaId, pageable));
        } else {
            catalogo.setProdutos(produtoService.listarTodos(pageable));
        }

        // Estatísticas
        catalogo.setEstatisticas(criarEstatisticasCatalogo());

        return catalogo;
    }

    public CatalogoDTO obterDestaques() {
        CatalogoDTO destaques = new CatalogoDTO();

        // Empresas em destaque (implementação básica)
        List<EmpresaDTO> empresasDestaque = empresaService.listarEmpresas(
                org.springframework.data.domain.PageRequest.of(0, 5)
        ).getContent();
        destaques.setEmpresasDestaque(empresasDestaque);

        // Produtos em destaque (implementação básica)
        List<ProdutoDTO> produtosDestaque = produtoService.listarTodos(
                org.springframework.data.domain.PageRequest.of(0, 10)
        ).getContent();
        destaques.setProdutosDestaque(produtosDestaque);

        // Categorias
        destaques.setCategorias(listarCategorias());

        return destaques;
    }

    public CatalogoDTO obterPromocoes(Pageable pageable) {
        CatalogoDTO promocoes = new CatalogoDTO();

        // Implementação básica - você pode expandir com lógica real de promoções
        promocoes.setEmpresasPromocao(List.of());
        promocoes.setProdutosPromocao(List.of());
        promocoes.setCategorias(listarCategorias());

        return promocoes;
    }

    public CatalogoDTO obterCatalogoPorCategoria(Long categoriaId, Pageable pageable) {
        return obterCatalogo(categoriaId, pageable);
    }

    public CatalogoDTO buscarNoCatalogo(String termo, Long categoriaId, Pageable pageable) {
        CatalogoDTO resultados = new CatalogoDTO();

        // Buscar empresas
        resultados.setEmpresas(empresaService.buscar(termo, categoriaId, pageable));

        // Buscar produtos
        if (categoriaId != null) {
            resultados.setProdutos(produtoService.buscarPorCategoria(categoriaId, pageable));
        } else if (termo != null && !termo.trim().isEmpty()) {
            resultados.setProdutos(produtoService.buscarPorTermo(termo, pageable));
        } else {
            resultados.setProdutos(produtoService.listarTodos(pageable));
        }

        // Categorias relacionadas
        if (termo != null && !termo.trim().isEmpty()) {
            resultados.setCategorias(buscarCategorias(termo));
        } else {
            resultados.setCategorias(listarCategorias());
        }

        return resultados;
    }

    public CatalogoDTO obterCatalogoProximo(Double latitude, Double longitude, Double raioKm, Pageable pageable) {
        CatalogoDTO catalogo = new CatalogoDTO();

        // Buscar empresas próximas
        List<EmpresaDTO> empresasProximas = empresaService.buscarPorLocalizacao(latitude, longitude, raioKm);
        catalogo.setEmpresasDestaque(empresasProximas);

        // Categorias
        catalogo.setCategorias(listarCategorias());

        return catalogo;
    }

    public CategoriaDTO buscarCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        return convertToDTO(categoria);
    }

    public CategoriaDTO buscarCategoriaPorSlug(String slug) {
        Categoria categoria = categoriaRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        return convertToDTO(categoria);
    }

    public Page<EmpresaDTO> listarEmpresasPorCategoria(Long categoriaId, Pageable pageable) {
        return empresaService.buscarPorCategoria(categoriaId, pageable);
    }

    public Page<ProdutoDTO> listarProdutosPorCategoria(Long categoriaId, Pageable pageable) {
        return produtoService.buscarPorCategoria(categoriaId, pageable);
    }

    public Map<String, Object> obterEstatisticasCategoria(Long categoriaId) {
        Map<String, Object> stats = new HashMap<>();

        // Contar empresas da categoria
        Long totalEmpresas = empresaRepository.countByCategoriaId(categoriaId);
        stats.put("totalEmpresas", totalEmpresas);

        // Contar produtos da categoria
        Long totalProdutos = produtoRepository.countByCategoriaId(categoriaId);
        stats.put("totalProdutos", totalProdutos);

        // Buscar categoria
        CategoriaDTO categoria = buscarCategoriaPorId(categoriaId);
        stats.put("categoria", categoria);

        return stats;
    }

    public List<CategoriaDTO> listarCategoriasPopulares(int limite) {
        // Implementação básica - ordenar por nome por enquanto
        // Você pode expandir com lógica de popularidade real
        return categoriaRepository.findAllAtivasOrderByNome()
                .stream()
                .limit(limite)
                .map(this::convertToDTO)
                .toList();
    }

    public List<CategoriaDTO> listarCategoriasComEmpresas() {
        // Implementação básica - todas as categorias ativas
        // Você pode implementar query específica para categorias que têm empresas
        return listarCategorias();
    }

    public List<CategoriaDTO> buscarCategorias(String termo) {
        return categoriaRepository.findAllAtivasOrderByNome()
                .stream()
                .filter(categoria -> categoria.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                        categoria.getDescricao() != null && categoria.getDescricao().toLowerCase().contains(termo.toLowerCase()))
                .map(this::convertToDTO)
                .toList();
    }

    public Object obterEstatisticasCatalogo() {
        return criarEstatisticasCatalogo();
    }

    public Long contarCategorias() {
        return categoriaRepository.count();
    }

    private CatalogoDTO.EstatisticasCatalogo criarEstatisticasCatalogo() {
        CatalogoDTO.EstatisticasCatalogo stats = new CatalogoDTO.EstatisticasCatalogo();

        // Contar totais
        stats.setTotalEmpresas(empresaRepository.count());
        stats.setTotalProdutos(produtoRepository.count());
        stats.setTotalCategorias(categoriaRepository.count());

        // Contar ativos
        Long empresasAtivas = empresaRepository.countByAtivoTrue();
        stats.setEmpresasAtivas(empresasAtivas);

        Long produtosAtivos = produtoRepository.countByAtivoTrue();
        stats.setProdutosAtivos(produtosAtivos);

        // Avaliação média geral (implementação básica)
        stats.setAvaliacaoMediaGeral(4.2); // Valor fixo por enquanto

        return stats;
    }

    private CategoriaDTO convertToDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setSlug(categoria.getSlug());
        dto.setIcone(categoria.getIcone());
        dto.setDescricao(categoria.getDescricao());
        return dto;
    }
}