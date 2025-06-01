package com.delivery.service.empresa;

import com.delivery.dto.empresa.ProdutoDTO;
import com.delivery.entity.Empresa;
import com.delivery.entity.Produto;
import com.delivery.exception.BusinessException;
import com.delivery.exception.NotFoundException;
import com.delivery.repository.CategoriaRepository;
import com.delivery.repository.EmpresaRepository;
import com.delivery.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProdutoDTO> listarProdutosDaEmpresa(String emailEmpresa) {
        Empresa empresa = empresaRepository.findByEmail(emailEmpresa)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        return produtoRepository.findByEmpresaIdAndAtivoTrue(empresa.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<ProdutoDTO> listarProdutosPorEmpresa(Long empresaId) {
        return produtoRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Page<ProdutoDTO> buscarPorCategoria(Long categoriaId, Pageable pageable) {
        return produtoRepository.findByCategoriaIdAndAtivoTrue(categoriaId, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProdutoDTO> buscarPorTermo(String termo, Pageable pageable) {
        return produtoRepository.findByTermoAndAtivoTrue(termo, pageable)
                .map(this::convertToDTO);
    }

    public Page<ProdutoDTO> listarTodos(Pageable pageable) {
        return produtoRepository.findAllAtivoOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public ProdutoDTO criarProduto(ProdutoDTO produtoDTO, String emailEmpresa) {
        Empresa empresa = empresaRepository.findByEmail(emailEmpresa)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        Produto produto = new Produto();
        produto.setEmpresa(empresa);
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setImagemUrl(produtoDTO.getImagemUrl());
        produto.setEstoque(produtoDTO.getEstoque() != null ? produtoDTO.getEstoque() : 0);
        produto.setAtivo(true);

        if (produtoDTO.getCategoria() != null && produtoDTO.getCategoria().getId() != null) {
            produto.setCategoria(categoriaRepository.findById(produtoDTO.getCategoria().getId())
                    .orElseThrow(() -> new NotFoundException("Categoria não encontrada")));
        }

        produto = produtoRepository.save(produto);
        return convertToDTO(produto);
    }

    @Transactional
    public ProdutoDTO atualizarProduto(Long id, ProdutoDTO produtoDTO, String emailEmpresa) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        // Verificar se o produto pertence à empresa
        if (!produto.getEmpresa().getUsuario().getEmail().equals(emailEmpresa)) {
            throw new BusinessException("Produto não pertence à empresa");
        }

        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setImagemUrl(produtoDTO.getImagemUrl());
        produto.setEstoque(produtoDTO.getEstoque());
        produto.setAtivo(produtoDTO.getAtivo());

        produto = produtoRepository.save(produto);
        return convertToDTO(produto);
    }

    @Transactional
    public void deletarProduto(Long id, String emailEmpresa) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        // Verificar se o produto pertence à empresa
        if (!produto.getEmpresa().getUsuario().getEmail().equals(emailEmpresa)) {
            throw new BusinessException("Produto não pertence à empresa");
        }

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    private ProdutoDTO convertToDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setImagemUrl(produto.getImagemUrl());
        dto.setEstoque(produto.getEstoque());
        dto.setAtivo(produto.getAtivo());
        dto.setEmpresaId(produto.getEmpresa().getId());

        if (produto.getCategoria() != null) {
            com.delivery.dto.publico.CategoriaDTO categoriaDTO = new com.delivery.dto.publico.CategoriaDTO();
            categoriaDTO.setId(produto.getCategoria().getId());
            categoriaDTO.setNome(produto.getCategoria().getNome());
            categoriaDTO.setSlug(produto.getCategoria().getSlug());
            categoriaDTO.setIcone(produto.getCategoria().getIcone());
            dto.setCategoria(categoriaDTO);
        }

        return dto;
    }
}