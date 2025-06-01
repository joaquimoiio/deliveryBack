package com.delivery.repository;

import com.delivery.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f WHERE f.empresa.id = :empresaId ORDER BY f.createdAt DESC")
    Page<Feedback> findByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT AVG(f.nota) FROM Feedback f WHERE f.empresa.id = :empresaId")
    Double findAvaliacaoMediaByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.empresa.id = :empresaId")
    Long countByEmpresaId(@Param("empresaId") Long empresaId);

    Optional<Feedback> findByPedidoId(Long pedidoId);

    boolean existsByPedidoId(Long pedidoId);
}