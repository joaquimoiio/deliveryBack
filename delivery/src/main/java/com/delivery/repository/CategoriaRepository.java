package com.delivery.repository;

import com.delivery.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findBySlug(String slug);

    @Query("SELECT c FROM Categoria c WHERE c.ativo = true ORDER BY c.nome")
    List<Categoria> findAllAtivasOrderByNome();

    boolean existsByNome(String nome);
    boolean existsBySlug(String slug);

    @Query("SELECT COUNT(c) FROM Categoria c WHERE c.ativo = true")
    Long countByAtivoTrue();

    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.empresas e WHERE e.ativo = true")
    List<Categoria> findCategoriasComEmpresas();

    List<Categoria> findByNomeContainingIgnoreCase(String nome);
}