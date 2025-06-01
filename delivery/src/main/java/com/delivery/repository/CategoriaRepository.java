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

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Categoria c WHERE c.nome = ?1")
    boolean existsByNome(String nome);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Categoria c WHERE c.slug = ?1")
    boolean existsBySlug(String slug);

    @Query("SELECT COUNT(c) FROM Categoria c WHERE c.ativo = true")
    Long countByAtivoTrue();

    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.empresas e WHERE e.ativo = true")
    List<Categoria> findCategoriasComEmpresas();

    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Categoria> findByNomeContainingIgnoreCase(String nome);
}
