package com.delivery.repository;

import com.delivery.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT p FROM Produto p WHERE p.empresa.id = :empresaId AND p.ativo = true")
    List<Produto> findByEmpresaIdAndAtivoTrue(@Param("empresaId") Long empresaId);

    @Query("SELECT p FROM Produto p WHERE p.empresa.id = :empresaId")
    List<Produto> findByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT p FROM Produto p WHERE p.categoria.id = :categoriaId AND p.ativo = true")
    Page<Produto> findByCategoriaIdAndAtivoTrue(@Param("categoriaId") Long categoriaId, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE " +
            "(:termo IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))) AND " +
            "p.ativo = true")
    Page<Produto> findByTermoAndAtivoTrue(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.createdAt DESC")
    Page<Produto> findAllAtivoOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.empresa.id = :empresaId")
    Page<Produto> findByEmpresaIdPaginated(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.empresa.id = :empresaId")
    Long countByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.empresa.id = :empresaId AND p.ativo = true")
    Long countByEmpresaIdAndAtivoTrue(@Param("empresaId") Long empresaId);



    @Query("SELECT COUNT(p) FROM Produto p WHERE p.ativo = true")
    Long countByAtivoTrue();

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.categoria.id = :categoriaId")
    Long countByCategoriaId(@Param("categoriaId") Long categoriaId);
}